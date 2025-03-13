package com.phantomvk.identifier.provider

import android.provider.Settings
import com.phantomvk.identifier.model.ProviderConfig

internal class HonorSettingsGlobalProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return !Settings.Global.getString(config.context.contentResolver, "oaid_limit_state").isNullOrBlank()
  }

  override fun run() {
    checkId(Settings.Global.getString(config.context.contentResolver, "oaid"), getConsumer())
  }
}