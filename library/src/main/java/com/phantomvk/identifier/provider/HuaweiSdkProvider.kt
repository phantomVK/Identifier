package com.phantomvk.identifier.provider

import android.content.Context
import com.phantomvk.identifier.model.ProviderConfig

internal class HuaweiSdkProvider(config: ProviderConfig) : AbstractProvider(config) {
  private lateinit var clazz: Class<*>

  override fun isSupported(): Boolean {
    clazz = Class.forName("com.huawei.hms.ads.identifier.AdvertisingIdClient")
    return clazz.getMethod("isAdvertisingIdAvailable", Context::class.java).invoke(null, config.context) as Boolean
  }

  override fun run() {
    val info = clazz.getMethod("getAdvertisingIdInfo", Context::class.java).invoke(null, config.context)
    if (info == null) {
      getConsumer().onError(ID_INFO_IS_NULL)
      return
    }

    if (config.verifyLimitAdTracking) {
      if (info.javaClass.getMethod("isLimitAdTrackingEnabled").invoke(info) as Boolean) {
        getConsumer().onError(LIMIT_AD_TRACKING_IS_ENABLED)
        return
      }
    }

    checkId(info.javaClass.getMethod("getId").invoke(info) as String, getConsumer())
  }
}