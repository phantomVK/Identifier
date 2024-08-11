package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.model.ProviderConfig

class PicoProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return true // isBrand("Pico")
  }

  override fun run() {
    val uri = Uri.parse("content://com.pico.idprovider")
    val client = config.context.contentResolver.acquireContentProviderClient(uri)
    if (client == null) {
      getCallback().onError(CONTENT_PROVIDER_CLIENT_IS_NULL)
      return
    }

    val bundle = client.call("request_oaid", null, null)
    releaseContentProviderClient(client)

    if (bundle == null) {
      getCallback().onError(BUNDLE_IS_NULL)
      return
    }

    if (config.isLimitAdTracking) {
      val forbidden = bundle.getBoolean("forbidden", false)
      if (forbidden) {
        getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
        return
      }
    }

    val id = bundle.getString("oaid", null)
    checkId(id, getCallback())
  }
}