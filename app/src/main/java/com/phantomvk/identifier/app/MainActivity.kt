package com.phantomvk.identifier.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
import com.phantomvk.identifier.app.main.MainManager
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

  private val subscriptions = ArrayList<Disposable>()
  private lateinit var textView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    textView = findViewById(R.id.system_textview)
    textView.movementMethod = ScrollingMovementMethod.getInstance()

    findViewById<Button>(R.id.refresh).setOnClickListener { getId() }
    findViewById<Button>(R.id.configs).setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
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
          showInfo(deviceStr)
        }

        override fun onError(msg: String, throwable: Throwable?) {
          val deviceStr = deviceInfo()
            .append("\n- Count: success->${successCount.get()}, error->${errorCount.incrementAndGet()}")
            .append("\n- ErrMsg: $msg")
          showInfo(deviceStr, throwable)
        }
      }

      disposeSubscriptions()
      getSubscriptionList(100).forEach { subscriptions.add(it.subscribe(mergeConsumer)) }
      return
    }

    disposeSubscriptions()
    getSubscriptionList(1).first().subscribe(object : Consumer {
      private val isAsync = Settings.AsyncCallback.getValue()

      override fun onSuccess(result: IdentifierResult) {
        assertThread(isAsync) { updateSuccessInfo(result) }
      }

      override fun onError(msg: String, throwable: Throwable?) {
        assertThread(isAsync) { showInfo("\n- ErrMsg: $msg", throwable) }
      }
    }).let { subscriptions.add(it) }
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
      "\n# Result:" +
          "\n * oaid: ${msg.oaid.ifBlank { "null" }}" +
          "\n * aaid: ${msg.aaid}" +
          "\n * vaid: ${msg.vaid}" +
          "\n * gaid: ${msg.gaid}"
    )

    if (!Settings.ProvidersDetails.getValue()) {
      showInfo(deviceStr)
      return
    }

    lifecycleScope.launch(Dispatchers.IO) {
      getResultList(this@MainActivity).forEach { r ->
        deviceStr.append("\n\n# ${r.tag} (${r.ts}μs)\n")
        if (r.result == null) {
          deviceStr.append("${r.msg}")
        } else {
          deviceStr.append(" * oaid: ${r.result.oaid}")
          r.result.aaid?.let { deviceStr.append("\n * aaid: $it") }
          r.result.vaid?.let { deviceStr.append("\n * vaid: $it") }
        }
      }

      showInfo(deviceStr)
    }
  }

  private fun showInfo(deviceStr: CharSequence, t: Throwable? = null) {
    val msg = "| ${Build.MANUFACTURER} | ${Build.BRAND} | === " +
        "| ${Build.MODEL} | ${Build.DEVICE} " +
        "| ${Build.VERSION.SDK_INT} | ${Build.FINGERPRINT} |"

    Log.i("IdentifierTAG", msg + '\n' + deviceStr, t)

    lifecycleScope.launch(Dispatchers.Main) {
      textView.text = deviceStr
      textView.setOnLongClickListener {
        MainManager.copyToClipboard(this@MainActivity, deviceStr)
        Toast.makeText(baseContext, "Message copied.", Toast.LENGTH_SHORT).show()
        return@setOnLongClickListener true
      }
    }
  }

  private fun deviceInfo(): StringBuilder {
    return StringBuilder("# Information\n")
      .append("- Ver: v${VERSION_NAME}_${GIT_REVISION}_${BUILD_TYPE}\n")
      .append("- Manufacturer: ${Build.MANUFACTURER}, Brand: ${Build.BRAND}\n")
      .append("- Model: ${Build.MODEL}, Device: ${Build.DEVICE}\n")
      .append("- Release: Android ${Build.VERSION.RELEASE} (SDK_INT: ${Build.VERSION.SDK_INT})\n")
      .append("- Display: ${Build.DISPLAY}\n")
      .append("- Incremental: ${Build.VERSION.INCREMENTAL}\n")
  }

  private fun disposeSubscriptions() {
    subscriptions.forEach { it.dispose() }
    subscriptions.clear()
  }

  override fun onDestroy() {
    super.onDestroy()
    disposeSubscriptions()
  }
}