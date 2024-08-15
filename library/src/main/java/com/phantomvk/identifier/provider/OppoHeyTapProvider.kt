package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.heytap.openid.IOpenID

internal class OppoHeyTapProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.heytap.openid")
  }

  override fun run() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IOpenID.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        val sign = when (val result = getSignatureHash()) {
          is CallBinderResult.Failed -> return result
          is CallBinderResult.Success -> result.id
        }

        val id = asInterface.getSerID(config.context.packageName, sign, "OUID")
        return checkId(id)
      }
    }

    val component = ComponentName("com.heytap.openid", "com.heytap.openid.IdentifyService")
    val intent = Intent("action.com.heytap.openid.OPEN_ID_SERVICE").setComponent(component)
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