package com.phantomvk.identifier.impl

import android.os.Build
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.model.ProviderConfig
import com.phantomvk.identifier.provider.AbstractProvider
import com.phantomvk.identifier.provider.AsusProvider
import com.phantomvk.identifier.provider.CoolpadProvider
import com.phantomvk.identifier.provider.CooseaProvider
import com.phantomvk.identifier.provider.FreemeProvider
import com.phantomvk.identifier.provider.GmsProvider
import com.phantomvk.identifier.provider.HonorProvider
import com.phantomvk.identifier.provider.HonorServiceProvider
import com.phantomvk.identifier.provider.HonorSettingsProvider
import com.phantomvk.identifier.provider.HuaweiSdkProvider
import com.phantomvk.identifier.provider.HuaweiServiceProvider
import com.phantomvk.identifier.provider.HuaweiSettingsProvider
import com.phantomvk.identifier.provider.LenovoProvider
import com.phantomvk.identifier.provider.MeizuProvider
import com.phantomvk.identifier.provider.MsaProvider
import com.phantomvk.identifier.provider.NubiaProvider
import com.phantomvk.identifier.provider.OppoColorOsProvider
import com.phantomvk.identifier.provider.OppoHeyTapProvider
import com.phantomvk.identifier.provider.QikuBinderProvider
import com.phantomvk.identifier.provider.QikuServiceProvider
import com.phantomvk.identifier.provider.SamsungProvider
import com.phantomvk.identifier.provider.VivoProvider
import com.phantomvk.identifier.provider.XiaomiProvider
import com.phantomvk.identifier.provider.XtcProvider
import com.phantomvk.identifier.provider.ZteProvider

object ManufacturerFactory {

  private fun isBrand(brand: String): Boolean {
    return Build.MANUFACTURER.equals(brand, true)
        && Build.BRAND.equals(brand, true)
  }

  private fun isBrand(manufacturer: String, brand: String): Boolean {
    return Build.MANUFACTURER.equals(manufacturer, true)
        && Build.BRAND.equals(brand, true)
  }

  private fun sysProperty(key: String, defValue: String): String? {
    return try {
      val clazz = Class.forName("android.os.SystemProperties")
      val method = clazz.getMethod("get", String::class.java, String::class.java)
      method.invoke(clazz, key, defValue) as String
    } catch (t: Throwable) {
      null
    }
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
      val isSupported = sysProperty("persist.sys.identifierid.supported", "0") == "1"
      if (isSupported) {
        val provider = VivoProvider(config)
        providers.add(provider)
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

    if (isBrand("ZTE")) {
      val provider = ZteProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    val provider = GmsProvider(config)
    if (provider.isSupported()) {
      providers.add(provider)
    }

    if (config.isExperimental) {
      addExperimentalProviders(config, providers)
    }

    return providers.toList()
  }

  private fun addExperimentalProviders(
    config: ProviderConfig,
    providers: LinkedHashSet<AbstractProvider>
  ) {
    if (sysPropertyEquals("ro.build.uiversion", "360UI")) {
      val qikuServiceProvider = QikuServiceProvider(config)
      if (qikuServiceProvider.isSupported()) {
        providers.add(qikuServiceProvider)
      }

      val qikuBinderProvider = QikuBinderProvider(config)
      if (qikuBinderProvider.isSupported()) {
        providers.add(qikuBinderProvider)
      }
    }

    if (sysPropertyContains("ro.build.freeme.label")) {
      val freemeProvider = FreemeProvider(config)
      if (freemeProvider.isSupported()) {
        providers.add(freemeProvider)
      }
    }

    if (sysPropertyEquals("ro.odm.manufacturer", "PRIZE")) {
      val cooseaProvider = CooseaProvider(config)
      if (cooseaProvider.isSupported()) {
        providers.add(cooseaProvider)
      }
    }

    val xtcProvider = XtcProvider(config)
    if (xtcProvider.isSupported()) {
      providers.add(xtcProvider)
    }

    val msaProvider = MsaProvider(config)
    if (msaProvider.isSupported()) {
      providers.add(msaProvider)
    }
  }
}