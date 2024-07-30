package com.phantomvk.identifier.impl

import android.os.Handler
import android.os.Looper

fun runOnMainThread(delayMillis: Long = 0, runnable: Runnable) {
  if (Looper.getMainLooper() == Looper.myLooper()) {
    runnable.run()
    return
  }

  val mainHandler = Handler(Looper.getMainLooper())
  if (delayMillis <= 0L) {
    mainHandler.post(runnable)
  } else {
    mainHandler.postDelayed(runnable, delayMillis)
  }
}