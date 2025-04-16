package com.phantomvk.identifier.internal

import java.util.HashMap

internal object RunnableComposer {
  /**
   * HashMap<CacheKey: String, runnable: HashSet<SerialRunnable>>
   */
  private val map = HashMap<String, HashSet<SerialRunnable>>()

  /**
   * Put runnable into the HashSet of SerialRunnable which is associated with the same cacheKey.
   *
   * @return return ture if HashSet is existed, otherwise return false.
   */
  @Synchronized
  internal fun putRunnable(cacheKey: String, runnable: SerialRunnable): Boolean {
    var set = map[cacheKey]
    if (set != null) {
      set.add(runnable)
      return true
    }

    set = HashSet()
    set.add(runnable)
    map[cacheKey] = set
    return false
  }

  /**
   * Remove runnable from the HashSet of SerialRunnable which is associated with the same cacheKey.
   *
   * Returns: true if the set contained the specified element
   */
  @Synchronized
  internal fun removeRunnable(cacheKey: String, runnable: SerialRunnable) {
    map[cacheKey]?.remove(runnable)
  }

  /**
   * Return the HashSet of SerialRunnable which is associated with the same cacheKey.
   */
  @Synchronized
  internal fun removeRunnableSet(cacheKey: String): HashSet<SerialRunnable>? {
    return map.remove(cacheKey)
  }
}