package com.phantomvk.identifier.provider

import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.log.Log
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
    Log.d("QikuBinderProvider", "isSupported:${isSupported(binder)}, isLimited:${isLimited()}")
    if (config.isLimitAdTracking) {
      if (isSupported(binder)) {
        getCallback().onError(EXCEPTION_THROWN)
        return
      }
    }

    when (val result = checkId(getId(4))) {
      is CallBinderResult.Failed -> {
        getCallback().onError(result.msg)
      }

      is CallBinderResult.Success -> {
        val vaid = if (config.queryVaid) getId(5) else null
        val aaid = if (config.queryAaid) getId(6) else null
        getCallback().onSuccess(IdentifierResult(result.id, aaid, vaid))
      }
    }
  }

  private fun isSupported(binder: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    return try {
      binder.transact(2, data, reply, 0)
      reply.readInt() == 1
    } catch (t: Throwable) {
      false
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun getId(code: Int): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    return try {
      binder.transact(code, data, reply, 0)
      reply.readString()
    } catch (t: Throwable) {
      null
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun isLimited(): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    return try {
      binder.transact(9, data, reply, 0)
      reply.readBoolean()
    } catch (t: Throwable) {
      false
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}