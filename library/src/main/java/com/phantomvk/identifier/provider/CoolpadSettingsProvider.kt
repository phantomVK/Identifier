package com.phantomvk.identifier.provider

import android.provider.Settings
import com.phantomvk.identifier.model.ProviderConfig

internal class CoolpadSettingsProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return true
  }

  override fun run() {
    val id = Settings.Global.getString(config.context.contentResolver, "coolos.oaid")
    checkId(id, getCallback())
  }
}