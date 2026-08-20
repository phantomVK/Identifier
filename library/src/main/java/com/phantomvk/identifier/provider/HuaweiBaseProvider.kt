package com.phantomvk.identifier.provider

import com.phantomvk.identifier.model.ProviderConfig
import com.phantomvk.identifier.provider.AbstractProvider.BinderResult
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal abstract class HuaweiBaseProvider(config: ProviderConfig) : AbstractProvider(config) {
  protected fun getAAIDAndVAID(): Pair<String?, String?> {
    val needAaid = config.idConfig.isAaidEnabled
    val needVaid = config.idConfig.isVaidEnabled

    val count = (if (needAaid) 1 else 0) + (if (needVaid) 1 else 0)
    if (count == 0) {
      return Pair(null, null)
    }

    val latch = CountDownLatch(count)
    val failedResult = Failed(COUNTDOWN_LATCH_TIMEOUT)
    var aaidResult: BinderResult = failedResult
    var vaidResult: BinderResult = failedResult

    if (needAaid) {
      try {
        com.huawei.hms.aaid.HmsInstanceId.getInstance(config.context).aaid
          .addOnSuccessListener {
            aaidResult = checkId(it?.id)
            latch.countDown()
          }
          .addOnFailureListener { t: Throwable ->
            aaidResult = Failed(EXCEPTION_THROWN, t)
            latch.countDown()
          }
      } catch (t: Throwable) {
        aaidResult = Failed(EXCEPTION_THROWN, t)
        latch.countDown()
      }
    }

    if (needVaid) {
      try {
        com.huawei.hms.opendevice.OpenDevice.getOpenDeviceClient(config.context).odid
          .addOnSuccessListener {
            vaidResult = checkId(it?.id)
            latch.countDown()
          }
          .addOnFailureListener { t: Throwable ->
            vaidResult = Failed(EXCEPTION_THROWN, t)
            latch.countDown()
          }
      } catch (t: Throwable) {
        vaidResult = Failed(EXCEPTION_THROWN, t)
        latch.countDown()
      }
    }

    latch.await(config.countDownLatchAwaitMilliSec, TimeUnit.MILLISECONDS)

    return Pair(
      if (needAaid) aaidResult.id else null,
      if (needVaid) vaidResult.id else null
    )
  }
}
