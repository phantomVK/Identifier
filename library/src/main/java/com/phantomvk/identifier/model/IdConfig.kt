package com.phantomvk.identifier.model

class IdConfig(
  var isAaidEnabled: Boolean = false,
  var isVaidEnabled: Boolean = false,
  var isGoogleAdsIdEnabled: Boolean = false,
) {
  fun clone(): IdConfig {
    return IdConfig(
      isAaidEnabled = isAaidEnabled,
      isVaidEnabled = isVaidEnabled,
      isGoogleAdsIdEnabled = isGoogleAdsIdEnabled,
    )
  }
}