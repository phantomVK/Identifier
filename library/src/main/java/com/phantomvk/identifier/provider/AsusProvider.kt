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

  override fun getInterfaceName(): String {
    return "com.asus.msa.SupplementaryDID.IDidAidlInterface"
  }

  // udid:2, oaid:3, vaid:4, aaid:5
  override fun run() {
    val componentName = ComponentName("com.asus.msa.SupplementaryDID", "com.asus.msa.SupplementaryDID.SupplementaryDIDService")
    val intent = Intent("com.asus.msa.action.ACCESS_DID").setComponent(componentName)
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isLimitAdTracking) {
          if (!isSupport(binder)) {
            return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
          }
        }

        when (val r = getId(binder, 3)) {
          is BinderResult.Failed -> return r
          is BinderResult.Success -> {
            val vaid = queryId(IdEnum.VAID) { getId(binder, 4) }
            val aaid = queryId(IdEnum.AAID) { getId(binder, 5) }
            return BinderResult.Success(r.id, vaid, aaid)
          }
        }
      }
    })
  }

  private fun isSupport(remote: IBinder): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.asus.msa.SupplementaryDID.IDidAidlInterface")
      remote.transact(1, data, reply, 0)
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