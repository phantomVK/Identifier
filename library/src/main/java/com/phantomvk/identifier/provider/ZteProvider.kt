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

    when (val r = getId("getOAID")) {
      is BinderResult.Failed -> getConsumer().onError(r.msg, r.throwable)
      is BinderResult.Success -> {
        val aaid = invokeById(IdEnum.AAID) { getId("getAAID") }
        val vaid = invokeById(IdEnum.VAID) { getId("getVAID") }
        getConsumer().onSuccess(IdentifierResult(r.id, aaid, vaid))
      }
    }
  }

  private fun getId(code: String): BinderResult {
    try {
      val result = when (code) {
        "getOAID" -> manager.getOAID(config.context)
        "getAAID" -> manager.getAAID(config.context)
        "getVAID" -> manager.getVAID(config.context)
        else -> null
      }
      return checkId(result)
    } catch (t: Throwable) {
      return BinderResult.Failed(EXCEPTION_THROWN, t)
    }
  }
}