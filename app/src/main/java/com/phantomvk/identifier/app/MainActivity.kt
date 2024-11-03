package com.phantomvk.identifier.app

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.app.Application.Companion.IS_DEBUG
import com.phantomvk.identifier.app.Application.Companion.IS_EXPERIMENTAL
import com.phantomvk.identifier.app.Application.Companion.IS_GOOGLE_ADS_ID_ENABLE
import com.phantomvk.identifier.app.Application.Companion.IS_LIMIT_AD_TRACKING
import com.phantomvk.identifier.app.Application.Companion.IS_MEM_CACHE_ENABLE
import com.phantomvk.identifier.interfaces.Disposable
import com.phantomvk.identifier.interfaces.OnResultListener
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig
import com.phantomvk.identifier.provider.AbstractProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

  private var disposable: Disposable? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    findViewById<Button>(R.id.button).setOnClickListener { getId() }
    getId()
  }

  private fun getId() {
    val listener = object : OnResultListener {
      override fun onSuccess(result: IdentifierResult) { updateSuccessInfo(result) }
      override fun onError(msg: String, t: Throwable?) { updateErrorInfo(msg, t) }
    }

    disposable?.dispose()
    disposable = IdentifierManager
      .getInstance()
      .setSubscriber(listener)
      .subscribe()
  }

  private fun updateSuccessInfo(msg: IdentifierResult) {
    lifecycleScope.launch(Dispatchers.IO) {
      val deviceStr = deviceInfo()
        .append("\n* oaid: ${msg.oaid}\n\n")
        .append(
          getResultList().joinToString("\n\n") { model ->
            val builder = StringBuilder("# ${model.tag} (${model.ts}μs)\n")
            if (model.result == null) {
              builder.append("-msg: ${model.msg}")
            } else {
              val list = arrayListOf("-oaid: ${model.result.oaid}")
              model.result.aaid?.let { list.add("-aaid: $it") }
              model.result.vaid?.let { list.add("-vaid: $it") }
              builder.append(list.joinToString("\n"))
            }
            builder.toString()
          })

      showInfo(deviceStr.toString())
    }
  }

  private fun updateErrorInfo(msg: String? = null, t: Throwable? = null) {
    val deviceStr = deviceInfo().append("\n* ErrMsg: $msg").toString()
    showInfo(deviceStr, t)
  }

  private fun showInfo(deviceStr: String, t: Throwable? = null) {
    Log.i("IdentifierTAG", deviceStr, t)
    Log.i("IdentifierTAG", "| ${Build.MANUFACTURER} | ${Build.BRAND} | === | ${Build.MODEL} | ${Build.DEVICE} | ${Build.VERSION.SDK_INT} | ${Build.FINGERPRINT} |")

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
    val list = ArrayList<ResultModel>()
    val config = ProviderConfig(applicationContext).apply {
      isDebug = IS_DEBUG
      isExperimental = IS_EXPERIMENTAL
      isGoogleAdsIdEnabled = IS_GOOGLE_ADS_ID_ENABLE
      isLimitAdTracking = IS_LIMIT_AD_TRACKING
      isMemCacheEnabled = IS_MEM_CACHE_ENABLE
      queryAaid = true
      queryVaid = true
      executor = Executor { r -> Thread(r).start() }
      callback = WeakReference(object : OnResultListener {
        override fun onSuccess(result: IdentifierResult) {}
        override fun onError(msg: String, t: Throwable?) {}
      })
    }

    val decimalFormat = DecimalFormat("#,###")
    val clazz = Class.forName("com.phantomvk.identifier.impl.SerialRunnable")
    val instance = clazz.getConstructor(ProviderConfig::class.java).newInstance(config)
    val providers = clazz.getDeclaredMethod("getProviders").apply { isAccessible = true }.invoke(instance) as List<AbstractProvider>

    for (provider in providers) {
      val startNameTs = System.nanoTime()
      val isSupported = try {
        provider.isSupported()
      } catch (t: Throwable) {
        false
      }

      if (!isSupported) {
        continue
      }

      val latch = CountDownLatch(1)
      val simpleName = provider.javaClass.simpleName
      val resultCallback = object : OnResultListener {
        override fun onSuccess(result: IdentifierResult) {
          list.add(ResultModel(simpleName, result, getNanoTimeStamp()))
          latch.countDown()
        }

        override fun onError(msg: String, t: Throwable?) {
          list.add(ResultModel(simpleName, null, getNanoTimeStamp(), msg))
          latch.countDown()
        }

        private fun getNanoTimeStamp(): String {
          val consumed = (System.nanoTime() - startNameTs) / 1000L
          return decimalFormat.format(consumed)
        }
      }
      provider.setCallback(resultCallback)
      provider.run()
      latch.await()
    }

    return list
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable?.dispose()
  }
}