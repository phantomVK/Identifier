package com.phantomvk.identifier.app.settings

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class SettingsAdapter(
  activity: AppCompatActivity,
  private val listener: Runnable
) : RecyclerView.Adapter<ViewHolder>() {

  private val settings: ArrayList<Any>
  private val density = activity.resources.displayMetrics.density
  private val density4Float = 4 * density
  private val density8Float = 8 * density
  private val density8Int = density8Float.toInt()
  private val density16Int = (16 * density).toInt()

  init {
    val actionList = Actions.values().toList()
    val settingList = Settings.values().toList()
    settings = ArrayList(actionList.size + settingList.size)
    settings.addAll(actionList)
    settings.addAll(settingList)
  }

  private val lpText = FrameLayout.LayoutParams(
    FrameLayout.LayoutParams.MATCH_PARENT,
    FrameLayout.LayoutParams.MATCH_PARENT
  )

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

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return when (viewType) {
      0 -> {
        val switch = SwitchCompat(parent.context).apply {
          layoutParams = lpSwitch
          setPadding(density16Int, density16Int, density16Int, density16Int)
        }

        CardView(switch.context).apply {
          radius = density8Float
          cardElevation = density4Float
          layoutParams = lpCardView
          addView(switch)
        }

        SettingsViewHolder(switch)
      }

      1 -> {
        val textView = AppCompatTextView(parent.context).apply {
          gravity = Gravity.CENTER
          layoutParams = lpText
          setTextColor(Color.BLACK)
          setPadding(density16Int, density16Int, density16Int, density16Int)
        }

        CardView(textView.context).apply {
          radius = density8Float
          cardElevation = density4Float
          layoutParams = lpCardView
          addView(textView)
        }

        ActionsViewHolder(textView)
      }

      else -> {
        val textView = AppCompatTextView(parent.context).apply {
          layoutParams = lpText
          setTextColor(Color.BLACK)
          setPadding(density16Int, density16Int, density16Int, density16Int)
        }

        CardView(textView.context).apply {
          radius = density8Float
          cardElevation = density4Float
          layoutParams = lpCardView
          addView(textView)
        }

        DisplaysViewHolder(textView)
      }
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = settings[position]
    when (holder) {
      is SettingsViewHolder -> holder.onBind(item as Settings)
      is ActionsViewHolder -> holder.onBind(item as Actions)
      is DisplaysViewHolder -> holder.onBind(item as String)
    }
  }

  override fun getItemViewType(position: Int): Int {
    return when (settings[position]) {
      is Settings -> 0
      is Actions -> 1
      else -> 2
    }
  }

  override fun getItemCount(): Int {
    return settings.size
  }

  private inner class SettingsViewHolder(
    private val switch: SwitchCompat
  ) : ViewHolder(switch.parent as View) {
    fun onBind(item: Settings) {
      switch.text = item.title
      switch.setOnCheckedChangeListener(null)
      switch.isChecked = item.getValue()
      switch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
        item.setValue(isChecked)
        listener.run()
      }
    }
  }

  private inner class ActionsViewHolder(
    private val textView: AppCompatTextView
  ) : ViewHolder(textView.parent as View) {
    fun onBind(item: Actions) {
      textView.text = item.title
      textView.setOnClickListener(item.listener)
    }
  }

  private inner class DisplaysViewHolder(
    private val textView: AppCompatTextView
  ) : ViewHolder(textView.parent as View) {
    fun onBind(item: String) {
      textView.text = item
    }
  }
}