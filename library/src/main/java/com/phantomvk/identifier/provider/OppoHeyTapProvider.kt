package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.RemoteException
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
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = OppoOpenId(binder, descriptor)
//        if (asInterface == null) {
//          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
//        }

        val sign = when (val result = getSignatureHash()) {
          is CallBinderResult.Failed -> return result
          is CallBinderResult.Success -> result.id
        }

        val id = asInterface.getSerID(config.context.packageName, sign, "OUID")
        return checkId(id)
      }
    }

    val component = ComponentName(pkg, cla)
    val intent = Intent(action).setComponent(component)
    bindService(intent, binderCallback)
  }

  private class OppoOpenId(
    private val remote: IBinder,
    private val descriptor: String
  ) : IInterface {
    override fun asBinder(): IBinder { return remote }

    @Throws(RemoteException::class)
    fun getSerID(pkgName: String, sign: String, type: String): String {
      val data = Parcel.obtain()
      val reply = Parcel.obtain()
      val result: String
      try {
        data.writeInterfaceToken(descriptor)
        data.writeString(pkgName)
        data.writeString(sign)
        data.writeString(type)
        remote.transact(1, data, reply, 0)
        reply.readException()
        result = reply.readString()!!
      } finally {
        reply.recycle()
        data.recycle()
      }
      return result
    }
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