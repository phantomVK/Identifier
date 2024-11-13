package com.phantomvk.identifier.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.phantomvk.identifier.disposable.Disposable;
import com.phantomvk.identifier.disposable.DisposedDisposable;
import com.phantomvk.identifier.listener.OnResultListener;
import com.phantomvk.identifier.model.IdentifierResult;
import com.phantomvk.identifier.model.ProviderConfig;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

public class Subscription {
    private final static ConcurrentHashMap<String, IdentifierResult> cache = new ConcurrentHashMap<>();
    private final ProviderConfig conf;

    public Subscription(ProviderConfig config, OnResultListener callback) {
        conf = config.clone();
        OnResultListener l = conf.isMemCacheEnabled() ? new CacheResultListener(conf.getCacheKey(), callback) : callback;
        conf.callback = new WeakReference<>(l);
    }

    @NonNull
    public Subscription enableAaid(boolean enable) {
        conf.setQueryAaid(enable);
        return this;
    }

    @NonNull
    public Subscription enableVaid(boolean enable) {
        conf.setQueryVaid(enable);
        return this;
    }

    @NonNull
    public Subscription enableGoogleAdsId(boolean enable) {
        conf.setQueryGoogleAdsId(enable);
        return this;
    }

    @NonNull
    public Disposable subscribe() {
        // cachedId is always null when cache is disabled.
        IdentifierResult result = cache.get(conf.getCacheKey());
        if (result == null) {
            // post the runnable to the executor even on the async thread.
            SerialRunnable runnable = new SerialRunnable(conf);
            conf.getExecutor().execute(runnable);
            return runnable;
        } else {
            OnResultListener callback = conf.getCallback().get();
            if (callback != null) {
                Thread.runOnMainThread(0, () -> callback.onSuccess(result));
            }

            // In order to return a non-null object.
            return new DisposedDisposable();
        }
    }

    private static final class CacheResultListener implements OnResultListener {
        private final String cacheKey;
        private final OnResultListener listener;

        private CacheResultListener(String cacheKey, OnResultListener listener) {
            this.cacheKey = cacheKey;
            this.listener = listener;
        }

        @Override
        public void onSuccess(@NonNull IdentifierResult result) {
            cache.put(cacheKey, result);
            listener.onSuccess(result);
        }

        @Override
        public void onError(@NonNull String msg, @Nullable Throwable t) {
            listener.onError(msg, t);
        }
    }
}