package com.phantomvk.identifier.model

class MemoryConfig(
  var isEnabled: Boolean
) {
  fun clone(): MemoryConfig {
    return MemoryConfig(
      isEnabled = isEnabled
    )
  }
}