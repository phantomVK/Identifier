package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class VivoProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//      return false
//    }
//
//    val value = sysProperty("persist.sys.identifierid.supported", "0")
//    return value == "1"

    return isContentProviderExisted("com.vivo.vms.IdProvider")
  }

  override fun run() {
    when (val r = getId("OAID")) {
      is BinderResult.Failed -> return getCallback().onError(r.msg)
      is BinderResult.Success -> {
        val aaid = if (config.queryAaid) (getId("AAID") as? BinderResult.Success)?.id else null
        getCallback().onSuccess(IdentifierResult(r.id, aaid))
      }
    }
  }

  private fun getId(code: String): BinderResult {
    val prefix = "content://com.vivo.vms.IdProvider/IdentifierId/${code}_"
    val uri = Uri.parse(prefix + config.context.packageName)
    val cursor = config.context.contentResolver.query(uri, null, null, null, null)
    if (cursor == null) {
      return BinderResult.Failed(QUERY_CURSOR_IS_NULL)
    }

    return cursor.use { c ->
      c.moveToFirst()

      val index = c.getColumnIndex("value")
      if (index == -1) {
        return BinderResult.Failed(NO_AVAILABLE_COLUMN_INDEX)
      }

      checkId(c.getString(index))
    }
  }
}