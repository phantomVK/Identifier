package com.phantomvk.identifier.impl

import android.os.Build
import com.phantomvk.identifier.manufacturer.AbstractProvider
import com.phantomvk.identifier.manufacturer.AsusProvider
import com.phantomvk.identifier.manufacturer.CoolpadProvider
import com.phantomvk.identifier.manufacturer.CooseaProvider
import com.phantomvk.identifier.manufacturer.FreemeProvider
import com.phantomvk.identifier.manufacturer.GmsProvider
import com.phantomvk.identifier.manufacturer.HonorProvider
import com.phantomvk.identifier.manufacturer.HonorServiceProvider
import com.phantomvk.identifier.manufacturer.HonorSettingsProvider
import com.phantomvk.identifier.manufacturer.HuaweiSdkProvider
import com.phantomvk.identifier.manufacturer.HuaweiServiceProvider
import com.phantomvk.identifier.manufacturer.HuaweiSettingsProvider
import com.phantomvk.identifier.manufacturer.LenovoProvider
import com.phantomvk.identifier.manufacturer.MeizuProvider
import com.phantomvk.identifier.manufacturer.MsaProvider
import com.phantomvk.identifier.manufacturer.NubiaProvider
import com.phantomvk.identifier.manufacturer.OppoColorOsProvider
import com.phantomvk.identifier.manufacturer.OppoHeyTapProvider
import com.phantomvk.identifier.manufacturer.QikuBinderProvider
import com.phantomvk.identifier.manufacturer.QikuServiceProvider
import com.phantomvk.identifier.manufacturer.SamsungProvider
import com.phantomvk.identifier.manufacturer.VivoProvider
import com.phantomvk.identifier.manufacturer.XiaomiProvider
import com.phantomvk.identifier.manufacturer.XtcProvider
import com.phantomvk.identifier.manufacturer.ZteProvider
import com.phantomvk.identifier.model.ProviderConfig
import com.phantomvk.identifier.util.sysProperty

object ManufacturerFactory {

  private fun isBrand(brand: String): Boolean {
    return Build.MANUFACTURER.equals(brand, true)
        && Build.BRAND.equals(brand, true)
  }

  private fun isBrand(manufacturer: String, brand: String): Boolean {
    return Build.MANUFACTURER.equals(manufacturer, true)
        && Build.BRAND.equals(brand, true)
  }

  private fun sysPropertyContains(key: String): Boolean {
    return !sysProperty(key, "").isNullOrBlank()
  }

  private fun sysPropertyEquals(key: String, value: String): Boolean {
    return sysProperty(key, "").equals(value, true)
  }

  fun getProviders(config: ProviderConfig): List<AbstractProvider> {
    val providers = LinkedHashSet<AbstractProvider>()
    if (isBrand("ASUS")) {
      val provider = AsusProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isBrand("coolpad")) {
      val coolpadProvider = CoolpadProvider(config)
      if (coolpadProvider.isSupported()) {
        providers.add(coolpadProvider)
      }
    }

    if (sysPropertyContains("ro.build.freeme.label")) {
      val provider = FreemeProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isBrand("HONOR")) {
      val provider = HonorProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }

      val honorServiceProvider = HonorServiceProvider(config)
      if (honorServiceProvider.isSupported()) {
        providers.add(honorServiceProvider)
      }

      val settingsProvider = HonorSettingsProvider(config)
      if (settingsProvider.isSupported()) {
        providers.add(settingsProvider)
      }
    }

    if (isBrand("HUAWEI")
      || isBrand("HUAWEI", "HONOR")
      || sysPropertyContains("ro.build.version.emui")
    ) {
      val provider = HuaweiSdkProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }

      val settingsProvider = HuaweiSettingsProvider(config)
      if (settingsProvider.isSupported()) {
        providers.add(settingsProvider)
      }

      val serviceProvider = HuaweiServiceProvider(config)
      if (serviceProvider.isSupported()) {
        providers.add(serviceProvider)
      }
    }

    if (isBrand("LENOVO")
      || isBrand("LENOVO", "ZUK")
      || isBrand("MOTOROLA")
    ) {
      val provider = LenovoProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isBrand("XIAOMI")
      || isBrand("XIAOMI", "REDMI")
      || isBrand("BLACKSHARK")
      || sysPropertyContains("ro.miui.ui.version.name")
    ) {
      val provider = XiaomiProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isBrand("NUBIA")) {
      val provider = NubiaProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isBrand("OPPO")
      || isBrand("realme")
      || isBrand("ONEPLUS")
      || sysPropertyContains("ro.build.version.opporom")
    ) {
      val heyTapProvider = OppoHeyTapProvider(config)
      if (heyTapProvider.isSupported()) {
        providers.add(heyTapProvider)
      }

      val colorOsProvider = OppoColorOsProvider(config)
      if (colorOsProvider.isSupported()) {
        providers.add(colorOsProvider)
      }
    }

    if (isBrand("VIVO") || sysPropertyContains("ro.vivo.os.version")) {
      val provider = VivoProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (sysPropertyEquals("ro.build.uiversion", "360UI")) {
      val provider = QikuServiceProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }

      val serviceProvider = QikuBinderProvider(config)
      if (serviceProvider.isSupported()) {
        providers.add(serviceProvider)
      }
    }

    if (isBrand("SAMSUNG")) {
      val provider = SamsungProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isBrand("MEIZU") || Build.DISPLAY.contains("FLYME", true)) {
      val provider = MeizuProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (sysPropertyEquals("ro.odm.manufacturer", "PRIZE")) {
      val provider = CooseaProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isBrand("ZTE")) {
      val provider = ZteProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    val xtcProvider = XtcProvider(config)
    if (xtcProvider.isSupported()) {
      providers.add(xtcProvider)
    }

    val provider = GmsProvider(config)
    if (provider.isSupported()) {
      providers.add(provider)
    }

    val msaProvider = MsaProvider(config)
    if (msaProvider.isSupported()) {
      providers.add(msaProvider)
    }

    return providers.toList()
  }
}