package com.phantomvk.identifier.app.main

import com.phantomvk.identifier.model.IdentifierResult

class ResultDetail(
  val tag: String,
  val result: IdentifierResult?,
  val ts: String? = null,
  val msg: String? = null
)