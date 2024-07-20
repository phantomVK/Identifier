package com.phantomvk.identifier.provider

import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import com.phantomvk.identifier.impl.Constants.EXCEPTION_THROWN
import com.phantomvk.identifier.impl.Constants.ID_IS_INVALID
import com.phantomvk.identifier.impl.Constants.ID_IS_NULL_OR_BLANK
import com.phantomvk.identifier.impl.Constants.SIGNATURE_HASH_IS_NULL
import com.phantomvk.identifier.impl.Constants.SIGNATURE_IS_NULL
import com.phantomvk.identifier.interfaces.OnResultListener
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import java.security.MessageDigest

abstract class AbstractProvider(protected val config: ProviderConfig) : Runnable {

  private lateinit var resultCallback: OnResultListener

  abstract fun getTag(): String

  protected abstract fun ifSupported(): Boolean

  protected abstract fun execute()

  fun isSupported(): Boolean {
    return try {
      ifSupported()
    } catch (t: Throwable) {
      false
    }
  }

  override fun run() {
    try {
      execute()
    } catch (t: Throwable) {
      getCallback().onError(EXCEPTION_THROWN, t)
    }
  }

  fun setCallback(callback: OnResultListener) {
    resultCallback = callback
  }

  protected fun getCallback(): OnResultListener {
    return resultCallback
  }

  protected fun isPackageInfoExisted(packageName: String): Boolean {
    return try {
      val manager = config.context.packageManager
      manager.getPackageInfo(packageName, 0) != null
    } catch (t: Throwable) {
      false
    }
  }

  protected fun isContentProviderExisted(packageName: String): Boolean {
    return try {
      val manager = config.context.packageManager
      manager.resolveContentProvider(packageName, 0) != null
    } catch (t: Throwable) {
      false
    }
  }

  protected fun checkId(id: String?, callback: OnResultListener? = null): CallBinderResult {
    if (id.isNullOrBlank()) {
      callback?.onError(ID_IS_NULL_OR_BLANK)
      return CallBinderResult.Failed(ID_IS_NULL_OR_BLANK)
    }

    if (id.all { it == '0' || it == '-' }) {
      callback?.onError(ID_IS_INVALID)
      return CallBinderResult.Failed(ID_IS_INVALID)
    }

    callback?.onSuccess(id)
    return CallBinderResult.Success(id)
  }

  private fun getSignatures(pm: PackageManager, packageName: String): Array<Signature>? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      val flag = PackageManager.GET_SIGNING_CERTIFICATES
      val info = pm.getPackageInfo(packageName, flag)?.signingInfo ?: return null

      return if (info.hasMultipleSigners()) {
        info.apkContentsSigners
      } else {
        info.signingCertificateHistory
      }
    }

    val flag = PackageManager.GET_SIGNATURES
    val signatures = pm.getPackageInfo(packageName, flag)?.signatures
    if (signatures.isNullOrEmpty() || signatures[0] == null) {
      return null
    }

    return signatures
  }

  /**
   * Calculate hash value using specified algorithm.
   */
  private fun hash(algorithm: String, bytes: ByteArray): String? {
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

  protected fun getSignatureHash(): CallBinderResult {
    val pkgManager = config.context.packageManager
    val pkgName = config.context.packageName
    val signature = getSignatures(pkgManager, pkgName)?.firstOrNull()
    if (signature == null) {
      return CallBinderResult.Failed(SIGNATURE_IS_NULL)
    }

    val byteArray = signature.toByteArray()
    val sign = hash("SHA1", byteArray)
    if (sign.isNullOrBlank()) {
      return CallBinderResult.Failed(SIGNATURE_HASH_IS_NULL)
    }

    return CallBinderResult.Success(sign)
  }
}