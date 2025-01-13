package com.phantomvk.identifier.impl

import android.os.Build
import android.os.Handler
import android.os.Looper
import com.phantomvk.identifier.disposable.Disposable
import com.phantomvk.identifier.listener.OnResultListener
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
import com.phantomvk.identifier.provider.HuaweiContentProvider
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

internal class SerialRunnable(
  config: ProviderConfig
) : AbstractProvider(config), OnResultListener, Disposable {

  @Volatile
  private var disposed = false

  init {
    setCallback(this)
  }

  override fun isSupported(): Boolean {
    return true
  }

  override fun run() {
    val cached = CacheCenter.get(config)
    if (cached == null) {
      config.executor.execute { execute(0, getProviders()) }
    } else {
      getCallback().onSuccess(cached)
    }
  }

  private fun execute(index: Int, providers: List<AbstractProvider>) {
    if (index == providers.size) {
      getCallback().onError(NO_IMPLEMENTATION_FOUND)
      return
    }

    if (disposed) {
      return
    }

    val provider = providers[index]
    val isSupported = try {
      provider.isSupported()
    } catch (t: Throwable) {
      false
    }

    if (!isSupported) {
      config.executor.execute { execute(index + 1, providers) }
      return
    }

    val resultCallback = object : OnResultListener {
      override fun onSuccess(result: IdentifierResult) {
        CacheCenter.put(config, result)
        getCallback().onSuccess(result)
      }

      override fun onError(msg: String, throwable: Throwable?) {
        Log.e("SerialRunnable", "${provider.javaClass.simpleName} onError.", throwable)
        config.executor.execute { execute(index + 1, providers) }
      }
    }

    // execute Runnable safely.
    try {
      provider.setCallback(resultCallback)
      provider.run()
    } catch (t: Throwable) {
      getCallback().onError(EXCEPTION_THROWN, t)
    }
  }

  override fun onError(msg: String, throwable: Throwable?) {
    invokeCallback { it.onError(msg, throwable) }
  }

  override fun onSuccess(result: IdentifierResult) {
    invokeCallback { it.onSuccess(result) }
  }

  override fun dispose() {
    invokeCallback()
  }

  override fun isDisposed(): Boolean {
    return disposed
  }

  private fun invokeCallback(callback: ((OnResultListener) -> Unit)? = null) {
    if (disposed) {
      return
    }

    synchronized(this) {
      if (disposed) return else disposed = true

      if (callback != null) {
        config.callback.get()?.let {
          if (config.asyncCallback && Looper.getMainLooper() == Looper.myLooper()) {
            config.executor.execute { callback.invoke(it) }
            return@let
          }

          if (!config.asyncCallback && Looper.getMainLooper() != Looper.myLooper()) {
            Handler(Looper.getMainLooper()).post { callback.invoke(it) }
            return@let
          }

          callback.invoke(it)
        }
      }

      config.callback.clear()
    }
  }

  private fun getProviders(): List<AbstractProvider> {
    val providers = ArrayList<AbstractProvider>()
    addProviders(config, providers)

    if (config.isExperimental) {
      addExperimentalProviders(config, providers)
    }

    if (config.queryGoogleAdsId) {
      providers.add(GoogleAdsIdProvider(config))
    }

    return providers
  }

  private fun addProviders(config: ProviderConfig, providers: ArrayList<AbstractProvider>) {
    if (isBrand("360")) {
      providers.add(QikuBinderProvider(config))
      return
    }

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
      || isSysPropertyContainsKey("ro.build.version.emui")
    ) {
      if (isBrand("HUAWEI")
        || isBrand("HUAWEI", "HONOR")
        || isSysPropertyContainsKey("ro.build.version.emui")
      ) {
        providers.add(HuaweiSdkProvider(config))
        providers.add(HuaweiSettingsProvider(config))
        providers.add(HuaweiContentProvider(config))
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
      || isSysPropertyContainsKey("ro.miui.ui.version.name")
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
      || isSysPropertyContainsKey("ro.build.version.opporom")
    ) {
      providers.add(OppoHeyTapProvider(config))
      providers.add(OppoColorOsProvider(config))
      return
    }

    if (isBrand("VIVO") || isSysPropertyContainsKey("ro.vivo.os.version")) {
      providers.add(VivoProvider(config))
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
      return
    }

    if (isSysPropertyContainsKey("ro.build.freeme.label")) {
      providers.add(FreemeProvider(config))
      return
    }

    if (isBrand("Pico")) {
      providers.add(PicoProvider(config))
      return
    }

    // Access denied finding property "ro.odm.manufacturer"
    if (getSysProperty("ro.odm.manufacturer", "") == "PRIZE") {
      providers.add(CooseaProvider(config))
      return
    }

    if (Build.MODEL.startsWith("xtc", true) || Build.MODEL.startsWith("imoo", true)) {
      providers.add(XtcProvider(config))
    }

    if (isPackageInfoExisted("com.mdid.msa")) {
      providers.add(MsaProvider(config))
    }
  }
}