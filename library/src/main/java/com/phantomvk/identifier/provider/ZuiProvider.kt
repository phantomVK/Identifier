package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig

internal class ZuiProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.zui.deviceidservice")
  }

  override fun getInterfaceName(): String {
    return "com.zui.deviceidservice.IDeviceidInterface"
  }

  override fun run() {
    val intent = Intent().setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService")
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isVerifyLimitAdTracking) {
          if (!isSupport(binder)) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return when (val r = getId(binder, 1, true)) {
          is BinderResult.Failed -> r
          is BinderResult.Success -> {
            val vaid = queryId(IdEnum.VAID) { getId(binder, 4, true) }
            val aaid = queryId(IdEnum.AAID) { getId(binder, 5, true) }
            BinderResult.Success(r.id, vaid, aaid)
          }
        }
      }
    })
  }

  private fun isSupport(remote: IBinder): Boolean {
    return readBoolean(remote, 3, true, null)
  }
}