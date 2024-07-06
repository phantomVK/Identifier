package generated.com.coolpad.deviceidsupport;

interface IDeviceIdManager {
    String getUDID(String str);
    String getOAID(String str);
    String getVAID(String str);
    String getAAID(String str);
    String getIMEI(String str);
    boolean isCoolOs();
    String getCoolOsVersion();
}