package com.phantomvk.identifier.impl

import android.os.Build
import com.phantomvk.identifier.model.ProviderConfig
import com.phantomvk.identifier.provider.AbstractProvider
import com.phantomvk.identifier.provider.AsusProvider
import com.phantomvk.identifier.provider.CoolpadProvider
import com.phantomvk.identifier.provider.CooseaProvider
import com.phantomvk.identifier.provider.FreemeProvider
import com.phantomvk.identifier.provider.GoogleAdvertisingIdProvider
import com.phantomvk.identifier.provider.HonorSdkProvider
import com.phantomvk.identifier.provider.HonorServiceProvider
import com.phantomvk.identifier.provider.HonorSettingsProvider
import com.phantomvk.identifier.provider.HuaweiSdkProvider
import com.phantomvk.identifier.provider.HuaweiServiceProvider
import com.phantomvk.identifier.provider.HuaweiSettingsProvider
import com.phantomvk.identifier.provider.ZuiProvider
import com.phantomvk.identifier.provider.MeizuProvider
import com.phantomvk.identifier.provider.MsaProvider
import com.phantomvk.identifier.provider.NubiaProvider
import com.phantomvk.identifier.provider.OppoColorOsProvider
import com.phantomvk.identifier.provider.OppoHeyTapProvider
import com.phantomvk.identifier.provider.PicoProvider
import com.phantomvk.identifier.provider.QikuBinderProvider
import com.phantomvk.identifier.provider.QikuServiceProvider
import com.phantomvk.identifier.provider.SamsungProvider
import com.phantomvk.identifier.provider.VivoProvider
import com.phantomvk.identifier.provider.XiaomiProvider
import com.phantomvk.identifier.provider.XtcProvider
import com.phantomvk.identifier.provider.ZteProvider

internal object ManufacturerFactory {

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
    val providers = ArrayList<AbstractProvider>()
    if (isBrand("ASUS")) {
      providers.add(AsusProvider(config))
    }

    if (isBrand("coolpad")) {
      providers.add(CoolpadProvider(config))
    }

    if (isBrand("HONOR")) {
      providers.add(HonorSdkProvider(config))
      providers.add(HonorSettingsProvider(config))
      providers.add(HonorServiceProvider(config))
    }

    if (isBrand("HUAWEI")
      || isBrand("HUAWEI", "HONOR")
      || sysPropertyContains("ro.build.version.emui")
    ) {
      providers.add(HuaweiSdkProvider(config))
      providers.add(HuaweiSettingsProvider(config))
      providers.add(HuaweiServiceProvider(config))
    }

    if (isBrand("LENOVO")
      || isBrand("LENOVO", "ZUK")
      || isBrand("MOTOROLA")
    ) {
      providers.add(ZuiProvider(config))
    }

    if (isBrand("XIAOMI")
      || isBrand("XIAOMI", "REDMI")
      || isBrand("BLACKSHARK")
      || sysPropertyContains("ro.miui.ui.version.name")
    ) {
      providers.add(XiaomiProvider(config))
    }

    if (isBrand("NUBIA")) {
      providers.add(NubiaProvider(config))
    }

    if (isBrand("OPPO")
      || isBrand("realme")
      || isBrand("ONEPLUS")
      || sysPropertyContains("ro.build.version.opporom")
    ) {
      providers.add(OppoColorOsProvider(config))
      providers.add(OppoHeyTapProvider(config))
    }

    if (isBrand("Pico")) {
      providers.add(PicoProvider(config))
    }

    if (isBrand("VIVO") || sysPropertyContains("ro.vivo.os.version")) {
      if (sysProperty("persist.sys.identifierid.supported", "0") == "1") {
        providers.add(VivoProvider(config))
      }
    }

    if (isBrand("SAMSUNG")) {
      providers.add(SamsungProvider(config))
    }

    if (isBrand("MEIZU") || Build.DISPLAY.contains("FLYME", true)) {
      providers.add(MeizuProvider(config))
    }

    if (isBrand("ZTE")) {
      providers.add(ZteProvider(config))
    }

    providers.add(GoogleAdvertisingIdProvider(config))

    if (config.isExperimental) {
      if (sysPropertyEquals("ro.build.uiversion", "360UI")) {
        providers.add(QikuServiceProvider(config))
        providers.add(QikuBinderProvider(config))
      }

      if (sysPropertyContains("ro.build.freeme.label")) {
        providers.add(FreemeProvider(config))
      }

      if (sysPropertyEquals("ro.odm.manufacturer", "PRIZE")) {
        providers.add(CooseaProvider(config))
      }

      providers.add(XtcProvider(config))
      providers.add(MsaProvider(config))
    }

    return providers
  }
}