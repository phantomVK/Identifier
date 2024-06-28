package com.phantomvk.identifier.model

import android.content.Context
import com.phantomvk.identifier.interfaces.OnResultListener

class ProviderConfig(
  val context: Context,
  val callback: OnResultListener
) {
  var isLimitAdTracking: Boolean = false
}