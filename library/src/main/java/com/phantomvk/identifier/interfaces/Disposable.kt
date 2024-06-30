package com.phantomvk.identifier.interfaces

/**
 * Represents a disposable resource.
 */
interface Disposable {
  /**
   * Dispose the resource, the operation should be idempotent.
   */
  fun dispose()

  /**
   * Returns true if this resource has been disposed.
   * @return true if this resource has been disposed
   */
  fun isDisposed(): Boolean
}
