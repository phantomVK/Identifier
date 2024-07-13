package com.phantomvk.identifier.impl;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.phantomvk.identifier.IdentifierManager;
import com.phantomvk.identifier.interfaces.Disposable;
import com.phantomvk.identifier.interfaces.OnResultListener;
import com.phantomvk.identifier.model.ProviderConfig;
import com.phantomvk.identifier.util.MainThreadKt;

import java.util.concurrent.Executor;

public class TaskBuilder {
    private volatile static String cachedId = null;
    private final ProviderConfig config;

    public TaskBuilder(Context context, OnResultListener callback) {
        config = new ProviderConfig(context, getWrappedCallback(callback));
    }

    private OnResultListener getWrappedCallback(OnResultListener callback) {
        if (!IdentifierManager.getInstance().isMemCacheEnabled()) {
            return callback;
        }

        return new OnResultListener() {
            @Override
            public void onSuccess(@NonNull String id) {
                cachedId = id;
                callback.onSuccess(id);
            }

            @Override
            public void onError(@NonNull String msg, @Nullable Throwable t) {
                callback.onError(msg, t);
            }
        };
    }

    @NonNull
    public TaskBuilder setLimitAdTracking(boolean enable) {
        config.setLimitAdTracking(enable);
        return this;
    }

    @NonNull
    public Disposable start() {
        // cachedId is always null when cache is disabled.
        String id = cachedId;
        if (!TextUtils.isEmpty(id)) {
            MainThreadKt.runOnMainThread(0, () -> config.getCallback().onSuccess(id));

            // In order to return a non-null object.
            return new Disposable() {
                @Override
                public void dispose() {
                }

                @Override
                public boolean isDisposed() {
                    return true;
                }
            };
        }

        Executor executor = IdentifierManager.getInstance().getExecutor();
        SerialRunnable runnable = new SerialRunnable(config);

        // no available executor found.
        if (executor == null) {
            new Thread(runnable).start();
            return runnable;
        }

        Runnable runnableWrapper = () -> {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                if (IdentifierManager.getInstance().isDebug()) {
                    throw new RuntimeException("Do not execute runnable on the main thread.");
                } else {
                    new Thread(runnable).start();
                }
            } else {
                runnable.run();
            }
        };

        // post the runnable to the executor even on the async thread.
        executor.execute(runnableWrapper);
        return runnable;
    }
}