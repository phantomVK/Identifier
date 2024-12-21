package com.phantomvk.identifier.impl

import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal object CacheCenter {

  // https://issuetracker.google.com/issues/37042460
  private val map = HashMap<String, IdentifierResult>()

  fun get(config: ProviderConfig): IdentifierResult? {
    return if (config.isMemCacheEnabled) {
      synchronized(map) { map[config.getCacheKey()] }
    } else {
      null
    }
  }

  fun put(config: ProviderConfig, result: IdentifierResult) {
    if (config.isMemCacheEnabled) {
      synchronized(map) { map.put(config.getCacheKey(), result) }
    }
  }
}