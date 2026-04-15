package com.phantomvk.identifier.provider

import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import com.phantomvk.identifier.internal.CacheCenter
import com.phantomvk.identifier.model.ProviderConfig
import java.security.MessageDigest

internal abstract class OppoBaseProvider(config: ProviderConfig) : AbstractProvider(config) {
  private fun getSignatures(): Array<Signature>? {
    val pm = config.context.packageManager
    val packageName = config.context.packageName

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      val info = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)?.signingInfo ?: return null
      return if (info.hasMultipleSigners()) {
        info.apkContentsSigners
      } else {
        info.signingCertificateHistory
      }
    }

    val signatures = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)?.signatures
    if (signatures.isNullOrEmpty() || signatures[0] == null) {
      return null
    }

    return signatures
  }

  /**
   * Calculate hash value using specified algorithm.
   */
  private fun sha1(bytes: ByteArray): String? {
    if (bytes.isEmpty()) return null

    return try {
      val chars = CharArray(40)
      val hexDigits = CacheCenter.HEX_DIGITS
      val byteArray = MessageDigest.getInstance("SHA1").digest(bytes)

      for (index in byteArray.indices) {
        val intValue = byteArray[index].toInt() and 0xFF
        chars[index * 2] = hexDigits[intValue ushr 4]
        chars[index * 2 + 1] = hexDigits[intValue and 0x0F]
      }

      String(chars)
    } catch (_: Throwable) {
      null
    }
  }

  protected fun getSignatureHash(): BinderResult {
    val signature = getSignatures()?.firstOrNull() ?: return Failed(SIGNATURE_IS_NULL)
    val sign = sha1(signature.toByteArray())
    if (sign.isNullOrBlank()) {
      return Failed(SIGNATURE_HASH_IS_NULL)
    }

    return Success(sign, null, null)
  }

  protected enum class OppoID(val id: String) {
    UDID("GUID"),
    OAID("OUID"),
    VAID("DUID"),
    AAID("AUID");
  }
}