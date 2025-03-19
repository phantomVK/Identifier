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
import java.lang.reflect.Method
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
      val resultCallback = object : Consumer {
        private val simpleName = (provider as Any).javaClass.simpleName
        private val startNanoTime = System.nanoTime()
        override fun onSuccess(result: IdentifierResult) {
          list.add(ResultDetail(simpleName, result, getNanoTimeStamp()))
          latch.countDown()
        }

        override fun onError(msg: String, throwable: Throwable?) {
          list.add(ResultDetail(simpleName, null, getNanoTimeStamp(), msg))
          latch.countDown()
        }

        private fun getNanoTimeStamp(): String {
          val consumed = (System.nanoTime() - startNanoTime) / 1000L
          return decimalFormat.format(consumed)
        }
      }
      setCallbackMethod.invoke(provider, resultCallback)
      runMethod.invoke(provider)
      latch.await()
    }

    return list
  }

  private fun getProviderList(): List<*> {
//    val application = Class.forName("android.app.ActivityThread")
//      .getMethod("currentApplication")
//      .invoke(null) as Application

    val c = Class.forName("com.phantomvk.identifier.model.ProviderConfig")
    val config = c.getConstructor(Context::class.java).newInstance(Application.applicationInstance)
    c.getMethod("setAsyncCallback", Boolean::class.java).invoke(config, Settings.AsyncCallback.getValue())
    c.getMethod("setDebug", Boolean::class.java).invoke(config, Settings.Debug.getValue())
    c.getMethod("setExperimental", Boolean::class.java).invoke(config, Settings.Experimental.getValue())
    c.getMethod("setVerifyLimitAdTracking", Boolean::class.java).invoke(config, Settings.LimitAdTracking.getValue())
    c.getMethod("setMergeRequests", Boolean::class.java).invoke(config, Settings.MergeRequests.getValue())

    val memoryConfig = MemoryConfig(Settings.MemCache.getValue())
    c.getMethod("setMemoryConfig", MemoryConfig::class.java).invoke(config, memoryConfig)

    val idConfig = IdConfig(Settings.Aaid.getValue(), Settings.Vaid.getValue(), Settings.GoogleAdsId.getValue())
    c.getMethod("setIdConfig", IdConfig::class.java).invoke(config, idConfig)

    val sysProps = Class.forName("android.os.SystemProperties").getMethod("get", String::class.java, String::class.java)
    c.getMethod("setSysProps", Method::class.java).invoke(config, sysProps)

    c.getMethod("setExecutor", Executor::class.java).invoke(config, Executor { r -> Thread(r).start() })
    c.getMethod("setConsumer", WeakReference::class.java).invoke(config, WeakReference(object : Consumer {
      override fun onSuccess(result: IdentifierResult) {}
      override fun onError(msg: String, throwable: Throwable?) {}
    }))

    val clz = Class.forName("com.phantomvk.identifier.internal.SerialRunnable")
    return clz.getDeclaredMethod("getProviders").apply { isAccessible = true }
      .invoke(clz.getConstructor(c).newInstance(config)) as List<*>
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