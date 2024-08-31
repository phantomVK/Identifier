package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig

internal class SamsungProvider(config: ProviderConfig) : AbstractProvider(config) {

  private val pkgName = "com.samsung.android.deviceidservice"
  private val className = "com.samsung.android.deviceidservice.DeviceIdService"

  override fun isSupported(): Boolean {
    return isPackageInfoExisted(pkgName)
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

    val intent = Intent().setClassName(pkgName, className)
    bindService(intent, binderCallback)
  }
}