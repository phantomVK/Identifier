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
        val id: String
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        val sign = when (val result = getSignatureHash()) {
          is CallBinderResult.Failed -> return result
          is CallBinderResult.Success -> result.id
        }

        try {
          data.writeInterfaceToken(descriptor)
          data.writeString(config.context.packageName)
          data.writeString(sign)
          data.writeString("OUID")
          binder.transact(1, data, reply, 0)
          reply.readException()
          id = reply.readString()!!
        } finally {
          reply.recycle()
          data.recycle()
        }

        return checkId(id)
      }
    }

    bindService(intent, binderCallback)
  }

//  private fun getIdName(name: String): String {
//    return when (name) {
//      "UDID" -> "GUID"
//      "OAID" -> "OUID"
//      "VAID" -> "DUID"
//      "AAID" -> "AUID"
//      else -> "OUID"
//    }
//  }
}