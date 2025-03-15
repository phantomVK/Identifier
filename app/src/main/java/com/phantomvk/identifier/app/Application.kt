package com.phantomvk.identifier.app

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.app.settings.Settings
import com.phantomvk.identifier.app.settings.SettingsManager
import com.phantomvk.identifier.log.Logger
import com.phantomvk.identifier.log.TraceLevel
import java.util.concurrent.Executors


class Application : Application() {

  companion object {
    lateinit var applicationInstance: Application
  }

  override fun onCreate() {
    super.onCreate()
    applicationInstance = this
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

    val logger = object : Logger {
      override fun log(level: TraceLevel, tag: String, message: String, throwable: Throwable?) {
        when (level) {
          TraceLevel.VERBOSE -> Log.v(tag, message, throwable)
          TraceLevel.DEBUG -> Log.d(tag, message, throwable)
          TraceLevel.INFO -> Log.i(tag, message, throwable)
          TraceLevel.WARN -> Log.w(tag, message, throwable)
          TraceLevel.ERROR -> Log.e(tag, message, throwable)
          TraceLevel.ASSERT -> Log.wtf(tag, message, throwable)
        }
      }
    }

    IdentifierManager.Builder(this)
      .setDebug(Settings.Debug.getValue())
      .setExecutor(Executors.newFixedThreadPool(1)) // optional: setup custom ThreadPoolExecutor
      .setLogger(logger)
      .build()
  }
}