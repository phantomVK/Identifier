package com.phantomvk.identifier.model

class IdentifierResult(
  val oaid: String,
  val aaid: String? = null,
  val vaid: String? = null,
  val gaid: String? = null
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as IdentifierResult

    if (oaid != other.oaid) return false
    if (aaid != other.aaid) return false
    if (vaid != other.vaid) return false
    if (gaid != other.gaid) return false

    return true
  }

  override fun hashCode(): Int {
    var result = oaid.hashCode()
    result = 31 * result + (aaid?.hashCode() ?: 0)
    result = 31 * result + (vaid?.hashCode() ?: 0)
    result = 31 * result + (gaid?.hashCode() ?: 0)
    return result
  }
}