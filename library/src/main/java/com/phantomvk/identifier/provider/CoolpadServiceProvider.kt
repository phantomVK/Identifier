package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig

internal class CoolpadServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.coolpad.deviceidsupport")
  }

  override fun run() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        when (val result = checkId(getId(binder, 2))) {
          is BinderResult.Failed -> return result
          is BinderResult.Success -> {
            val vaid = if (config.queryVaid) getId(binder, 3) else null
            val aaid = if (config.queryAaid) getId(binder, 4) else null
            return BinderResult.Success(result.id, vaid, aaid)
          }
        }
      }
    }

    val componentName = ComponentName("com.coolpad.deviceidsupport", "com.coolpad.deviceidsupport.DeviceIdService")
    val intent = Intent().setComponent(componentName)
    bindService(intent, binderCallback)
  }

  // oaid:2, vaid:3, aaid:4
  private fun getId(binder: IBinder, code: Int): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.coolpad.deviceidsupport.IDeviceIdManager")
      data.writeString(config.context.packageName)
      binder.transact(code, data, reply, 0)
      reply.readException()
      return reply.readString()
    } catch (t: Throwable) {
      return null
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}