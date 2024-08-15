package com.phantomvk.identifier.provider

import com.hihonor.ads.identifier.AdvertisingIdClient
import com.phantomvk.identifier.model.ProviderConfig

internal class HonorSdkProvider(config: ProviderConfig) : AbstractProvider(config) {

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

    checkId(info.id, getCallback())
  }
}