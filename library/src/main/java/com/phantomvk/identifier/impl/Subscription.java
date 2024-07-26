package com.phantomvk.identifier.impl;

import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.phantomvk.identifier.interfaces.Disposable;
import com.phantomvk.identifier.interfaces.OnResultListener;
import com.phantomvk.identifier.model.ProviderConfig;
import com.phantomvk.identifier.util.MainThreadKt;

public class Subscription {
    private volatile static String cachedId = null;
    private final ProviderConfig config;

    public Subscription(ProviderConfig config, OnResultListener callback) {
        this.config = config;
        config.callback = config.isMemCacheEnabled() ? getWrappedCallback(callback) : callback;
    }

    private OnResultListener getWrappedCallback(OnResultListener callback) {
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
    public Disposable subscribe() {
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

        // post the runnable to the executor even on the async thread.
        SerialRunnable runnable = new SerialRunnable(config);
        config.getExecutor().execute(runnable);
        return runnable;
    }
}