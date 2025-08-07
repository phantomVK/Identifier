package android.app;

import android.content.Context;

import androidx.annotation.Keep;

@Keep
public class ZteDeviceIdentifyManager {
    public ZteDeviceIdentifyManager(Context context) {
    }

    public boolean isSupported(Context context) {
        return false;
    }

    public String getOAID(Context context) {
        return null;
    }

    public String getAAID(Context context) {
        return null;
    }

    public String getVAID(Context context) {
        return null;
    }
}
