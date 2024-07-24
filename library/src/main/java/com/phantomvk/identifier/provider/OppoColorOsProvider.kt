package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.impl.Constants.AIDL_INTERFACE_IS_NULL
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
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

        val sign = when (val result = getSignatureHash()) {
          is CallBinderResult.Failed -> return result
          is CallBinderResult.Success -> result.id
        }

        val pkgName = config.context.packageName
        val id = asInterface.getSerID(pkgName, sign, "OUID")
        return checkId(id)
      }
    }

    val pkg = "com.coloros.mcs"
    val cls = "com.oplus.stdid.IdentifyService"
    val component = ComponentName(pkg, cls)
    val intent = Intent("action.com.oplus.stdid.ID_SERVICE").setComponent(component)
    bindService(intent, binderCallback)
  }
}