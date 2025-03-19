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

class MainActivity : AppCompatActivity() {

  private var disposable: Disposable? = null
  private lateinit var textView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    textView = findViewById(R.id.system_textview)
    findViewById<Button>(R.id.button).setOnClickListener { getId() }
    findViewById<Button>(R.id.button_settings).setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
    getId()
  }

  private fun getId() {
    val isAsync = Settings.AsyncCallback.getValue()
    val consumer = object : Consumer {
      override fun onSuccess(result: IdentifierResult) { assertThread(isAsync) { updateSuccessInfo(result) } }
      override fun onError(msg: String, throwable: Throwable?) { assertThread(isAsync) { updateErrorInfo(msg, throwable) } }
    }

    disposable?.dispose()
    disposable = IdentifierManager.build()
      .enableAsyncCallback(isAsync)
      .enableExperimental(Settings.Experimental.getValue())
      .enableVerifyLimitAdTracking(Settings.LimitAdTracking.getValue())
      .setIdConfig(
        IdConfig(
          isAaidEnabled = Settings.Aaid.getValue(),
          isVaidEnabled = Settings.Vaid.getValue(),
          isGoogleAdsIdEnabled = Settings.GoogleAdsId.getValue()
        )
      )
      .setMemoryConfig(MemoryConfig(Settings.MemCache.getValue()))
      .subscribe(consumer)
  }

  private fun updateSuccessInfo(msg: IdentifierResult) {
    val deviceStr = deviceInfo().append("\n- oaid: ${msg.oaid}\n\n")
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