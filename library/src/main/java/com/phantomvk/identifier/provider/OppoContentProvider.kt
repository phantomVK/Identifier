package com.phantomvk.identifier.provider

import android.content.ContentProviderClient
import android.net.Uri
import android.os.Bundle
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig


internal class OppoContentProvider(config: ProviderConfig) : OppoBaseProvider(config) {

  override fun isSupported(): Boolean {
    return isContentProviderExisted("com.oplus.omes.ids_provider")
  }

  override fun run() {
    val uri = Uri.parse("content://com.oplus.omes.ids_provider")
    val client = config.context.contentResolver.acquireContentProviderClient(uri)
    if (client == null) {
      getConsumer().onError(CONTENT_PROVIDER_CLIENT_IS_NULL)
      return
    }

    val sign = getSignatureHash()
    if (sign !is BinderResult.Success) {
      releaseContentProviderClient(client)
      getConsumer().onError(SIGNATURE_HASH_IS_NULL)
      return
    }

    val extras = Bundle();
    extras.putString("packageName", config.context.packageName)
    extras.putString("signature", sign.id)

    try {
      when (val r = getId(client, "OUID", extras)) {
        is BinderResult.Failed -> getConsumer().onError(r.msg, r.throwable)
        is BinderResult.Success -> {
          val aaid = invokeById(IdEnum.AAID) { getId(client, "AUID", extras) }
          getConsumer().onSuccess(IdentifierResult(r.id, aaid, null))
        }
      }
    } catch (t: Throwable) {
      getConsumer().onError(EXCEPTION_THROWN, t)
    } finally {
      releaseContentProviderClient(client)
    }
  }

  private fun getId(client: ContentProviderClient, method: String, extras: Bundle): BinderResult {
    val bundle = client.call(method, null, extras)
    return if (bundle == null) {
      BinderResult.Failed(BUNDLE_IS_NULL)
    } else {
      checkId(bundle.getString(method))
    }
  }
}
