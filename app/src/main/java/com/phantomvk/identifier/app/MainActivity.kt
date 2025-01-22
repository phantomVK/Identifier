package com.phantomvk.identifier.app

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.app.settings.Settings
import com.phantomvk.identifier.app.settings.SettingsActivity
import com.phantomvk.identifier.disposable.Disposable
import com.phantomvk.identifier.listener.OnResultListener
import com.phantomvk.identifier.model.IdentifierResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

  private val decimalFormat = DecimalFormat("#,###")
  private var disposable: Disposable? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    findViewById<Button>(R.id.button).setOnClickListener { getId() }
    findViewById<Button>(R.id.button_settings).setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
    getId()
  }

  private fun getId() {
    val isAsync = Settings.AsyncCallback.getValue()
    val listener = object : OnResultListener {
      override fun onSuccess(result: IdentifierResult) { assertThread(isAsync) { updateSuccessInfo(result) } }
      override fun onError(msg: String, throwable: Throwable?) { assertThread(isAsync) { updateErrorInfo(msg, throwable) } }
    }

    disposable?.dispose()
    disposable = IdentifierManager.build()
      .enableAsyncCallback(isAsync)
      .enableAaid(Settings.Aaid.getValue())
      .enableVaid(Settings.Vaid.getValue())
      .enableGoogleAdsId(Settings.GoogleAdsId.getValue())
      .setLimitAdTracking(Settings.LimitAdTracking.getValue())
      .subscribe(listener)
  }

  private fun updateSuccessInfo(msg: IdentifierResult) {
    val deviceStr = deviceInfo().append("\n* oaid: ${msg.oaid}\n\n")
    if (!Settings.ProvidersDetails.getValue()) {
      showInfo(deviceStr.toString())
      return
    }

    lifecycleScope.launch(Dispatchers.IO) {
      val builder = StringBuilder()
      val str = getResultList().joinToString("\n\n") { model ->
        builder.setLength(0)
        builder.append("# ${model.tag} (${model.ts}μs)\n")
        if (model.result == null) {
          builder.append("-msg: ${model.msg}")
        } else {
          val list = arrayListOf("-oaid: ${model.result.oaid}")
          model.result.aaid?.let { list.add("-aaid: $it") }
          model.result.vaid?.let { list.add("-vaid: $it") }
          builder.append(list.joinToString("\n"))
        }
      }

      showInfo(deviceStr.append(str).toString())
    }
  }

  private fun updateErrorInfo(msg: String? = null, t: Throwable? = null) {
    val deviceStr = deviceInfo().append("\n* ErrMsg: $msg").toString()
    showInfo(deviceStr, t)
  }

  private fun showInfo(deviceStr: String, t: Throwable? = null) {
    Log.i("IdentifierTAG", deviceStr, t)
    Log.i("IdentifierTAG", "| ${Build.MANUFACTURER} | ${Build.BRAND} | === " +
        "| ${Build.MODEL} | ${Build.DEVICE} " +
        "| ${Build.VERSION.SDK_INT} | ${Build.FINGERPRINT} |")

    lifecycleScope.launch(Dispatchers.Main) {
      val textView = findViewById<TextView>(R.id.system_textview)
      textView.text = deviceStr
      textView.setOnLongClickListener {
        copyToClipboard(deviceStr)
        Toast.makeText(baseContext, "Message copied.", Toast.LENGTH_SHORT).show()
        return@setOnLongClickListener true
      }
    }
  }

  private fun deviceInfo(): StringBuilder {
    return StringBuilder("* Manufacturer: ${Build.MANUFACTURER}, Brand: ${Build.BRAND}\n")
      .append("* Model: ${Build.MODEL}, Device: ${Build.DEVICE}\n")
      .append("* Release: Android ${Build.VERSION.RELEASE} (SDK_INT: ${Build.VERSION.SDK_INT})\n")
      .append("* Display: ${Build.DISPLAY}\n")
      .append("* Incremental: ${Build.VERSION.INCREMENTAL}")
  }

  private fun copyToClipboard(text: String) {
    try {
      val manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
      val clipData = ClipData.newPlainText("IdentifierTAG", text)
      manager.setPrimaryClip(clipData)
    } catch (ignore: Throwable) {
    }
  }

  private class ResultModel(
    val tag: String,
    val result: IdentifierResult?,
    val ts: String? = null,
    val msg: String? = null
  )

  private fun getResultList(): List<ResultModel> {
    val absProviderClass = Class.forName("com.phantomvk.identifier.provider.AbstractProvider")
    val isSupportedMethod = absProviderClass.getMethod("isSupported")
    val onResultListenerClass = Class.forName("com.phantomvk.identifier.listener.OnResultListener")
    val setCallbackMethod = absProviderClass.getDeclaredMethod("setCallback", onResultListenerClass)
    val runMethod = absProviderClass.getMethod("run")

    val list = ArrayList<ResultModel>()
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
      val resultCallback = object : OnResultListener {
        private val simpleName = (provider as Any).javaClass.simpleName
        private val startNanoTime = System.nanoTime()
        override fun onSuccess(result: IdentifierResult) {
          list.add(ResultModel(simpleName, result, getNanoTimeStamp()))
          latch.countDown()
        }

        override fun onError(msg: String, throwable: Throwable?) {
          list.add(ResultModel(simpleName, null, getNanoTimeStamp(), msg))
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

  override fun onDestroy() {
    super.onDestroy()
    disposable?.dispose()
  }

  private fun getProviderList(): List<*> {
    val application = Class.forName("android.app.ActivityThread")
      .getMethod("currentApplication")
      .invoke(null) as Application

    val c = Class.forName("com.phantomvk.identifier.model.ProviderConfig")
    val config = c.getConstructor(Context::class.java).newInstance(application)
    c.getMethod("setAsyncCallback", Boolean::class.java).invoke(config, Settings.AsyncCallback.getValue())
    c.getMethod("setDebug", Boolean::class.java).invoke(config, Settings.Debug.getValue())
    c.getMethod("setExperimental", Boolean::class.java).invoke(config, Settings.Experimental.getValue())
    c.getMethod("setLimitAdTracking", Boolean::class.java).invoke(config, Settings.LimitAdTracking.getValue())
    c.getMethod("setMemCacheEnabled", Boolean::class.java).invoke(config,Settings. MemCache.getValue())
    c.getMethod("setQueryAaid", Boolean::class.java).invoke(config, Settings.Aaid.getValue())
    c.getMethod("setQueryVaid", Boolean::class.java).invoke(config, Settings.Vaid.getValue())
    c.getMethod("setQueryGoogleAdsId", Boolean::class.java).invoke(config, Settings.GoogleAdsId.getValue())
    c.getMethod("setExecutor", Executor::class.java).invoke(config, Executor { r -> Thread(r).start() })
    c.getMethod("setCallback", WeakReference::class.java).invoke(config, WeakReference(object : OnResultListener {
      override fun onSuccess(result: IdentifierResult) {}
      override fun onError(msg: String, throwable: Throwable?) {}
    }))

    val clz = Class.forName("com.phantomvk.identifier.impl.SerialRunnable")
    return clz.getDeclaredMethod("getProviders").apply { isAccessible = true }
      .invoke(clz.getConstructor(c).newInstance(config)) as List<*>
  }

  private fun assertThread(isAsyncCallback: Boolean, runnable: Runnable) {
    if (isAsyncCallback && Looper.getMainLooper() == Looper.myLooper()) {
      throw RuntimeException("Should run on WorkerThread.")
    }

    if (!isAsyncCallback && Looper.getMainLooper() != Looper.myLooper()) {
      throw RuntimeException("Should run on UiThread.")
    }

    runnable.run()
  }
}