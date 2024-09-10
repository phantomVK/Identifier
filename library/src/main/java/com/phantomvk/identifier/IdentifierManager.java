package com.phantomvk.identifier;

import android.content.Context;

import androidx.annotation.NonNull;

import com.phantomvk.identifier.impl.Subscription;
import com.phantomvk.identifier.interfaces.OnResultListener;
import com.phantomvk.identifier.log.Log;
import com.phantomvk.identifier.log.Logger;
import com.phantomvk.identifier.model.ProviderConfig;

import java.util.concurrent.Executor;

public final class IdentifierManager {
    private static volatile IdentifierManager sInstance = null;

    private Context context = null;
    private Executor executor = null;
    private boolean isDebug = false;
    private boolean isExperimental = false;
    private boolean isGoogleAdsIdEnabled = false;
    private boolean isLimitAdTracking = false;
    private boolean isMemCacheEnabled = false;

    private IdentifierManager() {
    }

    @NonNull
    public static IdentifierManager getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("Should init first.");
        }

        return sInstance;
    }

    public Subscription setSubscriber(@NonNull OnResultListener callback) {
        ProviderConfig config = new ProviderConfig(context);
        config.setDebug(isDebug);
        config.setExecutor(executor);
        config.setExperimental(isExperimental);
        config.setGoogleAdsIdEnabled(isGoogleAdsIdEnabled);
        config.setLimitAdTracking(isLimitAdTracking);
        config.setMemCacheEnabled(isMemCacheEnabled);
        return new Subscription(config, callback);
    }

    public static class Builder {
        private final Context context;
        private Logger logger = null;
        private Executor executor = null;
        private boolean isDebug = false;
        private boolean isExperimental = false;
        private boolean isGoogleAdsIdEnabled = false;
        private boolean isLimitAdTracking = false;
        private boolean isMemCacheEnabled = false;

        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("Context should not be null.");
            }

            this.context = context.getApplicationContext();
        }

        @NonNull
        public Builder setDebug(boolean enable) {
            isDebug = enable;
            return this;
        }

        @NonNull
        public Builder setExperimental(boolean enable) {
            isExperimental = enable;
            return this;
        }

        @NonNull
        public Builder setGoogleAdsIdEnable(boolean enable){
            isGoogleAdsIdEnabled = enable;
            return this;
        }

        @NonNull
        public Builder setLimitAdTracking(boolean enable) {
            isLimitAdTracking = enable;
            return this;
        }

        @NonNull
        public Builder setMemCacheEnable(boolean enable) {
            isMemCacheEnabled = enable;
            return this;
        }

        @NonNull
        public Builder setLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        @NonNull
        public Builder setExecutor(@NonNull Executor executor) {
            this.executor = executor;
            return this;
        }

        public void init() {
            synchronized (Builder.class) {
                if (sInstance == null) {
                    // set logger
                    Log.setLogger(logger);

                    // init
                    IdentifierManager manager = new IdentifierManager();
                    manager.context = context.getApplicationContext();
                    manager.executor = executor == null ? c -> new Thread(c).start() : executor;
                    manager.isDebug = isDebug;
                    manager.isExperimental = isExperimental;
                    manager.isGoogleAdsIdEnabled = isGoogleAdsIdEnabled;
                    manager.isLimitAdTracking = isLimitAdTracking;
                    manager.isMemCacheEnabled = isMemCacheEnabled;
                    sInstance = manager;
                } else {
                    throw new RuntimeException("Should not init twice.");
                }
            }
        }
    }
}
