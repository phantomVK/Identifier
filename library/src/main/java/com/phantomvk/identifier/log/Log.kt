package com.phantomvk.identifier.log

object Log {
  private var sLogger: Logger? = null

  @JvmStatic
  fun setLogger(logger: Logger?) {
    sLogger = logger
  }

  internal fun v(tag: String, text: String, throwable: Throwable? = null) {
    log(TraceLevel.VERBOSE, tag, text, throwable)
  }

  internal fun d(tag: String, text: String, throwable: Throwable? = null) {
    log(TraceLevel.DEBUG, tag, text, throwable)
  }

  internal fun i(tag: String, text: String, throwable: Throwable? = null) {
    log(TraceLevel.INFO, tag, text, throwable)
  }

  internal fun w(tag: String, text: String, throwable: Throwable? = null) {
    log(TraceLevel.WARN, tag, text, throwable)
  }

  internal fun e(tag: String, text: String, throwable: Throwable? = null) {
    log(TraceLevel.ERROR, tag, text, throwable)
  }

  private fun log(level: TraceLevel, tag: String, msg: String, throwable: Throwable?) {
    try {
      sLogger?.log(level, tag, msg, throwable)
    } catch (ignore: Throwable) {
    }
  }
}
