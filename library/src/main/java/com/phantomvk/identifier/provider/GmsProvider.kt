package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService

/**
 * Google Mobile Services
 *
 * https://developers.google.com/android/reference/com/google/android/gms/ads/identifier/AdvertisingIdClient.Info
 */
class GmsProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "GmsProvider"
  }

  override fun ifSupported(): Boolean {
    return isPackageInfoExisted("com.android.vending")
  }

  override fun run() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IAdvertisingIdService.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          val isLimited = asInterface.isLimitAdTrackingEnabled(true)
          if (isLimited) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        val id = asInterface.id
        return checkId(id)
      }
    }

    val intent = Intent("com.google.android.gms.ads.identifier.service.START")
    intent.setPackage("com.google.android.gms")
    bindService(intent, binderCallback)
  }
}