package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.log.Log
import com.phantomvk.identifier.model.ProviderConfig

internal class QikuServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.qiku.id")
  }

  override fun run() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        Log.d("QikuServiceProvider", "isSupported:${isSupported(binder)}, isLimited:${isLimited(binder)}")
        if (config.isLimitAdTracking) {
          if (isSupported(binder) == 0) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        when (val r = checkId(getId(binder, 3))) {
          is BinderResult.Failed -> return r
          is BinderResult.Success -> {
            val vaid = if (config.queryVaid) getId(binder, 4) else null
            val aaid = if (config.queryAaid) getId(binder, 5) else null
            return BinderResult.Success(r.id, vaid, aaid)
          }
        }
      }
    }

    val intent = Intent("qiku.service.action.id")
    intent.setPackage("com.qiku.id")
    bindService(intent, binderCallback)
  }

  private fun isSupported(binder: IBinder): Int {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.qiku.id.IOAIDInterface")
      binder.transact(1, data, reply, 0)
      reply.readException()
      return reply.readInt()
    } catch (t: Throwable) {
      return 0
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun getId(binder: IBinder, code: Int): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.qiku.id.IOAIDInterface")
      binder.transact(code, data, reply, 0)
      reply.readException()
      return reply.readString()
    } catch (t: Throwable) {
      return null
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

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