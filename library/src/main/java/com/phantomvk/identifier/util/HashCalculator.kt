package com.phantomvk.identifier.util

import java.security.MessageDigest

object HashCalculator {

  /**
   * Calculate hash value using specified algorithm.
   */
  fun hash(algorithm: String, bytes: ByteArray): String? {
    if (bytes.isEmpty()) return null

    return try {
      val sb = StringBuilder()
      val byteArray = MessageDigest.getInstance(algorithm).digest(bytes)

      for (byte in byteArray) {
        sb.append(String.format("%02x", byte))
      }

      sb.toString()
    } catch (t: Throwable) {
      null
    }
  }
}