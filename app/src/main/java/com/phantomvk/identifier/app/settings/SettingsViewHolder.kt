package com.phantomvk.identifier.app.settings

import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.phantomvk.identifier.app.R

class SettingsViewHolder(itemView: View) : ViewHolder(itemView) {
  private val switchItem: SwitchCompat = itemView.findViewById(R.id.switch_item)

  fun bind(item: Settings) {
    switchItem.text = item.title
    switchItem.isChecked = item.getValue()
    switchItem.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
      item.setValue(isChecked)
    }
  }
}
