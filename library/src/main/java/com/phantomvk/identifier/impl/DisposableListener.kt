package com.phantomvk.identifier.impl

import com.phantomvk.identifier.disposable.Disposable
import com.phantomvk.identifier.listener.OnResultListener
import com.phantomvk.identifier.model.IdentifierResult
import java.lang.ref.WeakReference

internal class DisposableListener(
  private val reference: WeakReference<OnResultListener>
) : OnResultListener, Disposable {

  @Volatile
  private var disposed = false

  override fun onError(msg: String, t: Throwable?) {
    invokeCallback { it.onError(msg, t) }
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
      if (disposed) {
        return
      }

      if (callback != null) {
        reference.get()?.let {
          runOnMainThread(0) {
            callback.invoke(it)
          }
        }
      }

      reference.clear()
      disposed = true
    }
  }
}