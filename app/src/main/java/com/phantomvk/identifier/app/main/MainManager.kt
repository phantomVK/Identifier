package com.phantomvk.identifier.app.main

import android.content.Context
import android.os.Looper
import com.phantomvk.identifier.app.Application
import com.phantomvk.identifier.app.settings.Settings
import com.phantomvk.identifier.functions.Consumer
import com.phantomvk.identifier.model.IdConfig
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.MemoryConfig
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor

object MainManager {
  private val decimalFormat = DecimalFormat("#,###")

  fun getResultList(): List<ResultDetail> {
    val absProviderClass = Class.forName("com.phantomvk.identifier.provider.AbstractProvider")
    val isSupportedMethod = absProviderClass.getMethod("isSupported")
    val consumerClass = Class.forName("com.phantomvk.identifier.functions.Consumer")
    val setCallbackMethod = absProviderClass.getDeclaredMethod("setConsumer", consumerClass)
    val runMethod = absProviderClass.getMethod("run")

    val list = ArrayList<ResultDetail>()
    for (provider in getProviderList()) {
      val isSupported = try {
        isSupportedMethod.invoke(provider) as Boolean
      } catch (t: Throwable) {
        false
      }

      if (!isSupported) {
        continue
      }

      val latch = CountDownLatch(1)
      val simpleName = (provider as Any).javaClass.simpleName
      val nanoTime = System.nanoTime()
      val resultCallback = object : Consumer {
        override fun onSuccess(result: IdentifierResult) {
          list.add(ResultDetail(simpleName, result, getNanoTimeStamp(nanoTime)))
          latch.countDown()
        }

        override fun onError(msg: String, throwable: Throwable?) {
          list.add(ResultDetail(simpleName, null, getNanoTimeStamp(nanoTime), msg))
          latch.countDown()
        }
      }
      setCallbackMethod.invoke(provider, resultCallback)

      try {
        runMethod.invoke(provider)
      } catch (t: Throwable) {
        list.add(ResultDetail(simpleName, null, getNanoTimeStamp(nanoTime), t.toString()))
        latch.countDown()
      }

      latch.await()
    }

    return list
  }

  private fun getNanoTimeStamp(time: Long): String {
    val consumed = (System.nanoTime() - time) / 1000L
    return decimalFormat.format(consumed)
  }

  private fun getProviderList(): List<*> {
    val c = Class.forName("com.phantomvk.identifier.model.ProviderConfig")
    val config = c.getConstructor(Context::class.java).newInstance(Application.applicationInstance)
    c.getMethod("setAsyncCallback", Boolean::class.java).invoke(config, Settings.AsyncCallback.getValue())
    c.getMethod("setDebug", Boolean::class.java).invoke(config, Settings.Debug.getValue())
    c.getMethod("setExperimental", Boolean::class.java).invoke(config, Settings.Experimental.getValue())
    c.getMethod("setExternalSdkQuerying", Boolean::class.java).invoke(config,Settings.ExternalSdkQuerying.getValue())
    c.getMethod("setVerifyLimitAdTracking", Boolean::class.java).invoke(config, Settings.LimitAdTracking.getValue())
    c.getMethod("setMergeRequests", Boolean::class.java).invoke(config, Settings.MergeRequests.getValue())

    val memoryConfig = MemoryConfig(Settings.MemCache.getValue())
    c.getMethod("setMemoryConfig", MemoryConfig::class.java).invoke(config, memoryConfig)

    val idConfig = IdConfig(Settings.Aaid.getValue(), Settings.Vaid.getValue(), Settings.GoogleAdsId.getValue())
    c.getMethod("setIdConfig", IdConfig::class.java).invoke(config, idConfig)

    c.getMethod("setExecutor", Executor::class.java).invoke(config, Executor { r -> Thread(r).start() })
    c.getMethod("setConsumer", WeakReference::class.java).invoke(config, WeakReference(object : Consumer {
      override fun onSuccess(result: IdentifierResult) {}
      override fun onError(msg: String, throwable: Throwable?) {}
    }))

    val clz = Class.forName("com.phantomvk.identifier.internal.SerialRunnable")
    val instance = clz.getConstructor(c).newInstance(config)
    val list = ArrayList<Any>()

    clz.getDeclaredMethod("addProviders", ArrayList::class.java)
      .apply { isAccessible = true }
      .invoke(instance, list)

    if (Settings.Experimental.getValue()) {
      clz.getDeclaredMethod("addExperimentalProviders", ArrayList::class.java)
        .apply { isAccessible = true }
        .invoke(instance, list)
    }

    // GoogleAdsIdProvider
    val provider = Class.forName("com.phantomvk.identifier.provider.GoogleAdsIdProvider")
    list.add(provider.getConstructor(c).newInstance(config))

    return list
  }

  fun assertThread(isAsyncCallback: Boolean, runnable: Runnable) {
    if (isAsyncCallback && Looper.getMainLooper() == Looper.myLooper()) {
      throw RuntimeException("Should run on WorkerThread.")
    }

    if (!isAsyncCallback && Looper.getMainLooper() != Looper.myLooper()) {
      throw RuntimeException("Should run on UiThread.")
    }

    runnable.run()
  }
}