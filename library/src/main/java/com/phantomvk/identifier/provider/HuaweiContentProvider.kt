package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.model.ProviderConfig

internal class HuaweiContentProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isContentProviderExisted("com.huawei.hwid.pps.apiprovider")
  }

  override fun run() {
    val uri = Uri.parse("content://com.huawei.hwid.pps.apiprovider/oaid/query")
    val cursor = config.context.contentResolver.query(uri, null, null, null, null)
    if (cursor == null) {
      targetConsumer.onError(QUERY_CURSOR_IS_NULL)
      return
    }

    cursor.use { c ->
      c.moveToFirst()

      if (config.isVerifyLimitAdTracking) {
        val code = c.getColumnIndex("limit_track")
        if (code >= 0 && c.getString(code).toBoolean()) {
          return targetConsumer.onError(LIMIT_AD_TRACKING_IS_ENABLED)
        }
      }

      val code = c.getColumnIndex("oaid")
      if (code == -1) {
        return targetConsumer.onError(NO_AVAILABLE_COLUMN_INDEX)
      }

      checkId(c.getString(code), targetConsumer)
    }
  }
}
