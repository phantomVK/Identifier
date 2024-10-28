package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.android.creator.IdsSupplier

internal class FreemeProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.android.creator")
  }

  override fun run() {
    val intent = Intent("android.service.action.msa").setPackage("com.android.creator")
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        val asInterface = IdsSupplier.Stub.asInterface(binder)
        if (asInterface == null) {
          return BinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          if (!asInterface.isSupported) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return checkId(asInterface.oaid)
      }
    })
  }
}