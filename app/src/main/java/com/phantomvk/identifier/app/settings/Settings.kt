package com.phantomvk.identifier.app.settings

import android.content.Context
import android.content.SharedPreferences
import com.phantomvk.identifier.app.Application
import com.phantomvk.identifier.app.BuildConfig

private lateinit var sharedPreferences: SharedPreferences

object SettingsManager {
  fun init(application: Application) {
    sharedPreferences = application.getSharedPreferences("identifier_config", Context.MODE_PRIVATE)
  }
}

enum class Settings(
  val title: String,
  private val key: String,
  private val defValue: Boolean
) {
  AsyncCallback("Async callback", "is_async_callback", false),
  Debug("Debug mode", "is_debug", BuildConfig.DEBUG),
  Experimental("Experimental mode ", "is_experimental", true),
  ExternalSdkQuerying("External SDK Querying", "is_external_sdk_querying", false),
  LimitAdTracking("Limit Ad Tracking", "is_limit_ad_tracking", true),
  MemCache("Enable Memory Cache", "is_mem_cache_enable", false),
  MergeRequests("Enable Merge-Requests", "is_merge_requests_enable", false),
  Aaid("Enable AAID", "is_aaid_enable", true),
  Vaid("Enable VAID", "is_vaid_enable", true),
  GoogleAdsId("Enable Google Ads ID", "is_google_ads_id_enable", true),
  StrictMode("Enable StrictMode (Restart Required)", "is_strict_mode_enable", false),
  ProvidersDetails("Show providers' details", "is_show_providers_details", true);

  fun getValue(): Boolean {
    return sharedPreferences.getBoolean(key, defValue)
  }

  fun setValue(value: Boolean) {
    sharedPreferences.edit().putBoolean(key, value).apply()
  }
}