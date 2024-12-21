package com.phantomvk.identifier.app.settings

import com.phantomvk.identifier.app.BuildConfig
import com.tencent.mmkv.MMKV

private val mmkv by lazy(LazyThreadSafetyMode.NONE) { MMKV.mmkvWithID("identifier_config") }

enum class Settings(
  val title: String,
  private val key: String,
  private val defValue: Boolean
) {
  Debug("Debug mode", "is_debug", BuildConfig.DEBUG),
  Experimental("Experimental mode ", "is_experimental", true),
  LimitAdTracking("Limit Ad Tracking", "is_limit_ad_tracking", true),
  MemCache("Enable Memory Cache", "is_mem_cache_enable", false),
  Aaid("Enable AAID", "is_aaid_enable", true),
  Vaid("Enable VAID", "is_vaid_enable", true),
  GoogleAdsId("Enable Google Ads ID", "is_google_ads_id_enable", true),
  StrictMode("Enable StrictMode (Restart Required)", "is_strict_mode_enable", false),
  ProvidersDetails("Show providers' details", "is_show_providers_details", true);

  fun getValue(): Boolean {
    return mmkv.getBoolean(key, defValue)
  }

  fun setValue(value: Boolean) {
    mmkv.putBoolean(key, value)
  }
}