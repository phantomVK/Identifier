package com.phantomvk.identifier.log

sealed class TraceLevel(val level: Int, val levelString: String) {
  object VERBOSE : TraceLevel(2, "V")
  object DEBUG : TraceLevel(3, "D")
  object INFO : TraceLevel(4, "I")
  object WARN : TraceLevel(5, "W")
  object ERROR : TraceLevel(6, "E")
  object ASSERT : TraceLevel(7, "A")
}
