package com.phantomvk.identifier.log

interface Logger {
  fun log(level: TraceLevel, tag: String, message: String, throwable: Throwable?)
}
