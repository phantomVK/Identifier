package com.phantomvk.identifier.model

import android.content.Context
import com.phantomvk.identifier.interfaces.OnResultListener
import java.lang.ref.WeakReference
import java.util.concurrent.Executor

class ProviderConfig(val context: Context) {
  var executor: Executor = Executor { c: Runnable -> Thread(c).start() }
  var isDebug = false
  var isExperimental = false
  var isGoogleAdsIdEnabled = false
  var isLimitAdTracking: Boolean = false
  var isMemCacheEnabled = false
  var queryAaid: Boolean = false
  var queryVaid: Boolean = false
  lateinit var callback: WeakReference<OnResultListener>

  fun clone(): ProviderConfig {
    val config = ProviderConfig(context)
    config.executor = executor
    config.isDebug = isDebug
    config.isExperimental = isExperimental
    config.isGoogleAdsIdEnabled = isGoogleAdsIdEnabled
    config.isLimitAdTracking = isLimitAdTracking
    config.isMemCacheEnabled = isMemCacheEnabled
    config.queryAaid = queryAaid
    config.queryVaid = queryVaid
    return config
  }
}