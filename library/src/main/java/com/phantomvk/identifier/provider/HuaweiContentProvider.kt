package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal class HuaweiContentProvider(config: ProviderConfig) : HuaweiBaseProvider(config) {

  override fun isSupported(): Boolean {
    return isContentProviderExisted("com.huawei.hwid.pps.apiprovider")
  }

  override fun run() {
    val uri = Uri.parse("content://com.huawei.hwid.pps.apiprovider/oaid/query")
    val cursor = config.context.contentResolver.query(uri, null, null, null, null)
    if (cursor == null) {
      getConsumer().onError(QUERY_CURSOR_IS_NULL)
      return
    }

    cursor.use { c ->
      if (c.moveToFirst() == false) {
        getConsumer().onError(FAILED_TO_MOVE_CURSOR)
        return
      }

      if (config.isVerifyLimitAdTracking) {
        val code = c.getColumnIndex("limit_track")
        if (code >= 0 && c.getString(code).toBoolean()) {
          getConsumer().onError(LIMIT_AD_TRACKING_IS_ENABLED)
          return
        }
      }

      val code = c.getColumnIndex("oaid")
      if (code == -1) {
        getConsumer().onError(NO_AVAILABLE_COLUMN_INDEX)
        return
      }

      when (val r = checkId(c.getString(code))) {
        is Failed -> getConsumer().onError(r.msg, r.throwable)
        is Success -> getConsumer().onSuccess(IdentifierResult(r.id, getAAID(), getVAID()))
      }
    }
  }
}
