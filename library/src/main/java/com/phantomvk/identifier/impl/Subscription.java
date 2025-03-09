package com.phantomvk.identifier.impl;

import androidx.annotation.NonNull;

import com.phantomvk.identifier.disposable.Disposable;
import com.phantomvk.identifier.listener.OnResultListener;
import com.phantomvk.identifier.model.ProviderConfig;

import java.lang.ref.WeakReference;

public class Subscription {
    private final ProviderConfig conf;

    public Subscription(ProviderConfig config) {
        conf = config.clone();
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
    public Subscription enableExperimental(boolean enable) {
        conf.setExperimental(enable);
        return this;
    }

    @NonNull
    public Subscription enableMemCache(boolean enable) {
        conf.setMemCacheEnabled(enable);
        return this;
    }

    @NonNull
    public Subscription enableVerifyLimitAdTracking(boolean enable) {
        conf.setVerifyLimitAdTracking(enable);
        return this;
    }

    @NonNull
    public Disposable subscribe(@NonNull OnResultListener callback) {
        conf.callback = new WeakReference<>(callback);
        SerialRunnable runnable = new SerialRunnable(conf);
        runnable.run();
        return runnable;
    }
}