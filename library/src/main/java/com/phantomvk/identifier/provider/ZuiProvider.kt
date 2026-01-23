package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig

internal class ZuiProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.zui.deviceidservice")
  }

  override fun getInterfaceName(): String {
    return "com.zui.deviceidservice.IDeviceidInterface"
  }

  override fun run() {
    val intent = Intent().setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService")
    bindService(intent)
  }

  override fun call(binder: IBinder): BinderResult {
    if (config.isVerifyLimitAdTracking) {
      if (!readBoolean(binder, 3, true, null)) {
        return Failed(LIMIT_AD_TRACKING_IS_ENABLED)
      }
    }

    return queryId(binder, 1, 4, 5)
  }
}