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
    bindService(intent) { b -> if (config.isVerifyLimitAdTracking) callBinder(3, b) else callBinder(2, b) }
  }

  /**
   * @param code 2:getId(), 3:isLimited()
   * @param remote binder instance
   */
  private fun callBinder(code: Int, remote: IBinder) {
    val callback = object : IOAIDCallBack.Stub() {
      override fun a(i: Int, j: Long, z: Boolean, f: Float, d: Double, str: String?) {}
      override fun onResult(i: Int, bundle: Bundle?) {
        when (code) {
          2 -> { // getId()
            if (i == 0 && bundle != null) {
              verifyResult(checkId(bundle.getString("oa_id_flag")))
            } else {
              getConsumer().onError(BUNDLE_IS_NULL)
            }
          }

          3 -> { // isLimited()
            if (i == 0 && bundle?.getBoolean("oa_id_limit_state") == true) {
              getConsumer().onError(LIMIT_AD_TRACKING_IS_ENABLED)
            } else {
              callBinder(2, remote)
            }
          }
        }
      }
    }

    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken("com.hihonor.cloudservice.oaid.IOAIDService")
      data.writeStrongInterface(callback)
      remote.transact(code, data, reply, 0)
      reply.readException()
    } catch (t: Throwable) {
      getConsumer().onError(EXCEPTION_THROWN, t)
    } finally {
      reply.recycle()
      data.recycle()
    }
  }
}
