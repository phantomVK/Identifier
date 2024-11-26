package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig

internal class SamsungProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.samsung.android.deviceidservice")
  }

  override fun run() {
    val intent = Intent().setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService")
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        return when (val r = getId(binder, 1)) {
          is BinderResult.Failed -> return r
          is BinderResult.Success -> {
            val vaid = queryId(IdEnum.VAID) { getId(binder, 2) }
            val aaid = queryId(IdEnum.AAID) { getId(binder, 3) }
            BinderResult.Success(r.id, vaid, aaid)
          }
        }
      }
    })
  }

  private fun getId(binder: IBinder, code: Int): BinderResult {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.samsung.android.deviceidservice.IDeviceIdService")
      data.writeString(config.context.packageName)
      binder.transact(code, data, reply, 0)
      reply.readException()
      return checkId(reply.readString())
    } catch (t: Throwable) {
      return BinderResult.Failed(EXCEPTION_THROWN, t)
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}