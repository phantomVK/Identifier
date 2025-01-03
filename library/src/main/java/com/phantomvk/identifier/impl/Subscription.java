package com.phantomvk.identifier.impl;

import androidx.annotation.NonNull;

import com.phantomvk.identifier.disposable.Disposable;
import com.phantomvk.identifier.listener.OnResultListener;
import com.phantomvk.identifier.model.ProviderConfig;

import java.lang.ref.WeakReference;

public class Subscription {
    private final ProviderConfig conf;

    public Subscription(ProviderConfig config, OnResultListener callback) {
        conf = config.clone();
        conf.callback = new WeakReference<>(callback);
    }

    @NonNull
    public Subscription enableAsyncCallback(boolean enable) {
        conf.setAsyncCallback(enable);
        return this;
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
        SerialRunnable runnable = new SerialRunnable(conf);
        runnable.run();
        return runnable;
    }
}