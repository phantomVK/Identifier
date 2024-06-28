package com.phantomvk.identifier.manufacturer

import com.hihonor.ads.identifier.AdvertisingIdClient
import com.phantomvk.identifier.impl.Constants.ID_INFO_IS_NULL
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.model.ProviderConfig

class HonorProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "HonorProvider"
  }

  override fun ifSupported(): Boolean {
    return AdvertisingIdClient.isAdvertisingIdAvailable(config.context)
  }

  override fun execute() {
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