package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig

internal open class OppoHeyTapProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.heytap.openid")
  }

  override fun run() {
    getId(
      "com.heytap.openid.IOpenID",
      "com.heytap.openid",
      "com.heytap.openid.IdentifyService",
      "action.com.heytap.openid.OPEN_ID_SERVICE"
    )
  }

  protected fun getId(descriptor: String, pkg: String, cla: String, action: String) {
    val component = ComponentName(pkg, cla)
    val intent = Intent(action).setComponent(component)
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val sign = when (val result = getSignatureHash()) {
          is CallBinderResult.Failed -> return result
          is CallBinderResult.Success -> result.id
        }

        when (val result = checkId((getId(binder, descriptor, sign, "OAID")))) {
          is CallBinderResult.Failed -> return result
          is CallBinderResult.Success -> {
            val vaid = if (config.queryVaid) getId(binder, descriptor, sign, "VAID") else null
            val aaid = if (config.queryAaid) getId(binder, descriptor, sign, "AAID") else null
            return CallBinderResult.Success(result.id, vaid, aaid)
          }
        }
      }
    }

    bindService(intent, binderCallback)
  }

  private fun getId(binder: IBinder, descriptor: String, sign: String, code: String): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    return try {
      data.writeInterfaceToken(descriptor)
      data.writeString(config.context.packageName)
      data.writeString(sign)
      data.writeString(getIdName(code))
      binder.transact(1, data, reply, 0)
      reply.readException()
      val id = reply.readString()
      if (id.isNullOrBlank()) null else id
    } catch (t: Throwable) {
      null
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun getIdName(name: String): String {
    return when (name) {
      "UDID" -> "GUID"
      "OAID" -> "OUID"
      "VAID" -> "DUID"
      "AAID" -> "AUID"
      else -> throw IllegalArgumentException("Unknown id name: $name")
    }
  }
}