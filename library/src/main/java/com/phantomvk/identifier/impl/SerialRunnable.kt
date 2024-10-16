package com.phantomvk.identifier.impl

import android.os.Build
import android.os.Looper
import com.phantomvk.identifier.interfaces.Disposable
import com.phantomvk.identifier.interfaces.OnResultListener
import com.phantomvk.identifier.log.Log
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig
import com.phantomvk.identifier.provider.AbstractProvider
import com.phantomvk.identifier.provider.AsusProvider
import com.phantomvk.identifier.provider.CoolpadServiceProvider
import com.phantomvk.identifier.provider.CoolpadSettingsProvider
import com.phantomvk.identifier.provider.CooseaProvider
import com.phantomvk.identifier.provider.FreemeProvider
import com.phantomvk.identifier.provider.GoogleAdsIdProvider
import com.phantomvk.identifier.provider.HonorSdkProvider
import com.phantomvk.identifier.provider.HonorServiceProvider
import com.phantomvk.identifier.provider.HonorSettingsProvider
import com.phantomvk.identifier.provider.HuaweiSdkProvider
import com.phantomvk.identifier.provider.HuaweiServiceProvider
import com.phantomvk.identifier.provider.HuaweiSettingsProvider
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
import com.phantomvk.identifier.provider.ZuiProvider
import java.util.concurrent.CountDownLatch

internal class SerialRunnable(config: ProviderConfig) : AbstractProvider(config), Disposable {

  private val disposable = DisposableResultListener(config.callback)

  init {
    setCallback(disposable)
  }

  override fun isSupported(): Boolean {
    return true
  }

  override fun run() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      if (config.isDebug) {
        throw RuntimeException("Do not execute runnable on the main thread.")
      } else {
        Thread { onExecute() }.start()
      }
    } else {
      onExecute()
    }
  }

  private fun onExecute() {
    var isSuccess = false
    for (provider in getProviders()) {
      if (disposable.isDisposed) {
        return
      }

      val isSupported = try {
        provider.isSupported()
      } catch (t: Throwable) {
        false
      }

      if (!isSupported) {
        continue
      }

      val latch = CountDownLatch(1)
      val resultCallback = object : OnResultListener {
        override fun onSuccess(result: IdentifierResult) {
          getCallback().onSuccess(result)
          isSuccess = true
          latch.countDown()
        }

        override fun onError(msg: String, t: Throwable?) {
          Log.e("SerialRunnable", "${provider.javaClass.simpleName} onError.", t)
          latch.countDown()
        }
      }

      // execute Runnable safely.
      try {
        provider.setCallback(resultCallback)
        provider.run()
      } catch (t: Throwable) {
        getCallback().onError(EXCEPTION_THROWN, t)
      }

      latch.await()

      if (isSuccess) {
        return
      }
    }

    getCallback().onError(NO_IMPLEMENTATION_FOUND, null)
  }

  override fun dispose() {
    disposable.dispose()
  }

  override fun isDisposed(): Boolean {
    return disposable.isDisposed
  }

  private fun getProviders(): List<AbstractProvider> {
    val providers = ArrayList<AbstractProvider>()
    addProviders(config, providers)

    if (config.isExperimental) {
      addExperimentalProviders(config, providers)
    }

    if (config.isGoogleAdsIdEnabled) {
      providers.add(GoogleAdsIdProvider(config))
    }

    return providers
  }

  private fun addProviders(config: ProviderConfig, providers: ArrayList<AbstractProvider>) {
    if (isBrand("ASUS")) {
      providers.add(AsusProvider(config))
      return
    }

    if (isBrand("Coolpad")) {
      if (config.queryAaid || config.queryVaid) {
        providers.add(CoolpadServiceProvider(config))
        providers.add(CoolpadSettingsProvider(config))
      } else {
        providers.add(CoolpadSettingsProvider(config))
        providers.add(CoolpadServiceProvider(config))
      }
      return
    }

    if (isBrand("HUAWEI")
      || isBrand("HONOR")
      || isBrand("HUAWEI", "HONOR")
      || sysPropertyContains("ro.build.version.emui")
    ) {
      if (isBrand("HUAWEI")
        || isBrand("HUAWEI", "HONOR")
        || sysPropertyContains("ro.build.version.emui")
      ) {
        providers.add(HuaweiSdkProvider(config))
        providers.add(HuaweiSettingsProvider(config))
        providers.add(HuaweiServiceProvider(config))
      }

      if (isBrand("HONOR")) {
        providers.add(HonorSdkProvider(config))
        providers.add(HonorSettingsProvider(config))
        providers.add(HonorServiceProvider(config))
      }
      return
    }

    if (isBrand("LENOVO")
      || isBrand("LENOVO", "ZUK")
      || isBrand("MOTOROLA")
    ) {
      providers.add(ZuiProvider(config))
      return
    }

    if (isBrand("XIAOMI")
      || isBrand("XIAOMI", "REDMI")
      || isBrand("BLACKSHARK")
      || sysPropertyContains("ro.miui.ui.version.name")
    ) {
      providers.add(XiaomiProvider(config))
      return
    }

    if (isBrand("NUBIA")) {
      providers.add(NubiaProvider(config))
      return
    }

    if (isBrand("OPPO")
      || isBrand("realme")
      || isBrand("ONEPLUS")
      || sysPropertyContains("ro.build.version.opporom")
    ) {
      providers.add(OppoColorOsProvider(config))
      providers.add(OppoHeyTapProvider(config))
      return
    }

    if (isBrand("VIVO") || sysPropertyContains("ro.vivo.os.version")) {
      if (sysProperty("persist.sys.identifierid.supported", "0") == "1") {
        providers.add(VivoProvider(config))
      }
      return
    }

    if (isBrand("SAMSUNG")) {
      providers.add(SamsungProvider(config))
      return
    }

    if (isBrand("MEIZU") || Build.DISPLAY.contains("FLYME", true)) {
      providers.add(MeizuProvider(config))
      return
    }

    if (isBrand("ZTE")) {
      providers.add(ZteProvider(config))
      return
    }
  }

  private fun addExperimentalProviders(
    config: ProviderConfig,
    providers: ArrayList<AbstractProvider>
  ) {
    if (isBrand("360")) {
      providers.add(QikuServiceProvider(config))

      if (sysPropertyEquals("ro.build.uiversion", "360UI")) {
        providers.add(QikuBinderProvider(config))
      }
      return
    }

    if (sysPropertyContains("ro.build.freeme.label")) {
      providers.add(FreemeProvider(config))
      return
    }

    if (isBrand("Pico")) {
      providers.add(PicoProvider(config))
      return
    }

    if (sysPropertyEquals("ro.odm.manufacturer", "PRIZE")) {
      providers.add(CooseaProvider(config))
      return
    }

    providers.add(XtcProvider(config))
    providers.add(MsaProvider(config))
  }
}