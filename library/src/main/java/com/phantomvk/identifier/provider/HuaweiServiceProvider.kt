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

  override fun run() {
    val intent = Intent("com.uodis.opendevice.OPENIDS_SERVICE").setPackage(name)
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isLimitAdTracking) {
          if (isOaidTrackLimited(binder)) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return checkId(getOaid(binder))
      }
    })
  }

  private fun getOaid(remote: IBinder): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val result: String?
    try {
      data.writeInterfaceToken("com.uodis.opendevice.aidl.OpenDeviceIdentifierService")
      remote.transact(1, data, reply, 0)
      reply.readException()
      result = reply.readString()
    } finally {
      reply.recycle()
      data.recycle()
    }
    return result
  }

  private fun isOaidTrackLimited(remote: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val result: Boolean
    try {
      data.writeInterfaceToken("com.uodis.opendevice.aidl.OpenDeviceIdentifierService")
      remote.transact(2, data, reply, 0)
      reply.readException()
      result = (0 != reply.readInt())
    } finally {
      reply.recycle()
      data.recycle()
    }
    return result
  }
}
