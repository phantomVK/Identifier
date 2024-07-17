package com.phantomvk.identifier.impl

import com.phantomvk.identifier.interfaces.Disposable
import com.phantomvk.identifier.interfaces.OnResultListener
import com.phantomvk.identifier.util.runOnMainThread
import java.lang.ref.WeakReference

class DisposableResultListener(callback: OnResultListener) : OnResultListener, Disposable {

  @Volatile
  private var disposed = false

  private val reference = WeakReference(callback)

  override fun onError(msg: String, t: Throwable?) {
    invokeCallback { it.onError(msg, t) }
  }

  override fun onSuccess(id: String) {
    invokeCallback { it.onSuccess(id) }
  }

  override fun dispose() {
    invokeCallback()
  }

  private fun invokeCallback(callback: ((OnResultListener) -> Any)? = null) {
    if (disposed) {
      return
    }

    synchronized(this) {
      if (disposed) {
        return
      }

      if (callback != null) {
        reference.get()?.let {
          runOnMainThread { callback.invoke(it) }
        }
      }

      reference.clear()
      disposed = true
    }
  }

  override fun isDisposed(): Boolean {
    return disposed
  }
}