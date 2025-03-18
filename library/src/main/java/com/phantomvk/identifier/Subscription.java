package com.phantomvk.identifier;

import androidx.annotation.NonNull;

import com.phantomvk.identifier.disposable.Disposable;
import com.phantomvk.identifier.functions.Consumer;
import com.phantomvk.identifier.internal.SerialRunnable;
import com.phantomvk.identifier.model.IdConfig;
import com.phantomvk.identifier.model.MemoryConfig;
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
    public Subscription enableExperimental(boolean enable) {
        conf.setExperimental(enable);
        return this;
    }

    @NonNull
    public Subscription enableVerifyLimitAdTracking(boolean enable) {
        conf.setVerifyLimitAdTracking(enable);
        return this;
    }

    @NonNull
    public Subscription setIdConfig(@NonNull IdConfig idConfig) {
        conf.setIdConfig(idConfig);
        return this;
    }

    @NonNull
    public Subscription setMemoryConfig(@NonNull MemoryConfig memoryConfig) {
        conf.setMemoryConfig(memoryConfig);
        return this;
    }

    @NonNull
    public Disposable subscribe(@NonNull Consumer consumer) {
        ProviderConfig conf = this.conf.clone();
        conf.consumer = new WeakReference<>(consumer);
        SerialRunnable runnable = new SerialRunnable(conf);
        runnable.run();
        return runnable;
    }
}