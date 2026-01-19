package com.phantomvk.identifier.provider

import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import com.phantomvk.identifier.model.ProviderConfig
import java.security.MessageDigest

internal abstract class OppoBaseProvider(config: ProviderConfig) : AbstractProvider(config) {
  private fun getSignatures(pm: PackageManager, packageName: String): Array<Signature>? {
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
      var index = 0
      val chars = CharArray(40)
      val digits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
      val byteArray = MessageDigest.getInstance("SHA1").digest(bytes)

      for (byte in byteArray) {
        val intValue = byte.toInt()
        chars[index++] = digits[intValue ushr 4 and 0xf]
        chars[index++] = digits[intValue and 0xf]
      }

      String(chars)
    } catch (t: Throwable) {
      null
    }
  }

  protected fun getSignatureHash(): BinderResult {
    val signature = getSignatures(config.context.packageManager, config.context.packageName)
      ?.firstOrNull()
      ?: return Failed(SIGNATURE_IS_NULL)

    val byteArray = signature.toByteArray()
    val sign = sha1(byteArray)
    if (sign.isNullOrBlank()) {
      return Failed(SIGNATURE_HASH_IS_NULL)
    }

    return Success(sign, null, null)
  }
}