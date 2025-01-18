package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig

internal class HuaweiServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  private val name by lazy(LazyThreadSafetyMode.NONE) {
    listOf(
      "com.huawei.hwid",
      "com.huawei.hwid.tv",
      "com.huawei.hms"
    ).firstOrNull { isPackageInfoExisted(it) }
  }

  override fun isSupported(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return false
    }

    return name != null
  }

  override fun getInterfaceName(): String {
    return "com.uodis.opendevice.aidl.OpenDeviceIdentifierService"
  }

  override fun run() {
    val intent = Intent("com.uodis.opendevice.OPENIDS_SERVICE").setPackage(name)
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isLimitAdTracking) {
          if (isOaidTrackLimited(binder)) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return getId(binder, 1)
      }
    })
  }

  private fun isOaidTrackLimited(remote: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.uodis.opendevice.aidl.OpenDeviceIdentifierService")
      remote.transact(2, data, reply, 0)
      reply.readException()
      return 0 != reply.readInt()
    } catch (t: Throwable) {
      return false
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}
