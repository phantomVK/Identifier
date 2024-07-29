package com.phantomvk.identifier.impl

import android.os.Looper
import com.phantomvk.identifier.impl.Constants.EXCEPTION_THROWN
import com.phantomvk.identifier.impl.Constants.NO_IMPLEMENTATION_FOUND
import com.phantomvk.identifier.interfaces.Disposable
import com.phantomvk.identifier.interfaces.OnResultListener
import com.phantomvk.identifier.log.Log
import com.phantomvk.identifier.model.ProviderConfig
import com.phantomvk.identifier.provider.AbstractProvider
import java.util.concurrent.CountDownLatch

class SerialRunnable(config: ProviderConfig) : AbstractProvider(config), Disposable {

  private val disposable = DisposableResultListener(config.callback)

  init {
    setCallback(disposable)
  }

  override fun getTag(): String {
    return "SerialRunnable"
  }

  override fun ifSupported(): Boolean {
    return true
  }

  override fun run() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      if (config.isDebug) {
        throw RuntimeException("Do not execute runnable on the main thread.")
      } else {
        Thread { onExecute() }.start()
      }
    } else {
      onExecute()
    }
  }

  private fun onExecute() {
    if (disposable.isDisposed()) {
      return
    }

    // Start querying id.
    var isSuccess = false
    val providers = ManufacturerFactory.getProviders(config)
    for (provider in providers) {
      val latch = CountDownLatch(1)
      val resultCallback = object : OnResultListener {
        override fun onSuccess(id: String) {
          getCallback().onSuccess(id)
          isSuccess = true
          latch.countDown()
        }

        override fun onError(msg: String, t: Throwable?) {
          Log.e(getTag(), "${provider.getTag()} onError.", t)
          latch.countDown()
        }
      }

      if (disposable.isDisposed()) {
        return
      }

      // execute Runnable safely.
      try {
        provider.setCallback(resultCallback)
        provider.run()
      } catch (t: Throwable) {
        getCallback().onError(EXCEPTION_THROWN, t)
      }

      latch.await()

      if (isSuccess) {
        return
      }
    }

    getCallback().onError(NO_IMPLEMENTATION_FOUND, null)
  }

  override fun dispose() {
    disposable.dispose()
  }

  override fun isDisposed(): Boolean {
    return disposable.isDisposed()
  }
}