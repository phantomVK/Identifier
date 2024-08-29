package com.phantomvk.identifier.provider

import com.phantomvk.identifier.model.ProviderConfig

internal class OppoColorOsProvider(config: ProviderConfig) : OppoHeyTapProvider(config) {

  override fun isSupported(): Boolean {
    return isPackageInfoExisted("com.coloros.mcs")
  }

  override fun run() {
    getId(
      "com.oplus.stdid.IStdID",
      "com.coloros.mcs",
      "com.oplus.stdid.IdentifyService",
      "action.com.oplus.stdid.ID_SERVICE"
    )
  }
}