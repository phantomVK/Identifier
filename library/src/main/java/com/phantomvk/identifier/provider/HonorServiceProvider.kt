package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.hihonor.cloudservice.oaid.IOAIDCallBack
import java.util.concurrent.CountDownLatch

internal class HonorServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.hihonor.id")
  }

  override fun run() {
    val intent = Intent("com.hihonor.id.HnOaIdService").setPackage("com.hihonor.id")
    bindService(intent, object : BinderCallback {
      override fun call(binder: IBinder): BinderResult {
        if (config.isLimitAdTracking) {
          val result = isLimited(binder)
          if (result != null) {
            return result
          }
        }

        return getId(binder) ?: BinderResult.Failed(ID_INFO_IS_NULL)
      }
    })
  }

  private fun getId(remote: IBinder): BinderResult? {
    var result: BinderResult? = null
    val latch = CountDownLatch(1)
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val callback = object : IOAIDCallBack.Stub() {
      override fun a(i: Int, j: Long, z: Boolean, f: Float, d: Double, str: String?) {}
      override fun onResult(i: Int, bundle: Bundle?) {
        if (i != 0 || bundle == null) {
          result = BinderResult.Failed(BUNDLE_IS_NULL)
          latch.countDown()
          return
        }

        result = checkId(bundle.getString("oa_id_flag"))
        latch.countDown()
      }
    }

    try {
      data.writeInterfaceToken("com.hihonor.cloudservice.oaid.IOAIDService")
      data.writeStrongInterface(callback)
      remote.transact(2, data, reply, 0)
      reply.readException()
    } catch (t: Throwable) {
    } finally {
      reply.recycle()
      data.recycle()
    }

    latch.await()
    return result
  }

  private fun isLimited(remote: IBinder): BinderResult? {
    var result: BinderResult? = null
    val latch = CountDownLatch(1)
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    val callback = object : IOAIDCallBack.Stub() {
      override fun a(i: Int, j: Long, z: Boolean, f: Float, d: Double, str: String?) {}
      override fun onResult(i: Int, bundle: Bundle?) {
        if (i == 0 && bundle?.getBoolean("oa_id_limit_state") == true) {
          result = BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
        }
        latch.countDown()
      }
    }

    try {
      data.writeInterfaceToken("com.hihonor.cloudservice.oaid.IOAIDService")
      data.writeStrongInterface(callback)
      remote.transact(3, data, reply, 0)
      reply.readException()
    } catch (t: Throwable) {
    } finally {
      reply.recycle()
      data.recycle()
    }

    latch.await()
    return result
  }
}
