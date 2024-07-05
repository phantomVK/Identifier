package com.phantomvk.identifier.manufacturer

import android.provider.Settings
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.model.ProviderConfig

class HonorSettingsProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "HonorSettingsProvider"
  }

  override fun ifSupported(): Boolean {
    return true
  }

  override fun execute() {
    val resolver = config.context.contentResolver

    if (config.isLimitAdTracking) {
      val isLimitAdTrackingEnabled = Settings.Global.getString(resolver, "oaid_limit_state")
      if (isLimitAdTrackingEnabled?.toBoolean() == true) {
        getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
      }
    }

    val id = Settings.Global.getString(resolver, "oaid")
    checkId(id, getCallback())
  }
}