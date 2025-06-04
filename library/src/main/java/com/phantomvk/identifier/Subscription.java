package com.phantomvk.identifier;


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

    public Subscription enableAsyncCallback(boolean enable) {
        conf.setAsyncCallback(enable);
        return this;
    }

    public Subscription enableExperimental(boolean enable) {
        conf.setExperimental(enable);
        return this;
    }

    public Subscription enableExternalSdkQuerying(boolean enable) {
        conf.setExternalSdkQuerying(enable);
        return this;
    }

    public Subscription enableVerifyLimitAdTracking(boolean enable) {
        conf.setVerifyLimitAdTracking(enable);
        return this;
    }

    public Subscription setIdConfig(IdConfig idConfig) {
        conf.setIdConfig(idConfig);
        return this;
    }

    public Subscription setMemoryConfig(MemoryConfig memoryConfig) {
        conf.setMemoryConfig(memoryConfig);
        return this;
    }

    public Disposable subscribe(Consumer consumer) {
        ProviderConfig conf = this.conf.clone();
        conf.consumer = new WeakReference<>(consumer);
        SerialRunnable runnable = new SerialRunnable(conf);
        runnable.run();
        return runnable;
    }
}