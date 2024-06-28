package com.phantomvk.identifier.manufacturer

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.impl.Constants.AIDL_INTERFACE_IS_NULL
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.impl.ServiceManager
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.zui.deviceidservice.IDeviceidInterface

class LenovoProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "LenovoProvider"
  }

  override fun ifSupported(): Boolean {
    return isPackageInfoExisted("com.zui.deviceidservice")
  }

  override fun execute() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IDeviceidInterface.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          val isSupport = asInterface.isSupport
          if (!isSupport) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        val id = asInterface.oaid
        return checkId(id)
      }
    }

    val intent = Intent()
    intent.setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService")
    ServiceManager.bindService(config.context, intent, getCallback(), binderCallback)
  }
}