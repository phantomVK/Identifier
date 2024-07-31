package com.phantomvk.identifier.provider

import com.hihonor.ads.identifier.AdvertisingIdClient
import com.phantomvk.identifier.model.ProviderConfig

class HonorSdkProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "HonorSdkProvider"
  }

  override fun isSupported(): Boolean {
    return AdvertisingIdClient.isAdvertisingIdAvailable(config.context)
  }

  override fun run() {
    val info = AdvertisingIdClient.getAdvertisingIdInfo(config.context)
    if (info == null) {
      getCallback().onError(ID_INFO_IS_NULL)
      return
    }

    if (config.isLimitAdTracking) {
      if (info.isLimit) {
        getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
        return
      }
    }

    val id = info.id
    checkId(id, getCallback())
  }
}