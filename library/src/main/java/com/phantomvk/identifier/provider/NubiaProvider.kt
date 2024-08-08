package com.phantomvk.identifier.provider

import android.content.ContentProviderClient
import android.net.Uri
import android.os.Build
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
          getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          releaseClient(client)
          return
        }
      }
    }

    val bundle = client.call("getOAID", null, null)
    if (bundle == null) {
      getCallback().onError(BUNDLE_IS_NULL)
      releaseClient(client)
      return
    }

    var id: String? = null
    if (bundle.getInt("code", -1) == 0) {
      id = bundle.getString("id")
    }

    checkId(id, getCallback())
    releaseClient(client)
  }

  private fun releaseClient(client: ContentProviderClient) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      client.close()
    } else {
      client.release()
    }
  }
}