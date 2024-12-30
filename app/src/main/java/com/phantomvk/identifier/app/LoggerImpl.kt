package com.phantomvk.identifier.app

import android.util.Log
import com.phantomvk.identifier.log.Logger
import com.phantomvk.identifier.log.TraceLevel

class LoggerImpl : Logger {
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
