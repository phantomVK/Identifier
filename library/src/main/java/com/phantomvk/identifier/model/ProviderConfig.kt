package com.phantomvk.identifier.model

import android.content.Context
import com.phantomvk.identifier.listener.OnResultListener
import java.lang.ref.WeakReference
import java.util.concurrent.Executor

internal class ProviderConfig(val context: Context) {
  var asyncCallback = false
  var executor = Executor { c: Runnable -> Thread(c).start() }
  var isDebug = false
  var isExperimental = false
  var isLimitAdTracking = false
  var isMemCacheEnabled = false
  var queryAaid = false
  var queryVaid = false
  var queryGoogleAdsId = false
  lateinit var callback: WeakReference<OnResultListener>

  fun clone(): ProviderConfig {
    val config = ProviderConfig(context)
    config.asyncCallback = asyncCallback
    config.executor = executor
    config.isDebug = isDebug
    config.isExperimental = isExperimental
    config.isLimitAdTracking = isLimitAdTracking
    config.isMemCacheEnabled = isMemCacheEnabled
    config.queryAaid = queryAaid
    config.queryVaid = queryVaid
    config.queryGoogleAdsId = queryGoogleAdsId
    return config
  }

  fun getCacheKey(): String {
    var flag = if (queryAaid) 1 else 0
    if (queryVaid) flag += 2
    if (queryGoogleAdsId) flag += 4
    return flag.toString()
  }
}