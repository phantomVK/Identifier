package com.phantomvk.identifier.impl

import com.phantomvk.identifier.interfaces.OnResultListener
import java.lang.ref.WeakReference

class ResultListenerProxy(callback: OnResultListener) : OnResultListener {

  private val reference = WeakReference(callback)

  override fun onError(msg: String, t: Throwable?) {
    reference.get()?.let { callback ->
      reference.clear()
      callback.onError(msg, t)
    }
  }

  override fun onSuccess(id: String) {
    reference.get()?.let { callback ->
      reference.clear()
      callback.onSuccess(id)
    }
  }

  fun dispose() {
    reference.clear()
  }

  fun isDisposed(): Boolean {
    return reference.get() == null
  }
}