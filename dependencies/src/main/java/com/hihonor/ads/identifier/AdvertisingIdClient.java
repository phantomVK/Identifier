package com.hihonor.ads.identifier;

import android.content.Context;

public class AdvertisingIdClient {

    public static Info getAdvertisingIdInfo(Context context) {
        return null;
    }

    public static boolean isAdvertisingIdAvailable(Context context) {
        return false;
    }

    public static final class Info {
        public String id;
        public boolean isLimit;

        public Info() {
        }
    }
}
