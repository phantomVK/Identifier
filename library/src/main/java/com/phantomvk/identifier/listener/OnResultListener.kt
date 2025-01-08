package com.phantomvk.identifier.listener

import com.phantomvk.identifier.model.IdentifierResult

interface OnResultListener {
  fun onSuccess(result: IdentifierResult)
  fun onError(msg: String, throwable: Throwable? = null)
}