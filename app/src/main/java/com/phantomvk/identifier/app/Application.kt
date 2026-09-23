package com.phantomvk.identifier.app

import android.app.Application
import android.util.Log
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.app.settings.Settings
import com.phantomvk.identifier.app.settings.SettingsManager
import com.phantomvk.identifier.log.Logger
import com.phantomvk.identifier.log.TraceLevel
import java.util.concurrent.Executors


class Application : Application() {

  override fun onCreate() {
    super.onCreate()
    SettingsManager.init(this)

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
      .setExecutor(Executors.newFixedThreadPool(4) {
        Thread(it, "IdCallback").apply { isDaemon = true }
      })
      .setMergeRequests(Settings.MergeRequests.getValue())
      .setLogger(logger)
      .setPrivacyAcceptedListener { Settings.PrivacyAccepted.getValue() }
      .build()
  }
}