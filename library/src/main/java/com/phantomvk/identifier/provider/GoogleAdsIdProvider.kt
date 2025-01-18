package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import android.os.Parcel
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
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isLimitAdTracking) {
          if (isLimitAdTrackingEnabled(binder)) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return getId(binder, 1)
      }
    })
  }

  private fun isLimitAdTrackingEnabled(remote: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService")
      data.writeInt(1)
      remote.transact(2, data, reply, 0)
      reply.readException()
      return 0 != reply.readInt()
    } catch (t: Throwable) {
      return false
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}