package generated.com.zui.deviceidservice;

interface IDeviceidInterface {
    String getOAID();
    String getUDID();
    boolean isSupport();
    String getVAID(String str);
    String getAAID(String str);
    String createAAIDForPackageName(String str);
}