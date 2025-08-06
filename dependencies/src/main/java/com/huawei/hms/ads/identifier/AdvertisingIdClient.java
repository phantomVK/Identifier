package com.huawei.hms.ads.identifier;

import android.content.Context;

public class AdvertisingIdClient {

    public static boolean isAdvertisingIdAvailable(Context context) {
        return false;
    }

    public static Info getAdvertisingIdInfo(Context context) {
        return null;
    }

    public static final class Info {
        private final String advertisingId;
        private final boolean limitAdTrackingEnabled;

        public Info(String var1, boolean var2) {
            this.advertisingId = var1;
            this.limitAdTrackingEnabled = var2;
        }

        public String getId() {
            return null;
        }

        public boolean isLimitAdTrackingEnabled() {
            return false;
        }
    }
}
