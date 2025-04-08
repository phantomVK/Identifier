package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import com.phantomvk.identifier.model.ProviderConfig

internal class QikuServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.qiku.id")
  }

  override fun getInterfaceName(): String {
    return "com.qiku.id.IOAIDInterface"
  }

  override fun run() {
    val intent = Intent("qiku.service.action.id").setPackage("com.qiku.id")
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isVerifyLimitAdTracking) {
          if (isLimited(binder)) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        return queryId(binder, 3, 4, 5)
      }
    })
  }

//  private fun isSupported(remote: IBinder): Int {
//    val data = Parcel.obtain()
//    val reply = Parcel.obtain()
//    try {
//      data.writeInterfaceToken("com.qiku.id.IOAIDInterface")
//      remote.transact(1, data, reply, 0)
//      reply.readException()
//      return reply.readInt()
//    } catch (t: Throwable) {
//      return 0
//    } finally {
//      reply.recycle()
//      data.recycle()
//    }
//  }

  private fun isLimited(remote: IBinder): Boolean {
    return readBoolean(remote, 8, false, null)
  }
}