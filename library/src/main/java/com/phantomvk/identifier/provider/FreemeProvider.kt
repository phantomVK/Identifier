package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.impl.Constants.AIDL_INTERFACE_IS_NULL
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.impl.ServiceManager
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.android.creator.IdsSupplier

class FreemeProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "FreemeProvider"
  }

  override fun ifSupported(): Boolean {
    return isPackageInfoExisted("com.android.creator")
  }

  override fun execute() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IdsSupplier.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          val isSupported = asInterface.isSupported
          if (!isSupported) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        val id = asInterface.oaid
        return checkId(id)
      }
    }

    val intent = Intent("android.service.action.msa")
    intent.setPackage("com.android.creator")
    ServiceManager.bindService(config.context, intent, getCallback(), binderCallback)
  }
}