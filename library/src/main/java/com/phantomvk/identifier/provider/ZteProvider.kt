package com.phantomvk.identifier.provider

import android.content.Context
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class ZteProvider(config: ProviderConfig) : AbstractProvider(config) {

  private var clazz: Class<*>? = null
  private var instance: Any? = null

  override fun isSupported(): Boolean {
    try {
      val c = Class.forName("android.app.ZteDeviceIdentifyManager")
      val constructor = c.getDeclaredConstructor(Context::class.java)
      constructor.isAccessible = true
      instance = constructor.newInstance(config.context)
      clazz = c
    } catch (ignore: Throwable) {
      clazz = null
      instance = null
    }

    return instance != null
  }

  override fun run() {
    if (config.isVerifyLimitAdTracking) {
      try {
        val method = clazz?.getDeclaredMethod("isSupported", Context::class.java)
        val isSupported = method?.invoke(instance, config.context) as? Boolean
        if (isSupported == false) {
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
      val method = clazz?.getDeclaredMethod(code, Context::class.java)
      return checkId(method?.invoke(instance, config.context) as? String)
    } catch (t: Throwable) {
      return BinderResult.Failed(EXCEPTION_THROWN, t)
    }
  }
}