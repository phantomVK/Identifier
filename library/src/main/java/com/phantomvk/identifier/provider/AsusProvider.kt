package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.asus.msa.SupplementaryDID.IDidAidlInterface

class AsusProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "AsusProvider"
  }

  override fun ifSupported(): Boolean {
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

        val id = asInterface.oaid
        return checkId(id)
      }
    }

    val pkg = "com.asus.msa.SupplementaryDID"
    val cls = "com.asus.msa.SupplementaryDID.SupplementaryDIDService"
    val intent = Intent("com.asus.msa.action.ACCESS_DID")
    val componentName = ComponentName(pkg, cls)
    intent.setComponent(componentName)
    bindService(intent, binderCallback)
  }
}