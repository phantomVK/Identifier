package com.phantomvk.identifier.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.app.Application.Companion.IS_AAID_ENABLE
import com.phantomvk.identifier.app.Application.Companion.IS_DEBUG
import com.phantomvk.identifier.app.Application.Companion.IS_EXPERIMENTAL
import com.phantomvk.identifier.app.Application.Companion.IS_GOOGLE_ADS_ID_ENABLE
import com.phantomvk.identifier.app.Application.Companion.IS_LIMIT_AD_TRACKING
import com.phantomvk.identifier.app.Application.Companion.IS_MEM_CACHE_ENABLE
import com.phantomvk.identifier.app.Application.Companion.IS_VAID_ENABLE
import com.tencent.mmkv.MMKV


class SettingsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.title = "Settings"
    toolbar.setNavigationOnClickListener { finish() }

    val switches = listOf(
      ViewModel(R.id.switch_debug, "is_debug"),
      ViewModel(R.id.switch_experimental, "is_experimental"),
      ViewModel(R.id.switch_limit_ad_tracking, "is_limit_ad_tracking"),
      ViewModel(R.id.switch_mem_cache_enable, "is_mem_cache_enable", false),
      ViewModel(R.id.switch_aaid_enable, "is_aaid_enable"),
      ViewModel(R.id.switch_vaid_enable, "is_vaid_enable"),
      ViewModel(R.id.switch_google_ads_id_enable, "is_google_ads_id_enable"),
      ViewModel(R.id.switch_strict_mode, "is_strict_mode_enable", false)
    )

    val mmkv = MMKV.mmkvWithID("identifier_config")
    switches.forEach {
      val switch = findViewById<SwitchCompat>(it.id)
      switch.isChecked = mmkv.getBoolean(it.key, it.defValue)
      switch.setOnCheckedChangeListener({ _, isChecked -> mmkv.putBoolean(it.key, isChecked) })
    }
  }

  private fun updateProviderConfig() {
    val m = IdentifierManager::class.java
    val i = m.getDeclaredField("sInstance").apply { isAccessible = true }.get(null)
    val config = m.getDeclaredField("config").apply { isAccessible = true }.get(i)

    val c = Class.forName("com.phantomvk.identifier.model.ProviderConfig")
    val booleanClass = Boolean::class.java
    c.getMethod("setDebug", booleanClass).invoke(config, IS_DEBUG)
    c.getMethod("setExperimental", booleanClass).invoke(config, IS_EXPERIMENTAL)
    c.getMethod("setLimitAdTracking", booleanClass).invoke(config, IS_LIMIT_AD_TRACKING)
    c.getMethod("setMemCacheEnabled", booleanClass).invoke(config, IS_MEM_CACHE_ENABLE)
    c.getMethod("setQueryAaid", booleanClass).invoke(config, IS_AAID_ENABLE)
    c.getMethod("setQueryVaid", booleanClass).invoke(config, IS_VAID_ENABLE)
    c.getMethod("setQueryGoogleAdsId", booleanClass).invoke(config, IS_GOOGLE_ADS_ID_ENABLE)
  }

  override fun onDestroy() {
    super.onDestroy()
    updateProviderConfig()
  }

  private class ViewModel(val id: Int, val key: String, val defValue: Boolean = true)
}