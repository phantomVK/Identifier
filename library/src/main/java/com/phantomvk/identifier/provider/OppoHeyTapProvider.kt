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
) : OppoBaseProvider(config) {

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