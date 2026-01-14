package com.phantomvk.identifier.provider

import android.os.Build
import android.provider.Settings
import androidx.annotation.ChecksSdkIntAtLeast
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class HuaweiSettingsProvider(config: ProviderConfig) : HuaweiBaseProvider(config) {

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
  override fun isSupported(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
  }

  override fun run() {
    if (config.isVerifyLimitAdTracking) {
      try {
        val isLimited = Settings.Global.getString(config.context.contentResolver, "pps_track_limit")
        if (isLimited?.toBoolean() == true) {
          getConsumer().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          return
        }
      } catch (t: Throwable) {
      }
    }

    val id = Settings.Global.getString(config.context.contentResolver, "pps_oaid")
    when (val r = checkId(id)) {
      is BinderResult.Failed -> getConsumer().onError(r.msg, r.throwable)
      is BinderResult.Success -> getConsumer().onSuccess(IdentifierResult(r.id, getAAID(), getVAID()))
    }
  }
}
