package com.phantomvk.identifier.provider

import android.app.ZteDeviceIdentifyManager
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class ZteProvider(config: ProviderConfig) : AbstractProvider(config) {

  private lateinit var manager: ZteDeviceIdentifyManager

  override fun isSupported(): Boolean {
    try {
      manager = ZteDeviceIdentifyManager(config.context)
      return true
    } catch (ignore: Throwable) {
      return false
    }
  }

  override fun run() {
    if (config.isVerifyLimitAdTracking) {
      try {
        val isSupported = manager.isSupported(config.context)
        if (!isSupported) {
          getConsumer().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          return
        }
      } catch (ignore: Throwable) {
      }
    }

    when (val r = checkId(manager.getOAID(config.context))) {
      is Failed -> getConsumer().onError(r.msg, r.throwable)
      is Success -> {
        val aaid = if (config.idConfig.isAaidEnabled) checkId(manager.getAAID(config.context)).id else null
        val vaid = if (config.idConfig.isVaidEnabled) checkId(manager.getVAID(config.context)).id else null
        getConsumer().onSuccess(IdentifierResult(r.id, aaid, vaid))
      }
    }
  }
}