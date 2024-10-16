package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig

internal class AsusProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.asus.msa.SupplementaryDID")
  }

  override fun run() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        if (config.isLimitAdTracking) {
          if (!isSupport(binder)) {
            return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        when (val result = checkId(getId(binder, 3))) {
          is CallBinderResult.Failed -> return result
          is CallBinderResult.Success -> {
            val vaid = if (config.queryVaid) getId(binder, 4) else null
            val aaid = if (config.queryAaid) getId(binder, 5) else null
            return CallBinderResult.Success(result.id, vaid, aaid)
          }
        }
      }
    }

    val intent = Intent("com.asus.msa.action.ACCESS_DID")
    val componentName = ComponentName("com.asus.msa.SupplementaryDID", "com.asus.msa.SupplementaryDID.SupplementaryDIDService")
    intent.setComponent(componentName)
    bindService(intent, binderCallback)
  }

  private fun isSupport(remote: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val result: Boolean
    try {
      data.writeInterfaceToken("com.asus.msa.SupplementaryDID.IDidAidlInterface")
      remote.transact(1, data, reply, 0)
      reply.readException()
      result = (0 != reply.readInt())
    } finally {
      reply.recycle()
      data.recycle()
    }
    return result
  }

  private fun getId(remote: IBinder, code: Int): String? {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val result: String?
    try {
      data.writeInterfaceToken("com.asus.msa.SupplementaryDID.IDidAidlInterface")
      remote.transact(code, data, reply, 0)
      reply.readException()
      result = reply.readString()
    } finally {
      reply.recycle()
      data.recycle()
    }
    return result
  }
}