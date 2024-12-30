package com.phantomvk.identifier.log

enum class TraceLevel(val level: Int, val levelString: String) {
  VERBOSE(2, "V"),
  DEBUG(3, "D"),
  INFO(4, "I"),
  WARN(5, "W"),
  ERROR(6, "E"),
  ASSERT(7, "A")
}
