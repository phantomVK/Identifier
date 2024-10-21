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
    if (config.isLimitAdTracking) {
      try {
        val method = clazz?.getDeclaredMethod("isSupported", Context::class.java)
        val isSupported = method?.invoke(instance, config.context) as? Boolean
        if (isSupported == false) {
          getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          return
        }
      } catch (ignore: Throwable) {
      }
    }

    when (val r = checkId(getId("getOAID"))) {
      is BinderResult.Failed -> getCallback().onError(r.msg)
      is BinderResult.Success -> {
        val aaid = if (config.queryAaid) getId("getAAID") else null
        val vaid = if (config.queryVaid) getId("getVAID") else null
        getCallback().onSuccess(IdentifierResult(r.id, aaid, vaid))
      }
    }
  }

  private fun getId(code: String): String? {
    try {
      val method = clazz?.getDeclaredMethod(code, Context::class.java)
      return method?.invoke(instance, config.context) as? String
    } catch (t: Throwable) {
      return null
    }
  }
}