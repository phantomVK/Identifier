package com.phantomvk.identifier.impl

import com.phantomvk.identifier.interfaces.Disposable

class DisposedDisposable : Disposable {
  override fun dispose() {
  }

  override fun isDisposed(): Boolean {
    return true
  }
}