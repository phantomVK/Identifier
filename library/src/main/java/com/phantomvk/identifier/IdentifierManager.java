package com.phantomvk.identifier;

import android.content.Context;

import androidx.annotation.NonNull;

import com.phantomvk.identifier.impl.Subscription;
import com.phantomvk.identifier.listener.OnResultListener;
import com.phantomvk.identifier.log.Log;
import com.phantomvk.identifier.log.Logger;
import com.phantomvk.identifier.model.ProviderConfig;

import java.util.concurrent.ExecutorService;

public final class IdentifierManager {
    private static volatile IdentifierManager sInstance = null;
    private final ProviderConfig config;

    private IdentifierManager(ProviderConfig config) {
        this.config = config;
    }

    @NonNull
    public static IdentifierManager getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("Should init first.");
        }

        return sInstance;
    }

    @NonNull
    public Subscription setSubscriber(@NonNull OnResultListener callback) {
        return new Subscription(config, callback);
    }

    public static class Builder {
        private Logger logger = null;
        private final ProviderConfig config;

        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("Context should not be null.");
            }

            config = new ProviderConfig(context.getApplicationContext());
        }

        @NonNull
        public Builder setDebug(boolean enable) {
            config.setDebug(enable);
            return this;
        }

        @NonNull
        public Builder setExperimental(boolean enable) {
            config.setExperimental(enable);
            return this;
        }

        @NonNull
        public Builder setLimitAdTracking(boolean enable) {
            config.setLimitAdTracking(enable);
            return this;
        }

        @NonNull
        public Builder setMemCacheEnable(boolean enable) {
            config.setMemCacheEnabled(enable);
            return this;
        }

        @NonNull
        public Builder setLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        @NonNull
        public Builder setExecutor(@NonNull ExecutorService executor) {
            config.setExecutor(executor);
            return this;
        }

        public void init() {
            synchronized (Builder.class) {
                if (sInstance == null) {
                    Log.setLogger(logger);
                    sInstance = new IdentifierManager(config);
                } else {
                    throw new RuntimeException("Should not init twice.");
                }
            }
        }
    }
}
