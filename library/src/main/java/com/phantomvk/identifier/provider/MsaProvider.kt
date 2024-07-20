package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.impl.Constants.AIDL_INTERFACE_IS_NULL
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.impl.ServiceManager
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.bun.lib.MsaIdInterface

class MsaProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "MsaProvider"
  }

  override fun ifSupported(): Boolean {
    if (!IdentifierManager.getInstance().isExperimental) return false
    return isPackageInfoExisted("com.mdid.msa")
  }

  override fun execute() {
    startMsaKlService()

    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = MsaIdInterface.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          val isSupport = asInterface.isSupported
          if (!isSupport) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        val id = asInterface.oaid
        return checkId(id)
      }
    }

    val intent = Intent("com.bun.msa.action.bindto.service")
    intent.setClassName("com.mdid.msa", "com.mdid.msa.service.MsaIdService")
    intent.putExtra("com.bun.msa.param.pkgname", config.context.packageName)
    ServiceManager.bindService(config.context, intent, getCallback(), binderCallback)
  }

  private fun startMsaKlService(): Boolean {
    val intent = Intent("com.bun.msa.action.start.service")
    intent.setClassName("com.mdid.msa", "com.mdid.msa.service.MsaKlService")
    intent.putExtra("com.bun.msa.param.pkgname", config.context.packageName)

    return try {
      val componentName = if (Build.VERSION.SDK_INT >= 26) {
        config.context.startForegroundService(intent)
      } else {
        config.context.startService(intent)
      }

      componentName != null
    } catch (t: Throwable) {
      false
    }
  }
}