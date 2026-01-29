package com.phantomvk.identifier.provider

import com.android.id.impl.IdProviderImpl
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig


internal class OppoIdProvider(config: ProviderConfig) : AbstractProvider(config) {

  private lateinit var impl: IdProviderImpl

  override fun isSupported(): Boolean {
    impl = IdProviderImpl()
    return true
  }

  override fun run() {
    try {
      when (val r = checkId(impl.getOUID(config.context))) {
        is Failed -> getConsumer().onError(r.msg, r.throwable)
        is Success -> {
          val aaid = if (config.idConfig.isAaidEnabled) checkId(impl.getAUID(config.context)).id else null
          val vaid = if (config.idConfig.isVaidEnabled) checkId(impl.getDUID(config.context)).id else null
          getConsumer().onSuccess(IdentifierResult(r.id, aaid, vaid))
        }
      }
    } catch (t: Throwable) {
      getConsumer().onError(EXCEPTION_THROWN, t)
    }
  }
}
