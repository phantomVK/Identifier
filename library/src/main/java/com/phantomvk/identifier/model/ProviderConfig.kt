package com.phantomvk.identifier.model

import android.content.Context
import com.phantomvk.identifier.functions.Consumer
import java.lang.ref.WeakReference
import java.lang.reflect.Method
import java.util.concurrent.Executor

internal class ProviderConfig(val context: Context) {
  // Global configs.
  var executor = Executor { c: Runnable -> Thread(c).start() }
  var isDebug = false
  var isMergeRequests = false

  // Local configs.
  var isAsyncCallback = false
  var isExperimental = false
  var isExternalSdkQuerying = false
  var isVerifyLimitAdTracking = false
  var idConfig = IdConfig()
  var memoryConfig = MemoryConfig()
  lateinit var consumer: WeakReference<Consumer>
  lateinit var sysProps: Method

  fun clone(): ProviderConfig {
    val config = ProviderConfig(context)

    // Global configs.
    config.executor = executor
    config.isDebug = isDebug
    config.isMergeRequests = isMergeRequests

    // Local configs.
    config.isAsyncCallback = isAsyncCallback
    config.isExperimental = isExperimental
    config.isExternalSdkQuerying = isExternalSdkQuerying
    config.isVerifyLimitAdTracking = isVerifyLimitAdTracking
    config.idConfig = idConfig.clone()
    config.memoryConfig = memoryConfig.clone()
    return config
  }

  fun getCacheKey(): String {
    var flag = if (idConfig.isAaidEnabled) 1 else 0
    if (idConfig.isVaidEnabled) flag += 2
    if (idConfig.isGoogleAdsIdEnabled) flag += 4
    return flag.toString()
  }
}