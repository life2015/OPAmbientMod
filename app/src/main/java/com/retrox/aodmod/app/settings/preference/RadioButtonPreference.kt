package com.retrox.aodmod.app.settings.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceViewHolder
import com.retrox.aodmod.R
import org.jetbrains.anko.singleLine

class RadioButtonPreference : CheckBoxPreference {

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        widgetLayoutResource = R.layout.preference_widget_radiobutton
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        widgetLayoutResource = R.layout.preference_widget_radiobutton
    }

    constructor(context: Context?) : this(context, null) {}

    override fun onClick() {
        if (this.isChecked) {
            return
        }
        super.onClick()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val titleView = holder?.findViewById(android.R.id.title) as TextView
        titleView.singleLine = false
        titleView.typeface = ResourcesCompat.getFont(context, R.font.googlesans)
        val summaryView = holder.findViewById(android.R.id.summary) as? TextView
        summaryView?.maxLines = Int.MAX_VALUE
    }

}