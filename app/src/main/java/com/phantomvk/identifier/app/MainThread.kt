package com.phantomvk.identifier.app

import android.os.Handler
import android.os.Looper

private val mainHandler = Handler(Looper.getMainLooper())

fun runOnMainThread(delayMillis: Long = 0, runnable: Runnable) {
  if (Looper.getMainLooper() == Looper.myLooper()) {
    runnable.run()
  } else if (delayMillis <= 0L) {
    mainHandler.post(runnable)
  } else {
    mainHandler.postDelayed(runnable, delayMillis)
  }
}