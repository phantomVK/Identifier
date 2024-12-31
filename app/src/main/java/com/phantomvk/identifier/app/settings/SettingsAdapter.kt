package com.phantomvk.identifier.app.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phantomvk.identifier.app.R

class SettingsAdapter(
  private val inflater: LayoutInflater,
  private val settings: Array<Settings>,
  private val listener: Runnable
) : RecyclerView.Adapter<SettingsViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
    val v = inflater.inflate(R.layout.item_switch, parent, false)
    return SettingsViewHolder(v, listener)
  }

  override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
    holder.bind(settings[position])
  }

  override fun getItemCount(): Int {
    return settings.size
  }
}