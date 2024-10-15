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
    Log.d("QikuBinderProvider", "isSupported:${isIdSupported()}, isLimited:${isLimited()}")

    if (config.isLimitAdTracking) {
      if (isIdSupported()) {
        getCallback().onError(EXCEPTION_THROWN)
        return
      }
    }

    when (val result = checkId(getOAID())) {
      is CallBinderResult.Failed -> {
        getCallback().onError(result.msg)
      }

      is CallBinderResult.Success -> {
        val aaid = if (config.queryAaid) getAAID() else null
        val vaid = if (config.queryVaid) getVAID() else null
        getCallback().onSuccess(IdentifierResult(result.id, aaid, vaid))
      }
    }
  }

  private fun isIdSupported(): Boolean {
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

  private fun getOAID(): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    return try {
      binder.transact(4, data, reply, 0)
      reply.readString()
    } catch (t: Throwable) {
      null
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun getVAID(): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    return try {
      binder.transact(5, data, reply, 0)
      reply.readString()
    } catch (t: Throwable) {
      null
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun getAAID(): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    return try {
      binder.transact(6, data, reply, 0)
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