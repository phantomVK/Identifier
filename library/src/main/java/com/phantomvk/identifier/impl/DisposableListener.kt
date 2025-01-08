package com.phantomvk.identifier.impl

import android.os.Handler
import android.os.Looper
import com.phantomvk.identifier.disposable.Disposable
import com.phantomvk.identifier.listener.OnResultListener
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class DisposableListener(
  private val config: ProviderConfig
) : OnResultListener, Disposable {

  @Volatile
  private var disposed = false

  override fun onError(msg: String, throwable: Throwable?) {
    invokeCallback { it.onError(msg, throwable) }
  }

  override fun onSuccess(result: IdentifierResult) {
    invokeCallback { it.onSuccess(result) }
  }

  override fun dispose() {
    invokeCallback()
  }

  override fun isDisposed(): Boolean {
    return disposed
  }

  private fun invokeCallback(callback: ((OnResultListener) -> Unit)? = null) {
    if (disposed) {
      return
    }

    synchronized(this) {
      if (disposed) return else disposed = true

      if (callback != null) {
        config.callback.get()?.let {
          if (config.asyncCallback && Looper.getMainLooper() == Looper.myLooper()) {
            config.executor.execute { callback.invoke(it) }
            return@let
          }

          if (!config.asyncCallback && Looper.getMainLooper() != Looper.myLooper()) {
            Handler(Looper.getMainLooper()).post { callback.invoke(it) }
            return@let
          }

          callback.invoke(it)
        }
      }

      config.callback.clear()
    }
  }
}