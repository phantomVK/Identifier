package com.phantomvk.identifier.impl;

import androidx.annotation.NonNull;

import com.phantomvk.identifier.disposable.Disposable;
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
        conf.callback = new WeakReference<>(callback);
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
        // post the runnable to the executor even on the async thread.
        SerialRunnable runnable = new SerialRunnable(conf);
        conf.getExecutor().execute(runnable);
        return runnable;
    }
}