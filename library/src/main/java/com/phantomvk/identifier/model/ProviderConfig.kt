package com.phantomvk.identifier.model

import android.content.Context
import com.phantomvk.identifier.interfaces.OnResultListener
import java.lang.ref.WeakReference
import java.util.concurrent.Executor

class ProviderConfig(val context: Context) {
  var isDebug = false
  var isExperimental = false
  var isGoogleAdsIdEnabled = false
  var isLimitAdTracking: Boolean = false
  var isMemCacheEnabled = false

  lateinit var callback: WeakReference<OnResultListener>
  lateinit var executor: Executor
}