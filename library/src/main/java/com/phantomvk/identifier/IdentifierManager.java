package com.phantomvk.identifier;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.phantomvk.identifier.impl.TaskBuilder;
import com.phantomvk.identifier.interfaces.OnResultListener;
import com.phantomvk.identifier.log.Log;
import com.phantomvk.identifier.log.Logger;

import java.util.concurrent.Executor;

public final class IdentifierManager {
    private static volatile IdentifierManager sInstance = null;

    private Context context = null;
    private Executor executor = null;
    private boolean isDebug = false;
    private boolean isExperimental = false;
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

    @Nullable
    public Executor getExecutor() {
        return executor;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isExperimental() {
        return isExperimental;
    }

    public boolean isMemCacheEnabled() {
        return isMemCacheEnabled;
    }

    public TaskBuilder create(@NonNull OnResultListener callback) {
        return new TaskBuilder(context, callback);
    }

    public static class Builder {
        private final Context context;
        private Logger logger = null;
        private Executor executor = null;
        private boolean isDebug = false;
        private boolean isExperimental = false;
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
                    manager.isMemCacheEnabled = isMemCacheEnabled;
                    manager.executor = executor;
                    manager.isDebug = isDebug;
                    manager.isExperimental = isExperimental;
                    sInstance = manager;
                } else {
                    throw new RuntimeException("Should not init twice.");
                }
            }
        }
    }
}
