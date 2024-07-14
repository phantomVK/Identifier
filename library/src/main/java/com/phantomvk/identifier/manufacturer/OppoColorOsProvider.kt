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
import com.phantomvk.identifier.util.getSignatures
import com.phantomvk.identifier.util.hash
import generated.com.oplus.stdid.IStdID

class OppoColorOsProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "OppoColorOsProvider"
  }

  override fun ifSupported(): Boolean {
    return isPackageInfoExisted("com.coloros.mcs")
  }

  override fun execute() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IStdID.Stub.asInterface(binder)
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
        val sign = hash("SHA1", byteArray)
        if (sign.isNullOrBlank()) {
          return CallBinderResult.Failed(SIGNATURE_HASH_IS_NULL)
        }

        val id = asInterface.getSerID(pkgName, sign, "OUID")
        return checkId(id)
      }
    }

    val pkg = "com.coloros.mcs"
    val cls = "com.oplus.stdid.IdentifyService"
    val component = ComponentName(pkg, cls)
    val intent = Intent("action.com.oplus.stdid.ID_SERVICE").setComponent(component)
    ServiceManager.bindService(config.context, intent, getCallback(), binderCallback)
  }
}