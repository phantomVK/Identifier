package com.phantomvk.identifier.provider

import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.qiku.id.IOAIDInterface

internal class QikuBinderProvider(config: ProviderConfig) : AbstractProvider(config) {

  private lateinit var iBinder: IBinder

  override fun isSupported(): Boolean {
    val clazz = Class.forName("android.os.ServiceManager")
    val method = clazz.getDeclaredMethod("getService", String::class.java)
    val binder = method.invoke(null, "qikuid") as? IBinder ?: return false

    iBinder = binder
    return true
  }

  override fun run() {
    val proxy = Proxy(iBinder)

    if (config.isLimitAdTracking) {
      if (proxy.isLimited) {
        getCallback().onError(EXCEPTION_THROWN)
        return
      }
    }

    checkId(proxy.oaid, getCallback())
  }
}

private class Proxy(private val mRemote: IBinder) : IOAIDInterface {
  override fun asBinder(): IBinder {
    return mRemote
  }

  @Throws(RemoteException::class)
  override fun isSupported(): Int {
    val _data = Parcel.obtain()
    val _reply = Parcel.obtain()
    val _result: Int = try {
      mRemote.transact(2, _data, _reply, 0)
      _reply.readException()
      _reply.readInt()
    } finally {
      _reply.recycle()
      _data.recycle()
    }
    return _result
  }

  @Throws(RemoteException::class)
  override fun getUDID(): String? {
    val _data = Parcel.obtain()
    val _reply = Parcel.obtain()
    val _result: String? = try {
      mRemote.transact(3, _data, _reply, 0)
      _reply.readException()
      _reply.readString()
    } finally {
      _reply.recycle()
      _data.recycle()
    }
    return _result
  }

  @Throws(RemoteException::class)
  override fun getOAID(): String? {
    val _data = Parcel.obtain()
    val _reply = Parcel.obtain()
    val _result: String? = try {
      mRemote.transact(4, _data, _reply, 0)
      _reply.readException()
      _reply.readString()
    } finally {
      _reply.recycle()
      _data.recycle()
    }
    return _result
  }

  @Throws(RemoteException::class)
  override fun getVAID(): String? {
    val _data = Parcel.obtain()
    val _reply = Parcel.obtain()
    val _result: String? = try {
      mRemote.transact(5, _data, _reply, 0)
      _reply.readException()
      _reply.readString()
    } finally {
      _reply.recycle()
      _data.recycle()
    }
    return _result
  }

  @Throws(RemoteException::class)
  override fun getAAID(): String? {
    val _data = Parcel.obtain()
    val _reply = Parcel.obtain()
    val _result: String? = try {
      mRemote.transact(6, _data, _reply, 0)
      _reply.readException()
      _reply.readString()
    } finally {
      _reply.recycle()
      _data.recycle()
    }
    return _result
  }

  @Throws(RemoteException::class)
  override fun shutdown() {
    val _data = Parcel.obtain()
    val _reply = Parcel.obtain()
    try {
      mRemote.transact(7, _data, _reply, 0)
      _reply.readException()
    } finally {
      _reply.recycle()
      _data.recycle()
    }
  }

  @Throws(RemoteException::class)
  override fun resetOAID() {
    val _data = Parcel.obtain()
    val _reply = Parcel.obtain()
    try {
      mRemote.transact(8, _data, _reply, 0)
      _reply.readException()
    } finally {
      _reply.recycle()
      _data.recycle()
    }
  }

  @Throws(RemoteException::class)
  override fun isLimited(): Boolean {
    val _data = Parcel.obtain()
    val _reply = Parcel.obtain()
    val _result: Boolean = try {
      mRemote.transact(9, _data, _reply, 0)
      _reply.readException()
      0 != _reply.readInt()
    } finally {
      _reply.recycle()
      _data.recycle()
    }
    return _result
  }
}