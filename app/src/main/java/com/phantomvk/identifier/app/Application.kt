package com.phantomvk.identifier.app

import android.app.Application
import android.os.StrictMode
import com.phantomvk.identifier.IdentifierManager


class Application : Application() {
  companion object {
    const val IS_DEBUG = true
    const val IS_EXPERIMENTAL = true
    const val IS_GOOGLE_ADS_ID_ENABLE = true
    const val IS_LIMIT_AD_TRACKING = true
    const val IS_MEM_CACHE_ENABLE = false
  }

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build()
        .let { StrictMode.setThreadPolicy(it) }

      StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build()
        .let { StrictMode.setVmPolicy(it) }
    }

    IdentifierManager.Builder(this)
      .setDebug(IS_DEBUG)
      .setExperimental(IS_EXPERIMENTAL)
      .setGoogleAdsIdEnable(IS_GOOGLE_ADS_ID_ENABLE)
      .setLimitAdTracking(IS_LIMIT_AD_TRACKING)
      .setMemCacheEnable(IS_MEM_CACHE_ENABLE)
      .setExecutor { Thread(it).start() } // optional: setup custom ThreadPoolExecutor
      .setLogger(LoggerImpl())
      .init()
  }
}