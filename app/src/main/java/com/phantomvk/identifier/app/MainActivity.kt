package com.phantomvk.identifier.app

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.interfaces.OnResultListener

class MainActivity : AppCompatActivity() {

  private val infoTextView by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.system_textview) }

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<Button>(R.id.button).setOnClickListener { getId() }
    getId()
  }

  private fun getId() {
    val listener = object : OnResultListener {
      override fun onSuccess(id: String) { updateTextInfo(id) }
      override fun onError(msg: String, t: Throwable?) { updateTextInfo(msg, t) }
    }

    IdentifierManager
      .getInstance()
      .create(this, listener)
      .setLimitAdTracking(false)
      .start()
  }

  private fun updateTextInfo(msg: String? = null, t: Throwable? = null) {
    val deviceInfo = deviceInfo(msg)
    infoTextView.text = deviceInfo
    Log.i("IdentifierTAG", deviceInfo, t)

    if (!msg.isNullOrBlank()) {
      copyToClipboard(deviceInfo)
    }
  }

  private fun deviceInfo(id: String? = null): String {
    return """
        * Manufacturer: ${Build.MANUFACTURER}
        * Brand: ${Build.BRAND}
        * Model: ${Build.MODEL}
        * Device: ${Build.DEVICE}
        * Release: Android ${Build.VERSION.RELEASE} (SDK_INT: ${Build.VERSION.SDK_INT})
        * Display: ${Build.DISPLAY}
        * Incremental: ${Build.VERSION.INCREMENTAL}
        * Fingerprint: ${Build.FINGERPRINT}
        * AndroidId: ${getAndroidID(this)}
        * | ${Build.MANUFACTURER} | ${Build.BRAND} | === | ${Build.MODEL} | ${Build.DEVICE} | ${Build.VERSION.SDK_INT} | ${Build.FINGERPRINT} |
        * oaid: $id
      """.trimIndent()
  }

  private fun copyToClipboard(text: String) {
    try {
      val manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
      val clipData = ClipData.newPlainText("IdentifierDemo", text)
      manager.setPrimaryClip(clipData)
    } catch (ignore: Exception) {
    }
  }

  @SuppressLint("HardwareIds")
  private fun getAndroidID(context: Context): String? {
    val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    return if (id == null || id == "9774d56d682e549c") null else id
  }
}