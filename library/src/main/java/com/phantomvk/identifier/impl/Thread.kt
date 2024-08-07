package com.phantomvk.identifier.impl

import android.os.Handler
import android.os.Looper

private val mainHandler = Handler(Looper.getMainLooper())

internal fun runOnMainThread(delayMillis: Long = 0, runnable: Runnable) {
  if (Looper.getMainLooper() == Looper.myLooper()) {
    runnable.run()
    return
  }

  if (delayMillis > 0L) {
    mainHandler.postDelayed(runnable, delayMillis)
  } else {
    mainHandler.post(runnable)
  }
}