package com.phantomvk.identifier.app

import android.app.Application
import android.os.StrictMode
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.app.settings.Settings
import com.phantomvk.identifier.app.settings.SettingsManager
import java.util.concurrent.Executors


class Application : Application() {
  override fun onCreate() {
    super.onCreate()
    SettingsManager.init(this)

    if (Settings.StrictMode.getValue()) {
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
      .setDebug(Settings.Debug.getValue())
      .setExperimental(Settings.Experimental.getValue())
      .setLimitAdTracking(Settings.LimitAdTracking.getValue())
      .setMemCacheEnable(Settings.MemCache.getValue())
      .setExecutor(Executors.newFixedThreadPool(1)) // optional: setup custom ThreadPoolExecutor
      .setLogger(LoggerImpl())
      .init()
  }
}