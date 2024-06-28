package com.phantomvk.identifier.manufacturer

import android.os.Build
import android.provider.Settings
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.model.ProviderConfig

class HonorSettingsProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "HonorSettingsProvider"
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
  override fun ifSupported(): Boolean {
    return Build.VERSION.SDK_INT >= 17
  }

  @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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