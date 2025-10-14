package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig

internal class AsusProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.asus.msa.SupplementaryDID")
  }

  override fun getInterfaceName(): String {
    return "com.asus.msa.SupplementaryDID.IDidAidlInterface"
  }

  // udid:2, oaid:3, vaid:4, aaid:5
  override fun run() {
    val componentName = ComponentName("com.asus.msa.SupplementaryDID", "com.asus.msa.SupplementaryDID.SupplementaryDIDService")
    val intent = Intent("com.asus.msa.action.ACCESS_DID").setComponent(componentName)
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isVerifyLimitAdTracking) {
          if (!readBoolean(binder, 1, true, null)) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return queryId(binder, 3, 4, 5)
      }
    })
  }
}