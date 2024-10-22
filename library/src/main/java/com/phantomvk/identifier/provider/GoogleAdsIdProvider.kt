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

  override fun run() {
    val intent = Intent("com.google.android.gms.ads.identifier.service.START").setPackage("com.google.android.gms")
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isLimitAdTracking) {
          val isLimited = isLimitAdTrackingEnabled(binder)
          if (isLimited) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return checkId(getId(binder))
      }
    })
  }

  private fun getId(remote: IBinder): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val result: String?
    try {
      data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService")
      remote.transact(1, data, reply, 0)
      reply.readException()
      result = reply.readString()
    } finally {
      reply.recycle()
      data.recycle()
    }
    return result
  }

  private fun isLimitAdTrackingEnabled(remote: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val result: Boolean
    try {
      data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService")
      data.writeInt(1)
      remote.transact(2, data, reply, 0)
      reply.readException()
      result = (0 != reply.readInt())
    } finally {
      reply.recycle()
      data.recycle()
    }
    return result
  }
}