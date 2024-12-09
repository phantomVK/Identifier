package com.phantomvk.identifier.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.tencent.mmkv.MMKV


class SettingsActivity : AppCompatActivity() {

  private lateinit var switchDebug: SwitchCompat
  private lateinit var switchExperimental: SwitchCompat
  private lateinit var switchLimitAdTracking: SwitchCompat
  private lateinit var switchMemCacheEnable: SwitchCompat
  private lateinit var switchAaidEnable: SwitchCompat
  private lateinit var switchVaidEnable: SwitchCompat
  private lateinit var switchGoogleAdsIdEnable: SwitchCompat

  private val mmkv by lazy { MMKV.mmkvWithID("identifier_config") }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    toolbar.setNavigationOnClickListener { finish() }

    switchDebug = findViewById(R.id.switch_debug)
    switchExperimental = findViewById(R.id.switch_experimental)
    switchLimitAdTracking = findViewById(R.id.switch_limit_ad_tracking)
    switchMemCacheEnable = findViewById(R.id.switch_mem_cache_enable)
    switchAaidEnable = findViewById(R.id.switch_aaid_enable)
    switchVaidEnable = findViewById(R.id.switch_vaid_enable)
    switchGoogleAdsIdEnable = findViewById(R.id.switch_google_ads_id_enable)

    switchDebug.isChecked = mmkv.getBoolean("is_debug", true)
    switchExperimental.isChecked = mmkv.getBoolean("is_experimental", true)
    switchLimitAdTracking.isChecked = mmkv.getBoolean("is_limit_ad_tracking", true)
    switchMemCacheEnable.isChecked = mmkv.getBoolean("is_mem_cache_enable", false)
    switchAaidEnable.isChecked = mmkv.getBoolean("is_aaid_enable", true)
    switchVaidEnable.isChecked = mmkv.getBoolean("is_vaid_enable", true)
    switchGoogleAdsIdEnable.isChecked = mmkv.getBoolean("is_google_ads_id_enable", true)

    switchDebug.setOnCheckedChangeListener { _, isChecked ->
      mmkv.putBoolean("is_debug", isChecked)
      restartApp()
    }

    switchExperimental.setOnCheckedChangeListener { _, isChecked ->
      mmkv.putBoolean("is_experimental", isChecked)
      restartApp()
    }

    switchLimitAdTracking.setOnCheckedChangeListener { _, isChecked ->
      mmkv.putBoolean("is_limit_ad_tracking", isChecked)
      restartApp()
    }

    switchMemCacheEnable.setOnCheckedChangeListener { _, isChecked ->
      mmkv.putBoolean("is_mem_cache_enable", isChecked)
      restartApp()
    }

    switchAaidEnable.setOnCheckedChangeListener { _, isChecked ->
      mmkv.putBoolean("is_aaid_enable", isChecked)
      restartApp()
    }

    switchVaidEnable.setOnCheckedChangeListener { _, isChecked ->
      mmkv.putBoolean("is_vaid_enable", isChecked)
      restartApp()
    }

    switchGoogleAdsIdEnable.setOnCheckedChangeListener { _, isChecked ->
      mmkv.putBoolean("is_google_ads_id_enable", isChecked)
      restartApp()
    }
  }

  private fun restartApp() {
    Toast.makeText(this, "Restarting the app...", Toast.LENGTH_SHORT).show()
    Handler(Looper.getMainLooper()).postDelayed({
      val intent = packageManager.getLaunchIntentForPackage(packageName)
      val componentName = intent!!.component
      val mainIntent = Intent.makeRestartActivityTask(componentName)
      mainIntent.setPackage(packageName)
      startActivity(mainIntent)
      Runtime.getRuntime().exit(0)
    }, 2000)
  }
}