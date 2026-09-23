package com.phantomvk.identifier.app.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phantomvk.identifier.app.R


class SettingsActivity : AppCompatActivity() {

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
    v.setAdapter(SettingsAdapter(this))
  }
}
