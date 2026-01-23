package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig

internal class CoolpadServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.coolpad.deviceidsupport")
  }

  override fun getInterfaceName(): String {
    return "com.coolpad.deviceidsupport.IDeviceIdManager"
  }

  // udid:1, oaid:2, vaid:3, aaid:4
  override fun run() {
    val componentName = ComponentName("com.coolpad.deviceidsupport", "com.coolpad.deviceidsupport.DeviceIdService")
    val intent = Intent().setComponent(componentName)
    bindService(intent)
  }

  override fun call(binder: IBinder): BinderResult {
    return queryId(binder, 2, 3, 4)
  }
}