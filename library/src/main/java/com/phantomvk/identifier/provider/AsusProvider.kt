package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.asus.msa.SupplementaryDID.IDidAidlInterface

internal class AsusProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.asus.msa.SupplementaryDID")
  }

  override fun run() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IDidAidlInterface.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          val isSupport = asInterface.isSupport
          if (!isSupport) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return checkId(asInterface.oaid)
      }
    }

    val intent = Intent("com.asus.msa.action.ACCESS_DID")
    val componentName = ComponentName("com.asus.msa.SupplementaryDID", "com.asus.msa.SupplementaryDID.SupplementaryDIDService")
    intent.setComponent(componentName)
    bindService(intent, binderCallback)
  }
}