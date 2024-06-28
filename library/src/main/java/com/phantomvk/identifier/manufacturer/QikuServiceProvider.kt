package com.phantomvk.identifier.manufacturer

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.impl.Constants.AIDL_INTERFACE_IS_NULL
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.impl.ServiceManager
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.qiku.id.IOAIDInterface

class QikuServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "QikuServiceProvider"
  }

  override fun ifSupported(): Boolean {
    return isPackageInfoExisted("com.qiku.id")
  }

  override fun execute() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IOAIDInterface.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          val isLimited = asInterface.isLimited
          if (isLimited) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        val id = asInterface.oaid
        return checkId(id)
      }
    }

    val intent = Intent("qiku.service.action.id")
    intent.setPackage("com.qiku.id")
    ServiceManager.bindService(config.context, intent, getCallback(), binderCallback)
  }
}