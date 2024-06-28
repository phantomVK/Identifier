package generated.com.bun.lib;

interface MsaIdInterface {
    String getOAID();
    String getAAID();
    String getVAID();
    boolean isDataArrived();
    boolean isSupported();
    void shutDown();
}