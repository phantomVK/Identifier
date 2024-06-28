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

    private Executor executor = null;
    private boolean memCacheEnabled = false;
    private boolean isDebug = false;

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

    public boolean isMemCacheEnabled() {
        return memCacheEnabled;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public TaskBuilder create(
            @NonNull Context context,
            @NonNull OnResultListener callback
    ) {
        return new TaskBuilder(context.getApplicationContext(), callback);
    }

    public static class Builder {
        private Logger logger = null;
        private Executor executor = null;
        private boolean memCacheEnabled = false;
        private boolean isDebug = false;

        public Builder() {
        }

        @NonNull
        public Builder isDebug(boolean debug) {
            isDebug = debug;
            return this;
        }

        @NonNull
        public Builder setMemCacheEnable(boolean enable) {
            this.memCacheEnabled = enable;
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

        public synchronized void init() {
            if (sInstance == null) {
                // set logger
                Log.setLogger(logger);

                // init
                IdentifierManager manager = new IdentifierManager();
                manager.memCacheEnabled = memCacheEnabled;
                manager.executor = executor;
                manager.isDebug = isDebug;
                sInstance = manager;
            } else {
                throw new RuntimeException("Should not init twice.");
            }
        }
    }
}
