package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.bun.lib.MsaIdInterface

internal class MsaProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.mdid.msa")
  }

  override fun run() {
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

        return checkId(asInterface.oaid)
      }
    }

    val intent = Intent("com.bun.msa.action.bindto.service")
    intent.setClassName("com.mdid.msa", "com.mdid.msa.service.MsaIdService")
    intent.putExtra("com.bun.msa.param.pkgname", config.context.packageName)
    bindService(intent, binderCallback)
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