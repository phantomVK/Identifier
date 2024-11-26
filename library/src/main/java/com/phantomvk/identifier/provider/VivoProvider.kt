package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class VivoProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    val isSupported = getSysProperty("persist.sys.identifierid.supported", "0")
    if (isSupported != "1") {
      return false
    }

    return isContentProviderExisted("com.vivo.vms.IdProvider")
  }

  override fun run() {
    when (val r = getId("OAID")) {
      is BinderResult.Failed -> return getCallback().onError(r.msg, r.throwable)
      is BinderResult.Success -> {
        val aaid = queryId(IdEnum.AAID) { getId("AAID") }
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