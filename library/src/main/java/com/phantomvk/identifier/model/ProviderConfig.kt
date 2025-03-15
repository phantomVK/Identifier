package com.phantomvk.identifier.model

import android.content.Context
import com.phantomvk.identifier.functions.Consumer
import java.lang.ref.WeakReference
import java.lang.reflect.Method
import java.util.concurrent.Executor

internal class ProviderConfig(val context: Context) {
  var asyncCallback = false
  var executor = Executor { c: Runnable -> Thread(c).start() }
  var isDebug = false
  var isExperimental = false
  var idConfig = IdConfig()
  var memoryConfig = MemoryConfig()
  var verifyLimitAdTracking = false
  lateinit var consumer: WeakReference<Consumer>
  lateinit var sysProps: Method

  fun clone(): ProviderConfig {
    val config = ProviderConfig(context)
    config.asyncCallback = asyncCallback
    config.executor = executor
    config.isDebug = isDebug
    config.isExperimental = isExperimental
    config.idConfig = idConfig.clone()
    config.memoryConfig = memoryConfig.clone()
    config.verifyLimitAdTracking = verifyLimitAdTracking
    return config
  }

  fun getCacheKey(): String {
    var flag = if (idConfig.isAaidEnabled) 1 else 0
    if (idConfig.isVaidEnabled) flag += 2
    if (idConfig.isGoogleAdsIdEnabled) flag += 4
    return flag.toString()
  }
}