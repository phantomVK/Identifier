package com.phantomvk.identifier.provider

import android.os.IBinder
import android.os.Parcel
import android.os.ServiceManager
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class QikuBinderProvider(config: ProviderConfig) : AbstractProvider(config) {

  private lateinit var iBinder: IBinder

  override fun isSupported(): Boolean {
    iBinder = ServiceManager.getService("qikuid") ?: return false
    return true
  }

  override fun run() {
    if (config.isVerifyLimitAdTracking) {
      if (isLimited()) {
        getConsumer().onError(LIMIT_AD_TRACKING_IS_ENABLED)
        return
      }
    }

    when (val r = getId(4)) {
      is Failed -> getConsumer().onError(r.msg, r.throwable)
      is Success -> {
//        val vaid = invokeById(IdEnum.VAID) { getId(5) }
//        val aaid = invokeById(IdEnum.AAID) { getId(6) }
        getConsumer().onSuccess(IdentifierResult(r.id))
      }
    }
  }

//  private fun isSupported(remote: IBinder): Boolean {
//    val data = Parcel.obtain()
//    val reply = Parcel.obtain()
//    return try {
//      remote.transact(2, data, reply, 0)
//      reply.readInt() == 1
//    } catch (t: Throwable) {
//      false
//    } finally {
//      reply.recycle()
//      data.recycle()
//    }
//  }

  private fun getId(code: Int): BinderResult {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeString(config.context.packageName)
      iBinder.transact(code, data, reply, 0)
      return checkId(reply.readString())
    } catch (t: Throwable) {
      return Failed(EXCEPTION_THROWN, t)
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun isLimited(): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      iBinder.transact(9, data, reply, 0)
      return reply.readBoolean()
    } catch (t: Throwable) {
      return false
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}