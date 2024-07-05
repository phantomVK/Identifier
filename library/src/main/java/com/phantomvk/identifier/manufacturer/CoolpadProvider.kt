package com.phantomvk.identifier.manufacturer

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.provider.Settings
import com.phantomvk.identifier.impl.Constants.AIDL_INTERFACE_IS_NULL
import com.phantomvk.identifier.impl.ServiceManager
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.coolpad.deviceidsupport.IDeviceIdManager

class CoolpadProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "CoolpadProvider"
  }

  override fun ifSupported(): Boolean {
    return isPackageInfoExisted("com.coolpad.deviceidsupport")
  }

  override fun execute() {
    if (tryContentResolver()) {
      return
    } else {
      tryService()
    }
  }

  private fun tryContentResolver(): Boolean {
    try {
      val id = Settings.Global.getString(config.context.contentResolver, "coolos.oaid")
      if (checkId(id) is CallBinderResult.Success) {
        getCallback().onSuccess(id)
        return true
      }
    } catch (ignore: Throwable) {
    }

    return false
  }

  private fun tryService() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IDeviceIdManager.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        val id = asInterface.getOAID(config.context.packageName)
        return checkId(id)
      }
    }

    val intent = Intent()
    val componentName = ComponentName(
      "com.coolpad.deviceidsupport",
      "com.coolpad.deviceidsupport.DeviceIdService"
    )
    intent.setComponent(componentName)
    ServiceManager.bindService(config.context, intent, getCallback(), binderCallback)
  }
}