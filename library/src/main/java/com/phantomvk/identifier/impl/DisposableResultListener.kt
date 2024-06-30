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
    if (disposed) {
      return
    }

    synchronized(this) {
      if (disposed) {
        return
      }

      reference.get()?.let { runOnMainThread { it.onError(msg, t) } }
      dispose()
    }
  }

  override fun onSuccess(id: String) {
    if (disposed) {
      return
    }

    synchronized(this) {
      if (disposed) {
        return
      }

      reference.get()?.let { runOnMainThread { it.onSuccess(id) } }
      dispose()
    }
  }

  override fun dispose() {
    if (disposed) {
      return
    }

    synchronized(this) {
      if (disposed) {
        return
      }

      disposed = true
      reference.clear()
    }
  }

  override fun isDisposed(): Boolean {
    return disposed
  }
}