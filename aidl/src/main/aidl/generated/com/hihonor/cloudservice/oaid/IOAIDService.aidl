package generated.com.hihonor.cloudservice.oaid;

import generated.com.hihonor.cloudservice.oaid.IOAIDCallBack;

interface IOAIDService {
    void a(int i, long j, boolean z, float f, double d, String str);
    void getOaid(IOAIDCallBack callback);
    void isLimited(IOAIDCallBack callback);
}