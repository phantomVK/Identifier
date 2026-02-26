package com.phantomvk.identifier.internal

import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal object CacheCenter {

  // https://issuetracker.google.com/issues/37042460
  @Volatile
  private var map = HashMap<String, IdentifierResult>()

  /**
   * Visible to ALL SerialRunnable instances.
   *
   * HashMap<CacheKey: String, runnable: HashSet<SerialRunnable>>
   */
  private val runnableMap = HashMap<String, HashSet<SerialRunnable>>()

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

  /**
   * Put runnable into the HashSet of SerialRunnable which is associated with the same cacheKey.
   *
   * @return return ture if HashSet is existed, otherwise return false.
   */
  internal fun putRunnable(cacheKey: String, runnable: SerialRunnable): Boolean {
    synchronized(runnableMap) {
      var set = runnableMap[cacheKey]
      if (set != null) {
        set.add(runnable)
        return true
      }

      set = HashSet()
      set.add(runnable)
      runnableMap[cacheKey] = set
      return false
    }
  }

  /**
   * Remove runnable from the HashSet of SerialRunnable which is associated with the same cacheKey.
   *
   * Returns: true if the set contained the specified element
   */
  internal fun removeRunnable(cacheKey: String, runnable: SerialRunnable) {
    synchronized(runnableMap) {
      runnableMap[cacheKey]?.remove(runnable)
    }
  }

  /**
   * Return the HashSet of SerialRunnable which is associated with the same cacheKey.
   */
  internal fun removeRunnableSet(cacheKey: String): HashSet<SerialRunnable>? {
    synchronized(runnableMap) {
      return runnableMap.remove(cacheKey)
    }
  }
}