package com.phantomvk.identifier.app

import android.app.Application
import com.phantomvk.identifier.IdentifierManager

class Application : Application() {
  override fun onCreate() {
    super.onCreate()

    IdentifierManager.Builder(applicationContext)
      .setDebug(true)
      .setExperimental(true)
      .setLimitAdTracking(false)
      .setMemCacheEnable(false)
      .setExecutor { Thread(it).start() } // optional: setup custom ThreadPoolExecutor
      .setLogger(LoggerImpl())
      .init()
  }
}