package com.phantomvk.identifier.manufacturer

import android.net.Uri
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

  override fun execute() {
    val uri = Uri.parse("content://com.meizu.flyme.openidsdk/")
    val resolver = config.context.contentResolver
    val cursor = resolver.query(uri, null, null, arrayOf("oaid"), null)
    if (cursor == null) {
      getCallback().onError(QUERY_CURSOR_IS_NULL, null)
      return
    }

    cursor.use { c ->
      c.moveToFirst()

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