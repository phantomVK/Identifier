package com.phantomvk.identifier.functions

import com.phantomvk.identifier.model.IdentifierResult

interface Consumer {
  fun onSuccess(result: IdentifierResult)
  fun onError(msg: String, throwable: Throwable? = null)
}