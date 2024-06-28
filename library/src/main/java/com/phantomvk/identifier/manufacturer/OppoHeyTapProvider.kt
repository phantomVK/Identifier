package com.phantomvk.identifier.manufacturer

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.impl.Constants.AIDL_INTERFACE_IS_NULL
import com.phantomvk.identifier.impl.Constants.SIGNATURE_HASH_IS_NULL
import com.phantomvk.identifier.impl.Constants.SIGNATURE_IS_NULL
import com.phantomvk.identifier.impl.ServiceManager
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import com.phantomvk.identifier.util.HashCalculator
import com.phantomvk.identifier.util.getSignatures
import generated.com.heytap.openid.IOpenID

class OppoHeyTapProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "OppoHeyTapProvider"
  }

  override fun ifSupported(): Boolean {
    return isPackageInfoExisted("com.heytap.openid")
  }

  override fun execute() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IOpenID.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        val pkgManager = config.context.packageManager
        val pkgName = config.context.packageName
        val signature = getSignatures(pkgManager, pkgName)?.firstOrNull()
        if (signature == null) {
          return CallBinderResult.Failed(SIGNATURE_IS_NULL)
        }

        val byteArray = signature.toByteArray()
        val sign = HashCalculator.hash("SHA1", byteArray)
        if (sign.isNullOrBlank()) {
          return CallBinderResult.Failed(SIGNATURE_HASH_IS_NULL)
        }

        val id = asInterface.getSerID(pkgName, sign, "OUID")
        return checkId(id)
      }
    }

    val intent = Intent("action.com.heytap.openid.OPEN_ID_SERVICE")
    val component = ComponentName("com.heytap.openid", "com.heytap.openid.IdentifyService")
    intent.setComponent(component)
    ServiceManager.bindService(config.context, intent, getCallback(), binderCallback)
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