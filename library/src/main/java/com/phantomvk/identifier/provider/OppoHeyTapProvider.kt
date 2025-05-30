package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig
import java.security.MessageDigest

internal open class OppoHeyTapProvider(
  config: ProviderConfig,
  private val descriptor: String = "com.heytap.openid.IOpenID",
  private val pkg: String = "com.heytap.openid",
  private val cls: String = "com.heytap.openid.IdentifyService",
  private val action: String = "action.com.heytap.openid.OPEN_ID_SERVICE"
) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted(pkg)
  }

  override fun run() {
    val intent = Intent(action).setComponent(ComponentName(pkg, cls))
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        val sign = when (val result = getSignatureHash()) {
          is BinderResult.Failed -> return result
          is BinderResult.Success -> result.id
        }

        when (val r = (getId(binder, descriptor, sign, "OAID"))) {
          is BinderResult.Failed -> return r
          is BinderResult.Success -> {
//            val vaid = invokeById(IdEnum.VAID) { getId(binder, descriptor, sign, "VAID") }
            val aaid = invokeById(IdEnum.AAID) { getId(binder, descriptor, sign, "AAID") }
            return BinderResult.Success(r.id, null, aaid)
          }
        }
      }
    })
  }

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

  private fun getSignatureHash(): BinderResult {
    val signature = getSignatures(config.context.packageManager, config.context.packageName)
      ?.firstOrNull()
      ?: return BinderResult.Failed(SIGNATURE_IS_NULL)

    val byteArray = signature.toByteArray()
    val sign = sha1(byteArray)
    if (sign.isNullOrBlank()) {
      return BinderResult.Failed(SIGNATURE_HASH_IS_NULL)
    }

    return BinderResult.Success(sign, null, null)
  }

  private fun getId(remote: IBinder, descriptor: String, sign: String, code: String): BinderResult {
    val idName = when (code) {
      "UDID" -> "GUID"
      "OAID" -> "OUID"
      "VAID" -> "DUID"
      "AAID" -> "AUID"
      else -> throw IllegalArgumentException("Unknown code: $code")
    }

    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken(descriptor)
      data.writeString(config.context.packageName)
      data.writeString(sign)
      data.writeString(idName)
      remote.transact(1, data, reply, 0)
      reply.readException()
      return checkId(reply.readString())
    } catch (t: Throwable) {
      return BinderResult.Failed(EXCEPTION_THROWN, t)
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}