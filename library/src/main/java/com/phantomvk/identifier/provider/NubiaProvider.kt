package com.phantomvk.identifier.provider

import android.content.ContentProviderClient
import android.net.Uri
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class NubiaProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return true
  }

  override fun run() {
    val uri = Uri.parse("content://cn.nubia.identity/identity")
    val client = config.context.contentResolver.acquireContentProviderClient(uri)
    if (client == null) {
      getConsumer().onError(CONTENT_PROVIDER_CLIENT_IS_NULL)
      return
    }

    if (config.isVerifyLimitAdTracking) {
      val bundle = client.call("isSupport", null, null)
      if (bundle?.getInt("code", -1) == 0) {
        val isSupported = bundle.getBoolean("issupport", false)
        if (!isSupported) {
          releaseContentProviderClient(client)
          getConsumer().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          return
        }
      }
    }

    try {
      when (val r = getId(client, "getOAID")) {
        is BinderResult.Failed -> getConsumer().onError(r.msg, r.throwable)
        is BinderResult.Success -> {
          val aaid = invokeById(IdEnum.AAID) { getId(client, "getAAID") }
          val vaid = invokeById(IdEnum.VAID) { getId(client, "getVAID") }
          getConsumer().onSuccess(IdentifierResult(r.id, aaid, vaid))
        }
      }
    } catch (t: Throwable) {
      getConsumer().onError(EXCEPTION_THROWN, t)
    } finally {
      releaseContentProviderClient(client)
    }
  }

  private fun getId(client: ContentProviderClient, name: String): BinderResult {
    val bundle = client.call(name, config.context.packageName, null)
    if (bundle == null) {
      return BinderResult.Failed(BUNDLE_IS_NULL)
    }

    return if (bundle.getInt("code", -1) == 0) {
      checkId(bundle.getString("id"))
    } else {
      BinderResult.Failed(ID_IS_INVALID)
    }
  }
}