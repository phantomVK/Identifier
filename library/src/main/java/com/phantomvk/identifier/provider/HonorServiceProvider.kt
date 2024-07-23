package com.phantomvk.identifier.provider

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.phantomvk.identifier.impl.Constants.AIDL_INTERFACE_IS_NULL
import com.phantomvk.identifier.impl.Constants.BUNDLE_IS_NULL
import com.phantomvk.identifier.impl.Constants.ID_INFO_IS_NULL
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig
import generated.com.hihonor.cloudservice.oaid.IOAIDCallBack
import generated.com.hihonor.cloudservice.oaid.IOAIDService
import java.util.concurrent.CountDownLatch

class HonorServiceProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "HonorServiceProvider"
  }

  override fun ifSupported(): Boolean {
    return isPackageInfoExisted("com.hihonor.id")
  }

  override fun execute() {
    val binderCallback = object : BinderCallback {
      override fun call(binder: IBinder): CallBinderResult {
        val asInterface = IOAIDService.Stub.asInterface(binder)
        if (asInterface == null) {
          return CallBinderResult.Failed(AIDL_INTERFACE_IS_NULL)
        }

        if (config.isLimitAdTracking) {
          val result = isLimited(asInterface)
          if (result != null) {
            return result
          }
        }

        val result = getId(asInterface)
        return result ?: CallBinderResult.Failed(ID_INFO_IS_NULL)
      }
    }

    val intent = Intent("com.hihonor.id.HnOaIdService")
    intent.setPackage("com.hihonor.id")
    bindService(config.context, intent, getCallback(), binderCallback)
  }

  private fun isLimited(asInterface: IOAIDService): CallBinderResult? {
    var result: CallBinderResult? = null
    val latch = CountDownLatch(1)
    asInterface.isLimited(object : IOAIDCallBack.Stub() {
      override fun a(i: Int, j: Long, z: Boolean, f: Float, d: Double, str: String?) {}
      override fun onResult(i: Int, bundle: Bundle?) {
        if (i == 0 && bundle?.getBoolean("oa_id_limit_state") == true) {
          result = CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
        }
        latch.countDown()
      }
    })

    latch.await()
    return result
  }

  private fun getId(asInterface: IOAIDService): CallBinderResult? {
    var result: CallBinderResult? = null
    val latch = CountDownLatch(1)
    asInterface.getOaid(object : IOAIDCallBack.Stub() {
      override fun a(i: Int, j: Long, z: Boolean, f: Float, d: Double, str: String?) {}
      override fun onResult(i: Int, bundle: Bundle?) {
        if (i != 0 || bundle == null) {
          result = CallBinderResult.Failed(BUNDLE_IS_NULL)
          latch.countDown()
          return
        }

        val id = bundle.getString("oa_id_flag")
        result = checkId(id)
        latch.countDown()
      }
    })

    latch.await()
    return result
  }
}
