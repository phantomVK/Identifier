package com.phantomvk.identifier.provider

import com.android.id.impl.IdProviderImpl
import com.phantomvk.identifier.model.ProviderConfig

internal class AndroidIdProvider(config: ProviderConfig) : AbstractProvider(config) {

  private lateinit var impl: IdProviderImpl

  override fun isSupported(): Boolean {
    impl = IdProviderImpl()
    return true
  }

  override fun run() {
    getConsumer().onError(impl::class.java.declaredMethods.joinToString("\n * ", "\n * "))
  }
}