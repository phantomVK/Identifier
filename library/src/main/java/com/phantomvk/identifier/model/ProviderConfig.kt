package com.phantomvk.identifier.model

import android.content.Context
import com.phantomvk.identifier.interfaces.OnResultListener
import java.util.concurrent.Executor

class ProviderConfig(val context: Context) {
  var isDebug = false
  var isExperimental = false
  var isMemCacheEnabled = false
  var isLimitAdTracking: Boolean = false

  lateinit var callback: OnResultListener
  lateinit var executor: Executor
}