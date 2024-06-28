package com.phantomvk.identifier.interfaces

interface OnResultListener {
  fun onSuccess(id: String)
  fun onError(msg: String, t: Throwable? = null)
}