package com.phantomvk.identifier.provider

import android.content.Context
import com.phantomvk.identifier.model.ProviderConfig

internal class HonorSdkProvider(config: ProviderConfig) : AbstractProvider(config) {
  private lateinit var clazz: Class<*>

  override fun isSupported(): Boolean {
    clazz = Class.forName("com.hihonor.ads.identifier.AdvertisingIdClient")
    return clazz.getMethod("isAdvertisingIdAvailable", Context::class.java).invoke(null, config.context) as Boolean
  }

  override fun run() {
    val info = clazz.getMethod("getAdvertisingIdInfo", Context::class.java).invoke(null, config.context)
    if (info == null) {
      targetConsumer.onError(ID_INFO_IS_NULL)
      return
    }

    if (config.isVerifyLimitAdTracking) {
      if (info.javaClass.getField("isLimit").getBoolean(info)) {
        targetConsumer.onError(LIMIT_AD_TRACKING_IS_ENABLED)
        return
      }
    }

    checkId(info.javaClass.getField("id").get(info) as String, targetConsumer)
  }
}