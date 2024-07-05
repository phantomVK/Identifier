package com.phantomvk.identifier.manufacturer

import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.phantomvk.identifier.impl.Constants.BUNDLE_IS_NULL
import com.phantomvk.identifier.impl.Constants.CONTENT_PROVIDER_CLIENT_IS_NULL
import com.phantomvk.identifier.model.ProviderConfig

class NubiaProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "NubiaProvider"
  }

  override fun ifSupported(): Boolean {
    return true
  }

  override fun execute() {
    val bundle: Bundle?
    val uri = Uri.parse("content://cn.nubia.identity/identity")

    val client = config.context.contentResolver.acquireContentProviderClient(uri)
    if (client == null) {
      getCallback().onError(CONTENT_PROVIDER_CLIENT_IS_NULL)
      return
    }

    bundle = client.call("getOAID", null, null)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      client.close()
    } else {
      client.release()
    }

    if (bundle == null) {
      getCallback().onError(BUNDLE_IS_NULL)
      return
    }

    var id: String? = null
    if (bundle.getInt("code", -1) == 0) {
      id = bundle.getString("id")
    }

    checkId(id, getCallback())
  }
}