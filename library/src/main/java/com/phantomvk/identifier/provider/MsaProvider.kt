package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.bun.lib.MsaIdInterface

internal class MsaProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return true // return isPackageInfoExisted("com.mdid.msa")
  }

  override fun run() {
    startMsaKlService()

    val intent = Intent("com.bun.msa.action.bindto.service")
      .setClassName("com.mdid.msa", "com.mdid.msa.service.MsaIdService")
      .putExtra("com.bun.msa.param.pkgname", config.context.packageName)

    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        val asInterface = MsaIdInterface.Stub.asInterface(binder)
        if (asInterface == null) {
          return BinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.verifyLimitAdTracking) {
          if (!asInterface.isSupported) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return checkId(asInterface.oaid)
      }
    })
  }

  private fun startMsaKlService(): Boolean {
    val intent = Intent("com.bun.msa.action.start.service")
      .setClassName("com.mdid.msa", "com.mdid.msa.service.MsaKlService")
      .putExtra("com.bun.msa.param.pkgname", config.context.packageName)

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