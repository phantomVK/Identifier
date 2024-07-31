package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.model.ProviderConfig

class VivoProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "VivoProvider"
  }

  override fun isSupported(): Boolean {
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//      return false
//    }
//
//    val value = sysProperty("persist.sys.identifierid.supported", "0")
//    return value == "1"

    return true
  }

  override fun run() {
    val uri = Uri.parse("content://com.vivo.vms.IdProvider/IdentifierId/OAID")
    val resolver = config.context.contentResolver
    val cursor = resolver.query(uri, null, null, null, null)
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