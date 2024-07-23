package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.phantomvk.identifier.impl.Constants.AIDL_INTERFACE_IS_NULL
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.uodis.opendevice.aidl.OpenDeviceIdentifierService

class HuaweiServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  private val name by lazy(LazyThreadSafetyMode.NONE) {
    listOf(
      "com.huawei.hwid",
      "com.huawei.hwid.tv",
      "com.huawei.hms"
    ).firstOrNull { isPackageInfoExisted(it) }
  }

  override fun getTag(): String {
    return "HuaweiServiceProvider"
  }

  override fun ifSupported(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return false
    }

    return name != null
  }

  override fun execute() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = OpenDeviceIdentifierService.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          val isLimited = asInterface.isOaidTrackLimited
          if (isLimited) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        val id = asInterface.oaid
        return checkId(id)
      }
    }

    val intent = Intent("com.uodis.opendevice.OPENIDS_SERVICE")
    intent.setPackage(name)
    bindService(config.context, intent, getCallback(), binderCallback)
  }
}
