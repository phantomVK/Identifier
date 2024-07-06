package generated.com.qiku.id;

// Qiku: bindService
interface IOAIDInterface {
    int isSupported();
    String getUDID();
    String getOAID();
    String getVAID();
    String getAAID();
    void shutdown();
    void resetOAID();
    boolean isLimited();
}