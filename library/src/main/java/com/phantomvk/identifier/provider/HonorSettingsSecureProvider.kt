package com.phantomvk.identifier.provider

import android.provider.Settings
import com.phantomvk.identifier.model.ProviderConfig

internal class HonorSettingsSecureProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun run() {
    if (Settings.Secure.getString(config.context.contentResolver, "oaid_limit_state").isNullOrBlank()) {
      getConsumer().onError(NOT_SUPPORTED)
      return
    }

    checkId(Settings.Secure.getString(config.context.contentResolver, "oaid"), getConsumer())
  }
}