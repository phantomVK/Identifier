package com.phantomvk.identifier.app.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.app.R


class SettingsActivity : AppCompatActivity() {
  private var isConfChanged = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)
    toolbar.setNavigationOnClickListener { finish() }

    supportActionBar?.let {
      it.setDisplayHomeAsUpEnabled(true)
      it.setDisplayShowHomeEnabled(true)
      it.title = "Settings"
    }

    val v = findViewById<RecyclerView>(R.id.recycler_view_settings)
    v.setLayoutManager(LinearLayoutManager(this))
    v.setAdapter(SettingsAdapter(layoutInflater, Settings.values(), { isConfChanged = true }))
  }

  private fun updateProviderConfig() {
    if (!isConfChanged) return
    val m = IdentifierManager::class.java
    val i = m.getDeclaredField("sInstance").apply { isAccessible = true }.get(null)
    val config = m.getDeclaredField("config").apply { isAccessible = true }.get(i)
    val c = Class.forName("com.phantomvk.identifier.model.ProviderConfig")
    val booleanClass = Boolean::class.java
    c.getMethod("setDebug", booleanClass).invoke(config, Settings.Debug.getValue())
  }

  override fun onDestroy() {
    super.onDestroy()
    updateProviderConfig()
  }
}