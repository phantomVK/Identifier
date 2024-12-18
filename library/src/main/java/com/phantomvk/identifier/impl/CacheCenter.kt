package com.phantomvk.identifier.impl

import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig
import java.util.concurrent.ConcurrentHashMap

internal object CacheCenter {
  private val map = ConcurrentHashMap<String, IdentifierResult>()

  fun get(config: ProviderConfig): IdentifierResult? {
    return if (config.isMemCacheEnabled) map[config.getCacheKey()] else null
  }

  fun putIfAbsent(config: ProviderConfig, result: IdentifierResult) {
    if (config.isMemCacheEnabled) map.putIfAbsent(config.getCacheKey(), result)
  }
}