package com.phantomvk.identifier.provider

import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class QikuBinderProvider(config: ProviderConfig) : AbstractProvider(config) {

  private lateinit var remote: IBinder

  override fun isSupported(): Boolean {
    val clazz = Class.forName("android.os.ServiceManager")
    val method = clazz.getDeclaredMethod("getService", String::class.java)
    remote = method.invoke(null, "qikuid") as? IBinder ?: return false
    return true
  }

  override fun run() {
    if (config.verifyLimitAdTracking) {
      if (isLimited()) {
        getConsumer().onError(LIMIT_AD_TRACKING_IS_ENABLED)
        return
      }
    }

    when (val r = getId(4)) {
      is BinderResult.Failed -> getConsumer().onError(r.msg, r.throwable)
      is BinderResult.Success -> {
//        val vaid = queryId(IdEnum.VAID) { getId(5) }
//        val aaid = queryId(IdEnum.AAID) { getId(6) }
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
      remote.transact(code, data, reply, 0)
      return checkId(reply.readString())
    } catch (t: Throwable) {
      return BinderResult.Failed(EXCEPTION_THROWN, t)
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun isLimited(): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      remote.transact(9, data, reply, 0)
      return reply.readBoolean()
    } catch (t: Throwable) {
      return false
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}