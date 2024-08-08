package com.phantomvk.identifier.provider

import android.net.Uri
import com.phantomvk.identifier.model.ProviderConfig

// XTC, imoo
internal class XtcProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun isSupported(): Boolean {
    return isContentProviderExisted("com.xtc.provider")
  }

  override fun run() {
    val uri = Uri.parse("content://com.xtc.provider/BaseDataProvider/openID/8")
    val id = config.context.contentResolver.getType(uri)
    checkId(id, getCallback())
  }
}