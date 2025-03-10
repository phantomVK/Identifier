package com.phantomvk.identifier.provider

import android.provider.Settings
import com.phantomvk.identifier.model.ProviderConfig

internal class HonorSettingsSecureProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return !Settings.Secure.getString(config.context.contentResolver, "oaid_limit_state").isNullOrBlank()
  }

  override fun run() {
    checkId(Settings.Secure.getString(config.context.contentResolver, "oaid"), getCallback())
  }
}