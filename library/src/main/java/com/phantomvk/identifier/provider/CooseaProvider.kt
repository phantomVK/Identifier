package com.phantomvk.identifier.provider

import android.app.KeyguardManager
import android.content.Context.KEYGUARD_SERVICE
import com.phantomvk.identifier.model.ProviderConfig

internal class CooseaProvider(config: ProviderConfig) : AbstractProvider(config) {

  private val manager = config.context.getSystemService(KEYGUARD_SERVICE) as? KeyguardManager

  override fun isSupported(): Boolean {
    if (manager == null) return false
    val method = manager::class.java.getDeclaredMethod("isSupported")
    return (method.invoke(manager) as? Boolean) == true
  }

  override fun run() {
    val method = manager!!::class.java.getDeclaredMethod("obtainOaid")
    checkId(method.invoke(manager) as? String, targetConsumer)
  }
}