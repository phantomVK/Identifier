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
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        val result: String?
        try {
          data.writeInterfaceToken("com.samsung.android.deviceidservice.IDeviceIdService")
          binder.transact(1, data, reply, 0)
          reply.readException()
          result = reply.readString()
        } finally {
          reply.recycle()
          data.recycle()
        }
        return checkId(result)
      }
    }

    val intent = Intent().setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService")
    bindService(intent, binderCallback)
  }
}