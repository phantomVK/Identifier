package com.phantomvk.identifier.app

import android.app.Application
import com.phantomvk.identifier.IdentifierManager

class Application : Application() {
  override fun onCreate() {
    super.onCreate()
    IdentifierManager.Builder(this)
      .setDebug(true)
      .setExecutor { Thread(it).start() }
      .setExperimental(true)
      .setLimitAdTracking(false)
      .setLogger(LoggerImpl())
      .setMemCacheEnable(false)
      .init()
  }
}