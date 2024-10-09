package com.phantomvk.identifier.provider

import android.content.Context
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

    val method = clazz?.getDeclaredMethod("getOAID", Context::class.java)
    val id = method?.invoke(instance, config.context) as? String
    checkId(id, getCallback())
  }
}