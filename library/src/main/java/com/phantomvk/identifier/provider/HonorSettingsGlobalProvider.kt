package com.phantomvk.identifier.provider

import android.provider.Settings
import com.phantomvk.identifier.model.ProviderConfig

internal class HonorSettingsGlobalProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun run() {
    if (Settings.Global.getString(config.context.contentResolver, "oaid_limit_state").isNullOrBlank()) {
      getConsumer().onError(NOT_SUPPORTED)
      return
    }

    checkId(Settings.Global.getString(config.context.contentResolver, "oaid"), getConsumer())
  }
}