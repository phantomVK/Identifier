package com.phantomvk.identifier.impl

import com.phantomvk.identifier.interfaces.Disposable
import com.phantomvk.identifier.interfaces.OnResultListener
import java.lang.ref.WeakReference

internal class DisposableResultListener(
  private val reference: WeakReference<OnResultListener>
) : OnResultListener, Disposable {

  @Volatile
  private var disposed = false

  override fun onError(msg: String, t: Throwable?) {
    invokeCallback { it.onError(msg, t) }
  }

  override fun onSuccess(id: String) {
    invokeCallback { it.onSuccess(id) }
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
      if (disposed) {
        return
      }

      if (callback != null) {
        runOnMainThread {
          reference.get()?.let {
            callback.invoke(it)
          }
        }
      }

      reference.clear()
      disposed = true
    }
  }
}