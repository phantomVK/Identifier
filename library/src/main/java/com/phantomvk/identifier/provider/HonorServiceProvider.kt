package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.hihonor.cloudservice.oaid.IOAIDCallBack

internal class HonorServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.hihonor.id")
  }

  override fun run() {
    val intent = Intent("com.hihonor.id.HnOaIdService").setPackage("com.hihonor.id")
    bindService(intent) { binder ->
      if (config.isVerifyLimitAdTracking) {
        isLimited(binder) { result ->
          if (result is BinderResult.Success) {
            getId(binder) { r -> verifyResult(r) }
          } else {
            verifyResult(result)
          }
        }
      } else {
        getId(binder) { r -> verifyResult(r) }
      }
    }
  }

  private fun getId(remote: IBinder, callback: OnResultCallback) {
    val callback = object : IOAIDCallBack.Stub() {
      override fun a(i: Int, j: Long, z: Boolean, f: Float, d: Double, str: String?) {}
      override fun onResult(i: Int, bundle: Bundle?) {
        if (i != 0 || bundle == null) {
          callback.call(BinderResult.Failed(BUNDLE_IS_NULL))
        } else {
          callback.call(checkId(bundle.getString("oa_id_flag")))
        }
      }
    }

    callBinder(remote, callback, 2)
  }

  private fun isLimited(remote: IBinder, callback: OnResultCallback) {
    val callback = object : IOAIDCallBack.Stub() {
      override fun a(i: Int, j: Long, z: Boolean, f: Float, d: Double, str: String?) {}
      override fun onResult(i: Int, bundle: Bundle?) {
        if (i == 0 && bundle?.getBoolean("oa_id_limit_state") == true) {
          callback.call(BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED))
        } else {
          callback.call(BinderResult.Success("", "", ""))
        }
      }
    }

    callBinder(remote, callback, 3)
  }

  private fun callBinder(remote: IBinder, callback: IOAIDCallBack, code: Int) {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.hihonor.cloudservice.oaid.IOAIDService")
      data.writeStrongInterface(callback)
      remote.transact(code, data, reply, 0)
      reply.readException()
    } catch (t: Throwable) {
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  private fun interface OnResultCallback {
    fun call(result: BinderResult)
  }
}
