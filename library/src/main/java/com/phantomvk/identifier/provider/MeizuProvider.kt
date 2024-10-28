package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class MeizuProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isContentProviderExisted("com.meizu.flyme.openidsdk")
  }

  override fun run() {
    when (val r = getId("oaid")) {
      is BinderResult.Failed -> getCallback().onError(r.msg, r.throwable)
      is BinderResult.Success -> {
        val aaid = if (config.queryAaid) (getId("aaid") as? BinderResult.Success)?.id else null
        getCallback().onSuccess(IdentifierResult(r.id, aaid))
      }
    }
  }

  private fun getId(name: String): BinderResult {
    val uri = Uri.parse("content://com.meizu.flyme.openidsdk/")
    val cursor = config.context.contentResolver.query(uri, null, null, arrayOf(name), null)
    if (cursor == null) {
      return BinderResult.Failed(QUERY_CURSOR_IS_NULL)
    }

    cursor.use { c ->
      c.moveToFirst()

      if (config.isLimitAdTracking) {
        val code = c.getColumnIndex("code")
        if (code >= 0 && c.getLong(code) == 6L) {
          return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
        }

        val expired = c.getColumnIndex("expired")
        if (expired >= 0 && c.getLong(expired) == 0L) {
          return BinderResult.Failed(LIMIT_AD_TRACKING_IS_ENABLED)
        }
      }

      val index = c.getColumnIndex("value")
      if (index == -1) {
        return BinderResult.Failed(NO_AVAILABLE_COLUMN_INDEX)
      }

      return checkId(c.getString(index))
    }
  }
}