package com.phantomvk.identifier.provider

import com.huawei.hms.ads.identifier.AdvertisingIdClient
import com.phantomvk.identifier.model.ProviderConfig

class HuaweiSdkProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "HuaweiSdkProvider"
  }

  override fun isSupported(): Boolean {
    return AdvertisingIdClient.isAdvertisingIdAvailable(config.context)
  }

  override fun run() {
    val info = AdvertisingIdClient.getAdvertisingIdInfo(config.context)
    if (info == null) {
      getCallback().onError(ID_INFO_IS_NULL, null)
      return
    }

    if (config.isLimitAdTracking) {
      if (info.isLimitAdTrackingEnabled) {
        getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED, null)
        return
      }
    }

    val id = info.id
    checkId(id, getCallback())
  }
}