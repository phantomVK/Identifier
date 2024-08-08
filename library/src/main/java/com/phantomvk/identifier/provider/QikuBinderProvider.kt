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

    val id = proxy.oaid
    checkId(id, getCallback())
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
      mRemote.transact(TRANSACTION_isSupported, _data, _reply, 0)
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
      mRemote.transact(TRANSACTION_getUDID, _data, _reply, 0)
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
      mRemote.transact(TRANSACTION_getOAID, _data, _reply, 0)
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
      mRemote.transact(TRANSACTION_getVAID, _data, _reply, 0)
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
      mRemote.transact(TRANSACTION_getAAID, _data, _reply, 0)
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
      mRemote.transact(TRANSACTION_shutdown, _data, _reply, 0)
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
      mRemote.transact(TRANSACTION_resetOAID, _data, _reply, 0)
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
      mRemote.transact(TRANSACTION_isLimited, _data, _reply, 0)
      _reply.readException()
      0 != _reply.readInt()
    } finally {
      _reply.recycle()
      _data.recycle()
    }
    return _result
  }
}

private const val TRANSACTION_isSupported = IBinder.FIRST_CALL_TRANSACTION + 1
private const val TRANSACTION_getUDID = IBinder.FIRST_CALL_TRANSACTION + 2
private const val TRANSACTION_getOAID = IBinder.FIRST_CALL_TRANSACTION + 3
private const val TRANSACTION_getVAID = IBinder.FIRST_CALL_TRANSACTION + 4
private const val TRANSACTION_getAAID = IBinder.FIRST_CALL_TRANSACTION + 5
private const val TRANSACTION_shutdown = IBinder.FIRST_CALL_TRANSACTION + 6
private const val TRANSACTION_resetOAID = IBinder.FIRST_CALL_TRANSACTION + 7
private const val TRANSACTION_isLimited = IBinder.FIRST_CALL_TRANSACTION + 8