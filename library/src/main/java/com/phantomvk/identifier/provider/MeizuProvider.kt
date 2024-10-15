package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class MeizuProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isContentProviderExisted("com.meizu.flyme.openidsdk")
  }

  override fun run() {
    when (val result = getId("oaid")) {
      is CallBinderResult.Failed -> {
        getCallback().onError(result.msg)
      }

      is CallBinderResult.Success -> {
        val aaid = if (config.queryAaid) (getId("aaid") as? CallBinderResult.Success)?.id else null
        getCallback().onSuccess(IdentifierResult(result.id, aaid))
      }
    }
  }

  private fun getId(name: String): CallBinderResult {
    val uri = Uri.parse("content://com.meizu.flyme.openidsdk/")
    val resolver = config.context.contentResolver
    val cursor = resolver.query(uri, null, null, arrayOf(name), null)
    if (cursor == null) {
      return CallBinderResult.Failed(QUERY_CURSOR_IS_NULL)
    }

    cursor.use { c ->
      c.moveToFirst()

      if (config.isLimitAdTracking) {
        val code = c.getColumnIndex("code")
        if (code >= 0 && c.getLong(code) == 6L) {
          return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
        }

        val expired = c.getColumnIndex("expired")
        if (expired >= 0 && c.getLong(expired) == 0L) {
          return CallBinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
        }
      }

      val index = c.getColumnIndex("value")
      if (index == -1) {
        return CallBinderResult.Failed(NO_AVAILABLE_COLUMN_INDEX)
      }

      return checkId(c.getString(index))
    }
  }
}