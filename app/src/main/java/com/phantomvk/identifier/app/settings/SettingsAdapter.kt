package com.phantomvk.identifier.app.settings

import android.view.Gravity
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class SettingsAdapter(
  activity: AppCompatActivity,
  private val listener: Runnable
) : RecyclerView.Adapter<SettingsViewHolder>() {

  private val settings: Array<Settings> = Settings.values()
  private val density = activity.resources.displayMetrics.density
  private val density4Float = 4 * density
  private val density8Float = 8 * density
  private val density8Int = density8Float.toInt()
  private val density16Int = (16 * density).toInt()

  private val lpSwitch = FrameLayout.LayoutParams(
    FrameLayout.LayoutParams.MATCH_PARENT,
    FrameLayout.LayoutParams.WRAP_CONTENT
  )

  private val lpCardView = FrameLayout.LayoutParams(
    FrameLayout.LayoutParams.MATCH_PARENT,
    FrameLayout.LayoutParams.WRAP_CONTENT
  ).apply {
    gravity = Gravity.START
    setMargins(density8Int, density8Int, density8Int, 0)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
    val switch = SwitchCompat(parent.context).apply {
      layoutParams = lpSwitch
      setPadding(density16Int, density16Int, density16Int, density16Int)
    }

    CardView(parent.context).apply {
      radius = density8Float
      cardElevation = density4Float
      layoutParams = lpCardView
      addView(switch)
    }

    return SettingsViewHolder(switch)
  }

  override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
    val item = settings[position]
    val switch = holder.switch

    switch.text = item.title
    switch.isChecked = item.getValue()
    switch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
      item.setValue(isChecked)
      listener.run()
    }
  }

  override fun getItemCount(): Int {
    return settings.size
  }
}