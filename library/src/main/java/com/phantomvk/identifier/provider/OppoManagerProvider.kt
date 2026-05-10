package com.phantomvk.identifier.provider

import android.app.OplusNotificationManager
import android.os.Binder
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class OppoManagerProvider(config: ProviderConfig) : OppoBaseProvider(config) {

  private lateinit var o: OplusNotificationManager

  override fun isSupported(): Boolean {
    o = OplusNotificationManager()
    return true
  }

  override fun run() {
    val p = config.context.packageName
    val u = Binder.getCallingUid()

    try {
      when (val r = checkId(o.getStdid(p, u, OAID))) {
        is Failed -> getConsumer().onError(r.msg, r.throwable)
        is Success -> {
          val aaid = if (config.idConfig.isAaidEnabled) checkId(o.getStdid(p, u, AAID)).id else null
          val vaid = if (config.idConfig.isVaidEnabled) checkId(o.getStdid(p, u, VAID)).id else null
          getConsumer().onSuccess(IdentifierResult(r.id, aaid, vaid))
        }
      }
    } catch (t: Throwable) {
      getConsumer().onError(EXCEPTION_THROWN, t)
    }
  }
}
