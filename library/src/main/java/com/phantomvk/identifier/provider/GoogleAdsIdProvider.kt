package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig

/**
 * Google Mobile Services
 *
 * https://developers.google.com/android/reference/com/google/android/gms/ads/identifier/AdvertisingIdClient.Info
 */
internal class GoogleAdsIdProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.android.vending")
  }

  override fun getInterfaceName(): String {
    return "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService"
  }

  override fun run() {
    val intent = Intent("com.google.android.gms.ads.identifier.service.START").setPackage("com.google.android.gms")
    bindService(intent)
  }

  override fun call(binder: IBinder): BinderResult {
    if (config.isVerifyLimitAdTracking) {
      if (readBoolean(binder, 2, false) { it.writeInt(1) }) {
        return Failed(LIMIT_AD_TRACKING_IS_ENABLED)
      }
    }

    return getId(binder, 1)
  }
}