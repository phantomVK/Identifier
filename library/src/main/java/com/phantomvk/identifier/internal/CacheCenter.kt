package com.phantomvk.identifier.internal

import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal object CacheCenter {

  // https://issuetracker.google.com/issues/37042460
  @Volatile
  private var map = HashMap<String, IdentifierResult>()

  fun get(config: ProviderConfig): IdentifierResult? {
    return if (config.isMemCacheEnabled) {
      map[config.getCacheKey()]
    } else {
      null
    }
  }

  fun put(config: ProviderConfig, result: IdentifierResult) {
    if (config.isMemCacheEnabled) {
      // fail-fast
      val cacheKey = config.getCacheKey()
      if (map[cacheKey] == result) {
        return
      }

      synchronized(CacheCenter::class.java) {
        if (map[cacheKey] == result) {
          return
        }

        val hm = HashMap(map)
        hm[cacheKey] = result
        map = hm
      }
    }
  }
}