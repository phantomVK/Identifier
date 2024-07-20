package com.phantomvk.identifier.app

import android.app.Application
import com.phantomvk.identifier.IdentifierManager

class Application : Application() {

  companion object {
    lateinit var sApplication: Application
  }

  override fun onCreate() {
    super.onCreate()
    sApplication = this

    // init.
    IdentifierManager.Builder(this)
      .setDebug(true)
      .setExperimental(true)
      .setMemCacheEnable(false)
      .setExecutor { Thread(it).start() }
      .setLogger(LoggerImpl())
      .init()
  }
}