# Huawei HMS SDK
-dontwarn com.huawei.hms.ads.**
-keep class com.huawei.hms.ads.** {*; }
-keep interface com.huawei.hms.ads.** {*; }

# Honor MCS SDK
-dontwarn com.hihonor.ads.identifier.**
-keeppackagenames com.hihonor.ads.identifier
-keeppackagenames com.hihonor.cloudservice.oaid
-keep class com.hihonor.ads.identifier.AdvertisingIdClient*{*;}