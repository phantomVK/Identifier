package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.impl.Constants.LIMIT_AD_TRACKING_IS_ENABLED
import com.phantomvk.identifier.impl.Constants.NO_AVAILABLE_COLUMN_INDEX
import com.phantomvk.identifier.impl.Constants.QUERY_CURSOR_IS_NULL
import com.phantomvk.identifier.model.ProviderConfig

class MeizuProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "MeizuProvider"
  }

  override fun ifSupported(): Boolean {
    return isContentProviderExisted("com.meizu.flyme.openidsdk")
  }

  override fun run() {
    val uri = Uri.parse("content://com.meizu.flyme.openidsdk/")
    val resolver = config.context.contentResolver
    val cursor = resolver.query(uri, null, null, arrayOf("oaid"), null)
    if (cursor == null) {
      getCallback().onError(QUERY_CURSOR_IS_NULL, null)
      return
    }

    cursor.use { c ->
      c.moveToFirst()

      if (config.isLimitAdTracking) {
        val code = c.getColumnIndex("code")
        if (code >= 0 && c.getLong(code) == 6L) {
          getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          return
        }

        val expired = c.getColumnIndex("expired")
        if (expired >= 0 && c.getLong(expired) == 0L) {
          getCallback().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          return
        }
      }

      val index = c.getColumnIndex("value")
      if (index == -1) {
        getCallback().onError(NO_AVAILABLE_COLUMN_INDEX, null)
        return
      }

      val id = c.getString(index)
      checkId(id, getCallback())
    }
  }
}