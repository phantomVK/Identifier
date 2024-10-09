package com.phantomvk.identifier.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.phantomvk.identifier.interfaces.Disposable;
import com.phantomvk.identifier.interfaces.OnResultListener;
import com.phantomvk.identifier.model.IdentifierResult;
import com.phantomvk.identifier.model.ProviderConfig;

import java.lang.ref.WeakReference;

public class Subscription {
    private volatile static IdentifierResult cachedResult = null;
    private final ProviderConfig config;

    public Subscription(ProviderConfig config, OnResultListener callback) {
        this.config = config;
        OnResultListener l = config.isMemCacheEnabled() ? new CacheResultListener(callback) : callback;
        config.callback = new WeakReference<>(l);
    }

    @NonNull
    public Disposable subscribe() {
        // cachedId is always null when cache is disabled.
        IdentifierResult result = cachedResult;
        if (result == null) {
            // post the runnable to the executor even on the async thread.
            SerialRunnable runnable = new SerialRunnable(config);
            config.getExecutor().execute(runnable);
            return runnable;
        } else {
            OnResultListener callback = config.getCallback().get();
            if (callback != null) {
                Thread.runOnMainThread(0, () -> callback.onSuccess(result));
            }

            // In order to return a non-null object.
            return new DisposedDisposable();
        }
    }

    private static final class CacheResultListener implements OnResultListener {
        private final OnResultListener listener;

        private CacheResultListener(OnResultListener listener) {
            this.listener = listener;
        }

        @Override
        public void onSuccess(@NonNull IdentifierResult result) {
            cachedResult = result;
            listener.onSuccess(result);
        }

        @Override
        public void onError(@NonNull String msg, @Nullable Throwable t) {
            listener.onError(msg, t);
        }
    }
}