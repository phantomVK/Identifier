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

        return when (val r = checkId(getId(binder, 1))) {
          is CallBinderResult.Failed -> r
          is CallBinderResult.Success -> {
            val vaid = if (config.queryVaid) getId(binder, 4) else null
            val aaid = if (config.queryAaid) getId(binder, 5) else null
            CallBinderResult.Success(r.id, vaid, aaid)
          }
        }
      }
    }

    val intent = Intent().setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService")
    bindService(intent, binderCallback)
  }

  private fun getId(remote: IBinder, code: Int): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
      data.writeString(config.context.packageName)
      remote.transact(code, data, reply, 0)
      reply.readException()
      return reply.readString()
    } catch (t: Throwable) {
      return null
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun isSupport(remote: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
      remote.transact(3, data, reply, 0)
      reply.readException()
      return (0 != reply.readInt())
    } catch (t: Throwable) {
      return false
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}