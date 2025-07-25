package com.phantomvk.identifier.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.Subscription
import com.phantomvk.identifier.app.BuildConfig.BUILD_TYPE
import com.phantomvk.identifier.app.BuildConfig.GIT_REVISION
import com.phantomvk.identifier.app.BuildConfig.VERSION_NAME
import com.phantomvk.identifier.app.main.MainManager.assertThread
import com.phantomvk.identifier.app.main.MainManager.getResultList
import com.phantomvk.identifier.app.settings.Settings
import com.phantomvk.identifier.app.settings.SettingsActivity
import com.phantomvk.identifier.disposable.Disposable
import com.phantomvk.identifier.functions.Consumer
import com.phantomvk.identifier.model.IdConfig
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.MemoryConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

  private var disposable: Disposable? = null
  private lateinit var textView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    textView = findViewById(R.id.system_textview)
    findViewById<Button>(R.id.button).setOnClickListener { getId() }
    findViewById<Button>(R.id.clear_cache).setOnClickListener { IdentifierManager.clearMemoryCache() }
    findViewById<Button>(R.id.button_settings).setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
    getId()
  }

  private fun getId() {
    if (Settings.MergeRequests.getValue()) {
      val mergeConsumer = object : Consumer {
        private val successCount = AtomicInteger()
        private val errorCount = AtomicInteger()
        override fun onSuccess(result: IdentifierResult) {
          val deviceStr = deviceInfo()
            .append("\n- Count: success->${successCount.incrementAndGet()}, error->${errorCount.get()}")
            .append("\n- oaid: ${result.oaid}")
          showInfo(deviceStr.toString())
        }

        override fun onError(msg: String, throwable: Throwable?) {
          val deviceStr = deviceInfo()
            .append("\n- Count: success->${successCount.get()}, error->${errorCount.incrementAndGet()}")
            .append("\n- ErrMsg: $msg")
          showInfo(deviceStr.toString(), throwable)
        }
      }

      getSubscriptionList(100).forEach { it.subscribe(mergeConsumer) }
      return
    }

    disposable?.dispose()
    disposable = getSubscriptionList(1).first().subscribe(object : Consumer {
      private val isAsync = Settings.AsyncCallback.getValue()

      override fun onSuccess(result: IdentifierResult) {
        assertThread(isAsync) { updateSuccessInfo(result) }
      }

      override fun onError(msg: String, throwable: Throwable?) {
        assertThread(isAsync) { updateErrorInfo(msg, throwable) }
      }
    })
  }

  private fun getSubscriptionList(capacity: Int): List<Subscription> {
    val list = ArrayList<Subscription>(capacity)
    val asyncCallback = Settings.AsyncCallback.getValue()
    val experimental = Settings.Experimental.getValue()
    val externalSdkQuerying = Settings.ExternalSdkQuerying.getValue()
    val limitAdTracking = Settings.LimitAdTracking.getValue()
    val memoryConfig = MemoryConfig(Settings.MemCache.getValue())
    val idConfig = IdConfig(
      isAaidEnabled = Settings.Aaid.getValue(),
      isVaidEnabled = Settings.Vaid.getValue(),
      isGoogleAdsIdEnabled = Settings.GoogleAdsId.getValue()
    )

    repeat(capacity) {
      IdentifierManager.build()
        .enableAsyncCallback(asyncCallback)
        .enableExperimental(experimental)
        .enableExternalSdkQuerying(externalSdkQuerying)
        .enableVerifyLimitAdTracking(limitAdTracking)
        .setIdConfig(idConfig)
        .setMemoryConfig(memoryConfig)
        .let { list.add(it) }
    }

    return list
  }

  private fun updateSuccessInfo(msg: IdentifierResult) {
    val deviceStr = deviceInfo().append(
      "\n- Result:" +
          "\n * oaid: ${msg.oaid}" +
          "\n * aaid: ${msg.aaid}" +
          "\n * vaid: ${msg.vaid}" +
          "\n * gaid: ${msg.gaid}\n\n"
    )

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
    val deviceStr = deviceInfo().append("\n- ErrMsg: $msg").toString()
    showInfo(deviceStr, t)
  }

  private fun showInfo(deviceStr: String, t: Throwable? = null) {
    Log.i("IdentifierTAG", deviceStr, t)
    Log.i("IdentifierTAG", "| ${Build.MANUFACTURER} | ${Build.BRAND} | === " +
        "| ${Build.MODEL} | ${Build.DEVICE} " +
        "| ${Build.VERSION.SDK_INT} | ${Build.FINGERPRINT} |")

    lifecycleScope.launch(Dispatchers.Main) {
      textView.text = deviceStr
      textView.setOnLongClickListener {
        copyToClipboard(deviceStr)
        Toast.makeText(baseContext, "Message copied.", Toast.LENGTH_SHORT).show()
        return@setOnLongClickListener true
      }
    }
  }

  private fun deviceInfo(): StringBuilder {
    return StringBuilder("# Device info\n")
      .append("- AppVer: v${VERSION_NAME}_${GIT_REVISION}_${BUILD_TYPE}\n")
      .append("- Manufacturer: ${Build.MANUFACTURER}, Brand: ${Build.BRAND}\n")
      .append("- Model: ${Build.MODEL}, Device: ${Build.DEVICE}\n")
      .append("- Release: Android ${Build.VERSION.RELEASE} (SDK_INT: ${Build.VERSION.SDK_INT})\n")
      .append("- Display: ${Build.DISPLAY}\n")
      .append("- Incremental: ${Build.VERSION.INCREMENTAL}")
  }

  private fun copyToClipboard(text: String) {
    try {
      val manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
      val clipData = ClipData.newPlainText("IdentifierTAG", text)
      manager.setPrimaryClip(clipData)
    } catch (ignore: Throwable) {
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (disposable?.isDisposed == false) {
      disposable?.dispose()
    }
  }
}