package com.phantomvk.identifier.internal

import java.util.HashMap

internal object RunnableComposer {
  /**
   * HashMap<CacheKey: String, runnable: HashSet<SerialRunnable>>
   */
  private val map = HashMap<String, HashSet<SerialRunnable>>()

  /**
   * @return return ture if HashSet is existed, otherwise return false.
   */
  @Synchronized
  internal fun putRunnable(cacheKey: String, consumer: SerialRunnable): Boolean {
    var set = map[cacheKey]
    if (set != null) {
      set.add(consumer)
      return true
    }

    set = HashSet()
    set.add(consumer)
    map[cacheKey] = set
    return false
  }

  /**
   * Return the HashSet of SerialRunnable which is associated with the same cacheKey.
   */
  @Synchronized
  internal fun removeRunnableSet(cacheKey: String): HashSet<SerialRunnable> {
    val set = map[cacheKey]
    map.remove(cacheKey)
    return set ?: HashSet()
  }
}