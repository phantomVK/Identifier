package com.phantomvk.identifier.interfaces

import com.phantomvk.identifier.model.IdentifierResult

interface OnResultListener {
  fun onSuccess(result: IdentifierResult)
  fun onError(msg: String, t: Throwable? = null)
}