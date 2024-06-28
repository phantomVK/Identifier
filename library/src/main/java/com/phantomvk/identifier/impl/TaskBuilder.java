package com.phantomvk.identifier.impl;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.phantomvk.identifier.IdentifierManager;
import com.phantomvk.identifier.interfaces.Disposable;
import com.phantomvk.identifier.interfaces.OnResultListener;
import com.phantomvk.identifier.model.ProviderConfig;

import java.util.concurrent.Executor;

public class TaskBuilder {
    private final ProviderConfig config;

    public TaskBuilder(Context context, OnResultListener callback) {
        config = new ProviderConfig(context, callback);
    }

    @NonNull
    public TaskBuilder setLimitAdTracking(boolean enable) {
        config.setLimitAdTracking(enable);
        return this;
    }

    @NonNull
    public Disposable start() {
        SerialRunnable runnable = new SerialRunnable(config);

        // no custom executor.
        Executor executor = IdentifierManager.getInstance().getExecutor();
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
                runnable.execute();
            }
        };

        executor.execute(runnableWrapper);
        return runnable;
    }
}