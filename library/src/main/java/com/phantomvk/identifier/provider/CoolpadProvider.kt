package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.provider.Settings
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.coolpad.deviceidsupport.IDeviceIdManager

internal class CoolpadProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.coolpad.deviceidsupport")
  }

  override fun run() {
    // Querying id from settings.
    try {
      val id = Settings.Global.getString(config.context.contentResolver, "coolos.oaid")
      if (checkId(id) is CallBinderResult.Success) {
        getCallback().onSuccess(id)
        return
      }
    } catch (ignore: Throwable) {
    }

    // Querying id from service.
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

    val componentName = ComponentName("com.coolpad.deviceidsupport", "com.coolpad.deviceidsupport.DeviceIdService")
    val intent = Intent().setComponent(componentName)
    bindService(intent, binderCallback)
  }
}