package com.phantomvk.identifier.model

class MemoryConfig(
  var isEnabled: Boolean = false
) {
  fun clone(): MemoryConfig {
    return MemoryConfig(
      isEnabled = isEnabled
    )
  }
}