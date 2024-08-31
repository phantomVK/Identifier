package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig

internal class ZuiProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.zui.deviceidservice")
  }

  override fun run() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        if (config.isLimitAdTracking) {
          if (!isSupport(binder)) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return checkId(getOAID(binder))
      }
    }

    val intent = Intent().setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService")
    bindService(intent, binderCallback)
  }

  private fun getOAID(remote: IBinder): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val result: String?
    try {
      data.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
      remote.transact(1, data, reply, 0)
      reply.readException()
      result = reply.readString()
    } finally {
      reply.recycle()
      data.recycle()
    }
    return result
  }

  private fun isSupport(remote: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val result: Boolean
    try {
      data.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
      remote.transact(3, data, reply, 0)
      reply.readException()
      result = (0 != reply.readInt())
    } finally {
      reply.recycle()
      data.recycle()
    }
    return result
  }
}