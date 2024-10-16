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
      override fun call(binder: IBinder): CallBinderResult {
        when (val result = checkId(getId(binder, 2))) {
          is CallBinderResult.Failed -> return result
          is CallBinderResult.Success -> {
            val aaid = if (config.queryAaid) getId(binder, 4) else null
            val vaid = if (config.queryVaid) getId(binder, 3) else null
            return CallBinderResult.Success(result.id, aaid, vaid)
          }
        }
      }
    }

    val componentName = ComponentName("com.coolpad.deviceidsupport", "com.coolpad.deviceidsupport.DeviceIdService")
    val intent = Intent().setComponent(componentName)
    bindService(intent, binderCallback)
  }

  // oaid:2, aaid:4, vaid:3
  private fun getId(binder: IBinder, code: Int): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val result: String?
    try {
      data.writeInterfaceToken("com.coolpad.deviceidsupport.IDeviceIdManager")
      data.writeString(config.context.packageName)
      binder.transact(code, data, reply, 0)
      reply.readException()
      result = reply.readString()
    } finally {
      reply.recycle()
      data.recycle()
    }
    return result
  }
}