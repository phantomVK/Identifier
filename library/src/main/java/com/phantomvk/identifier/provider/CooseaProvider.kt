package com.phantomvk.identifier.provider

import android.app.KeyguardManager
import android.content.Context.KEYGUARD_SERVICE
import com.phantomvk.identifier.model.ProviderConfig

internal class CooseaProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun run() {
    val manager = config.context.getSystemService(KEYGUARD_SERVICE) as? KeyguardManager
    if (manager == null) {
      getConsumer().onError(NOT_SUPPORTED)
      return
    }

    val isSupported = manager::class.java.getDeclaredMethod("isSupported")
    if ((isSupported.invoke(manager) as? Boolean) != true) {
      getConsumer().onError(NOT_SUPPORTED)
      return
    }

    val method = manager::class.java.getDeclaredMethod("obtainOaid")
    checkId(method.invoke(manager) as? String, getConsumer())
  }
}