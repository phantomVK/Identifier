package com.phantomvk.identifier.model

sealed class CallBinderResult {
  class Success(val id: String) : CallBinderResult()
  class Failed(val msg: String) : CallBinderResult()
}