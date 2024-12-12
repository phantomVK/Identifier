package com.phantomvk.identifier.app

import android.app.Application
import android.os.StrictMode
import com.phantomvk.identifier.IdentifierManager
import com.tencent.mmkv.MMKV


class Application : Application() {
  companion object {
    private val mmkv by lazy { MMKV.mmkvWithID("identifier_config") }

    val IS_DEBUG: Boolean
      get() = mmkv.getBoolean("is_debug", true)

    val IS_EXPERIMENTAL: Boolean
      get() = mmkv.getBoolean("is_experimental", true)

    val IS_LIMIT_AD_TRACKING: Boolean
      get() = mmkv.getBoolean("is_limit_ad_tracking", true)

    val IS_MEM_CACHE_ENABLE: Boolean
      get() = mmkv.getBoolean("is_mem_cache_enable", false)

    val IS_AAID_ENABLE: Boolean
      get() = mmkv.getBoolean("is_aaid_enable", true)

    val IS_VAID_ENABLE: Boolean
      get() = mmkv.getBoolean("is_vaid_enable", true)

    val IS_GOOGLE_ADS_ID_ENABLE: Boolean
      get() = mmkv.getBoolean("is_google_ads_id_enable", true)
  }

  override fun onCreate() {
    super.onCreate()
    MMKV.initialize(this)

    if (mmkv.getBoolean("is_strict_mode_enable", false)) {
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
      .setLimitAdTracking(IS_LIMIT_AD_TRACKING)
      .setMemCacheEnable(IS_MEM_CACHE_ENABLE)
      .setExecutor { Thread(it).start() } // optional: setup custom ThreadPoolExecutor
      .setLogger(LoggerImpl())
      .init()
  }
}