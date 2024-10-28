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
      getCallback().onError(CONTENT_PROVIDER_CLIENT_IS_NULL)
      return
    }

    if (config.isLimitAdTracking) {
      val bundle = client.call("isSupport", null, null)
      if (bundle?.getInt("code", -1) == 0) {
        val isSupported = bundle.getBoolean("issupport", false)
        if (!isSupported) {
          releaseContentProviderClient(client)
          getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          return
        }
      }
    }

    val bundle = client.call("getOAID", null, null)
    val aaid = if (config.queryAaid) getId(client, "getAAID") else null
    val vaid = if (config.queryVaid) getId(client, "getVAID") else null
    releaseContentProviderClient(client)

    if (bundle == null) {
      getCallback().onError(BUNDLE_IS_NULL)
      return
    }

    var id: String? = null
    if (bundle.getInt("code", -1) == 0) {
      id = bundle.getString("id")
    }

    when (val r = checkId(id)) {
      is BinderResult.Failed -> getCallback().onError(r.msg, r.throwable)
      is BinderResult.Success -> getCallback().onSuccess(IdentifierResult(r.id, aaid, vaid))
    }
  }

  private fun getId(client: ContentProviderClient, name: String): String? {
    val id = client.call(name, config.context.packageName, null)?.getString("id")
    return (checkId(id) as? BinderResult.Success)?.id
  }
}