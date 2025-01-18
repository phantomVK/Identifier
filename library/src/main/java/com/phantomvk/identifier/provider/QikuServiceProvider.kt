package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import android.os.Parcel
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
        if (config.isLimitAdTracking) {
          if (isLimited(binder)) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        when (val r = getId(binder, 3, true)) {
          is BinderResult.Failed -> return r
          is BinderResult.Success -> {
            val vaid = queryId(IdEnum.VAID) { getId(binder, 4, true) }
            val aaid = queryId(IdEnum.AAID) { getId(binder, 5, true) }
            return BinderResult.Success(r.id, vaid, aaid)
          }
        }
      }
    })
  }

//  private fun isSupported(binder: IBinder): Int {
//    val data = Parcel.obtain()
//    val reply = Parcel.obtain()
//    try {
//      data.writeInterfaceToken("com.qiku.id.IOAIDInterface")
//      binder.transact(1, data, reply, 0)
//      reply.readException()
//      return reply.readInt()
//    } catch (t: Throwable) {
//      return 0
//    } finally {
//      reply.recycle()
//      data.recycle()
//    }
//  }

  private fun isLimited(binder: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.qiku.id.IOAIDInterface")
      binder.transact(8, data, reply, 0)
      reply.readException()
      return (0 != reply.readInt())
    } catch (t: Throwable) {
      return false
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}