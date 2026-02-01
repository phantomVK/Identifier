package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig

internal class HuaweiServiceProvider(config: ProviderConfig) : HuaweiBaseProvider(config) {

  private lateinit var packageName: String

  override fun isSupported(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return false
    }

    val name = listOf(
      "com.huawei.hwid",
      "com.huawei.hwid.tv",
      "com.huawei.hms"
    ).firstOrNull { try { isPackageInfoExisted(it) } catch (t: Throwable) { false } }

    if (name == null) {
      return false
    }

    packageName = name
    return true
  }

  override fun getInterfaceName(): String {
    return "com.uodis.opendevice.aidl.OpenDeviceIdentifierService"
  }

  override fun run() {
    val intent = Intent("com.uodis.opendevice.OPENIDS_SERVICE").setPackage(packageName)
    bindService(intent)
  }

  override fun call(binder: IBinder): BinderResult {
    if (config.isVerifyLimitAdTracking) {
      if (readBoolean(binder, 2, false, null)) {
        return Failed(LIMIT_AD_TRACKING_IS_ENABLED)
      }
    }

    return when (val r = getId(binder, 1)) {
      is Failed -> r
      is Success -> Success(r.id, getVAID(), getAAID())
    }
  }
}
