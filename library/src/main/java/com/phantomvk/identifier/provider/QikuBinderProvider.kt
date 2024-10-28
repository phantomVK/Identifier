package com.phantomvk.identifier.provider

import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class QikuBinderProvider(config: ProviderConfig) : AbstractProvider(config) {

  private lateinit var binder: IBinder

  override fun isSupported(): Boolean {
    val clazz = Class.forName("android.os.ServiceManager")
    val method = clazz.getDeclaredMethod("getService", String::class.java)
    binder = method.invoke(null, "qikuid") as? IBinder ?: return false
    return true
  }

  override fun run() {
    if (config.isLimitAdTracking) {
      if (isLimited()) {
        getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
        return
      }
    }

    when (val r = getId(4)) {
      is BinderResult.Failed -> getCallback().onError(r.msg, r.throwable)
      is BinderResult.Success -> {
//        val vaid = if (config.queryVaid) (getId(5) as? BinderResult.Success)?.id else null
//        val aaid = if (config.queryAaid) (getId(6) as? BinderResult.Success)?.id else null
        getCallback().onSuccess(IdentifierResult(r.id))
      }
    }
  }

//  private fun isSupported(binder: IBinder): Boolean {
//    val data = Parcel.obtain()
//    val reply = Parcel.obtain()
//    return try {
//      binder.transact(2, data, reply, 0)
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
      binder.transact(code, data, reply, 0)
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
      binder.transact(9, data, reply, 0)
      return reply.readBoolean()
    } catch (t: Throwable) {
      return false
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}