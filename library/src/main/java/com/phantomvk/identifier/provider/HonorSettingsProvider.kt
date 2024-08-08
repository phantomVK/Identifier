package com.phantomvk.identifier.provider

import android.provider.Settings
import com.phantomvk.identifier.model.ProviderConfig

internal class HonorSettingsProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return true
  }

  override fun run() {
    val resolver = config.context.contentResolver

    if (config.isLimitAdTracking) {
      val isLimited = Settings.Global.getString(resolver, "oaid_limit_state")
      if (isLimited != "false") {
        getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
        return
      }
    }

    val id = Settings.Global.getString(resolver, "oaid")
    checkId(id, getCallback())
  }
}