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
    when (val r = checkId(impl.getOUID(config.context))) {
      is BinderResult.Failed -> getConsumer().onError(r.msg, r.throwable)
      is BinderResult.Success -> {
        val aaid = invokeById(IdEnum.AAID) { checkId(impl.getAUID(config.context)) }
//        val vaid = invokeById(IdEnum.VAID) { checkId(impl.getDUID(config.context)) }
        getConsumer().onSuccess(IdentifierResult(r.id, aaid, null))
      }
    }
  }
}
