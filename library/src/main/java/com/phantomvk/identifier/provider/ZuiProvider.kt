package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.zui.deviceidservice.IDeviceidInterface

internal class ZuiProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.zui.deviceidservice")
  }

  override fun run() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IDeviceidInterface.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          if (!asInterface.isSupport) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return checkId(asInterface.oaid)
      }
    }

    val intent = Intent().setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService")
    bindService(intent, binderCallback)
  }
}