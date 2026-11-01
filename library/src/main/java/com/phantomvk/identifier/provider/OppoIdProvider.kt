package com.phantomvk.identifier.provider

import com.android.id.impl.IdProviderImpl
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig


internal class OppoIdProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun run() {
    val impl = IdProviderImpl()
    when (val r = checkId(impl.getOUID(config.context))) {
      is Failed -> getConsumer().onError(r.msg, r.throwable)
      is Success -> {
        val aaid = if (config.idConfig.isAaidEnabled) checkId(impl.getAUID(config.context)).id else null
        val vaid = if (config.idConfig.isVaidEnabled) checkId(impl.getDUID(config.context)).id else null
        getConsumer().onSuccess(IdentifierResult(r.id, aaid, vaid))
      }
    }
  }
}
