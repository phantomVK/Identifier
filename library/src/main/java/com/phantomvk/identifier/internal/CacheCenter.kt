package com.phantomvk.identifier.internal

import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig
import java.lang.reflect.Method

internal object CacheCenter {

  // https://issuetracker.google.com/issues/37042460
  @Volatile
  private var map = HashMap<String, IdentifierResult>()

  @Volatile
  private var getSystemPropsMethod: Method? = null

  internal fun get(config: ProviderConfig): IdentifierResult? {
    return if (config.memoryConfig.isEnabled) {
      map[config.getCacheKey()]
    } else {
      null
    }
  }

  internal fun put(config: ProviderConfig, result: IdentifierResult) {
    if (config.memoryConfig.isEnabled) {
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

  fun clear() {
    synchronized(CacheCenter::class.java) {
      map = HashMap()
    }
  }

  internal fun getSystemPropsMethod(): Method {
    var method = getSystemPropsMethod
    if (method != null) {
      return method
    }

    method = Class.forName("android.os.SystemProperties").getMethod("get", String::class.java, String::class.java)
    getSystemPropsMethod = method
    return method
  }
}