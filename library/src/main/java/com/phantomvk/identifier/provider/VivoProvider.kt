package com.phantomvk.identifier.provider

import android.net.Uri
import android.os.SystemProperties
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class VivoProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isContentProviderExisted("com.vivo.vms.IdProvider")
  }

  override fun run() {
    if (config.isVerifyLimitAdTracking) {
      val isSupported = SystemProperties.get("persist.sys.identifierid.supported", "0")
      if (isSupported != "1") {
        getConsumer().onError(LIMIT_AD_TRACKING_IS_ENABLED)
        return
      }
    }

    when (val r = getId("OAID")) {
      is Failed -> return getConsumer().onError(r.msg, r.throwable)
      is Success -> {
        val aaid = if (config.idConfig.isAaidEnabled) (getId("AAID") as? Success)?.id else null
        getConsumer().onSuccess(IdentifierResult(r.id, aaid))
      }
    }
  }

  private fun getId(code: String): BinderResult {
    val prefix = "content://com.vivo.vms.IdProvider/IdentifierId/${code}_"
    val uri = Uri.parse(prefix + config.context.packageName)
    val cursor = config.context.contentResolver.query(uri, null, null, null, null)
    if (cursor == null) {
      return Failed(QUERY_CURSOR_IS_NULL)
    }

    return cursor.use { c ->
      c.moveToFirst()

      val index = c.getColumnIndex("value")
      if (index == -1) {
        return Failed(NO_AVAILABLE_COLUMN_INDEX)
      }

      checkId(c.getString(index))
    }
  }
}