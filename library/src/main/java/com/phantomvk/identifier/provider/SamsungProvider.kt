package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig

internal class SamsungProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.samsung.android.deviceidservice")
  }

  override fun getInterfaceName(): String {
    return "com.samsung.android.deviceidservice.IDeviceIdService"
  }

  override fun run() {
    val intent = Intent().setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService")
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        return when (val r = getId(binder, 1, true)) {
          is BinderResult.Failed -> return r
          is BinderResult.Success -> {
            val vaid = queryId(IdEnum.VAID) { getId(binder, 2, true) }
            val aaid = queryId(IdEnum.AAID) { getId(binder, 3, true) }
            BinderResult.Success(r.id, vaid, aaid)
          }
        }
      }
    })
  }
}