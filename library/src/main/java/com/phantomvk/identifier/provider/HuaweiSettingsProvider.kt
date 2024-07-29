package com.phantomvk.identifier.provider

import android.os.Build
import android.provider.Settings
import androidx.annotation.ChecksSdkIntAtLeast
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.model.ProviderConfig

class HuaweiSettingsProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "HuaweiSettingsProvider"
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
  override fun ifSupported(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
  }

  override fun run() {
    if (config.isLimitAdTracking) {
      try {
        val isLimited = Settings.Global.getString(config.context.contentResolver, "pps_track_limit")
        if (isLimited?.toBoolean() == true) {
          getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          return
        }
      } catch (t: Throwable) {
      }
    }

    val id = Settings.Global.getString(config.context.contentResolver, "pps_oaid")
    checkId(id, getCallback())
  }
}
