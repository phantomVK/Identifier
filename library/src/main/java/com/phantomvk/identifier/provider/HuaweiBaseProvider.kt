package com.phantomvk.identifier.provider

import com.phantomvk.identifier.model.ProviderConfig
import com.phantomvk.identifier.provider.AbstractProvider.BinderResult
import java.util.concurrent.CountDownLatch

internal abstract class HuaweiBaseProvider(config: ProviderConfig) : AbstractProvider(config) {
  protected fun getAAID(): String? {
    if (config.idConfig.isAaidEnabled == false) {
      return null
    }

    val latch = CountDownLatch(1)
    var result: BinderResult = Failed(EXCEPTION_THROWN)

    try {
      com.huawei.hms.aaid.HmsInstanceId.getInstance(config.context).aaid
        .addOnSuccessListener {
          result = checkId(it?.id)
          latch.countDown()
        }
        .addOnFailureListener { t: Throwable ->
          result = Failed(EXCEPTION_THROWN, t)
          latch.countDown()
        }
    } catch (t: Throwable) {
      result = Failed(EXCEPTION_THROWN, t)
      latch.countDown()
    }

    latch.await()
    return result.id
  }

  protected fun getVAID(): String? {
    if (config.idConfig.isVaidEnabled == false) {
      return null
    }

    val latch = CountDownLatch(1)
    var result: BinderResult = Failed(EXCEPTION_THROWN)

    try {
      com.huawei.hms.opendevice.OpenDevice.getOpenDeviceClient(config.context).odid
        .addOnSuccessListener {
          result = checkId(it?.id)
          latch.countDown()
        }
        .addOnFailureListener { t: Throwable ->
          result = Failed(EXCEPTION_THROWN, t)
          latch.countDown()
        }
    } catch (t: Throwable) {
      result = Failed(EXCEPTION_THROWN, t)
      latch.countDown()
    }

    latch.await()
    return result.id
  }
}