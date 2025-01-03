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
    val intent = Intent().setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService")
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isLimitAdTracking) {
          if (!isSupport(binder)) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return when (val r = getId(binder, 1)) {
          is BinderResult.Failed -> r
          is BinderResult.Success -> {
            val vaid = queryId(IdEnum.VAID) { getId(binder, 4) }
            val aaid = queryId(IdEnum.AAID) { getId(binder, 5) }
            BinderResult.Success(r.id, vaid, aaid)
          }
        }
      }
    })
  }

  private fun getId(remote: IBinder, code: Int): BinderResult {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
      data.writeString(config.context.packageName)
      remote.transact(code, data, reply, 0)
      reply.readException()
      return checkId(reply.readString())
    } catch (t: Throwable) {
      return BinderResult.Failed(EXCEPTION_THROWN, t)
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
      return 0 != reply.readInt()
    } catch (t: Throwable) {
      return true
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}