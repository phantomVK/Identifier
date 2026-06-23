package com.android.id.impl;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Keep;

@Keep
public class IdProviderImpl {
    /**
     * AAID For Xiaomi
     */
    public String getAAID(Context context) {
        return null;
    }

    /**
     * OAID For Xiaomi
     */
    public String getOAID(Context context) {
        return null;
    }

    /**
     * UDID For Xiaomi
     */
    public String getUDID(Context context) {
        return null;
    }

    /**
     * VAID For Xiaomi
     */
    public String getVAID(Context context) {
        return null;
    }

    /**
     * APID For Oppo
     */
    public String getAPID(Context context) {
        return null;
    }

    /**
     * UDID For Oppo
     */
    public String getGUID(Context context) {
        return null;
    }

    /**
     * OAID For OPPO
     */
    public String getOUID(Context context) {
        return null;
    }

    /**
     * VAID For Oppo
     */
    public String getDUID(Context context) {
        return null;
    }

    /**
     * AAID For Oppo
     */
    public String getAUID(Context context) {
        return null;
    }

    /**
     * OpenId for Oppo
     */
    public String getOpenid(Context context, String appId) {
        return null;
    }

    /**
     * StdId for Oppo
     */
    public String getStdid(Context context, String appId) {
        return null;
    }

    /**
     * checkSelfOAIDPermission for Oppo
     *
     * @return 0:enable -1:disable
     */
    public static int checkSelfOAIDPermission(Context context) {
        return 0;
    }

    /**
     * requestOAIDPermission for Oppo, show only once.
     */
    public static void requestOAIDPermission(Activity activity, int flag) {
    }
}
