package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig

internal class SamsungProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getInterfaceName(): String {
    return "com.samsung.android.deviceidservice.IDeviceIdService"
  }

  override fun run() {
    if (!isPackageInfoExisted("com.samsung.android.deviceidservice")) {
      getConsumer().onError(NOT_SUPPORTED)
      return
    }

    val intent = Intent().setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService")
    bindService(intent)
  }

  override fun call(binder: IBinder): BinderResult {
    return queryId(binder, 1, 2, 3)
  }
}