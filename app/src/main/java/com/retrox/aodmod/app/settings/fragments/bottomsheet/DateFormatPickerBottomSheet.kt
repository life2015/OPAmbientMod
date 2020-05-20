package com.retrox.aodmod.app.settings.fragments.bottomsheet

import android.os.Bundle
import android.view.View
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.SettingsActivity
import com.retrox.aodmod.extensions.dateFormats
import com.retrox.aodmod.extensions.getDateFormatted
import com.retrox.aodmod.extensions.resetPrefPermissions
import kotlinx.android.synthetic.main.fragment_bottomsheet_date_format.*
import kotlinx.android.synthetic.main.item_radio_button.view.*

class DateFormatPickerBottomSheet : BottomSheetFragment() {

    private var selectedDateFormat = AppPref.dateFormat

    init{
        layout = R.layout.fragment_bottomsheet_date_format
        okLabel = android.R.string.ok
        cancelLabel = android.R.string.cancel
        isSwipeable = true
        cancelListener = {true}
        okListener = {
            AppPref.dateFormat = selectedDateFormat
            (activity as? SettingsActivity)?.loadPreview()
            resetPrefPermissions(context)
            true
        }
    }

    companion object {
        const val KEY_SELECTED_FORMAT = "selected_format"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedDateFormat = savedInstanceState?.getString(KEY_SELECTED_FORMAT) ?: AppPref.dateFormat
        val formattedDateFormats = arrayOf<CharSequence>(
                getDateFormatted(dateFormats[0]),
                getDateFormatted(dateFormats[1]),
                getDateFormatted(dateFormats[2]),
                getDateFormatted(dateFormats[3]),
                getString(R.string.custom_settings_date_format_no_leading, getDateFormatted(dateFormats[4])),
                getString(R.string.custom_settings_date_format_no_leading, getDateFormatted(dateFormats[5]))
        )
        val views = arrayOfNulls<View>(dateFormats.size)
        val dateFormatIndex = dateFormats.indexOf(selectedDateFormat)
        formattedDateFormats.forEachIndexed { index, dateFormat ->
            val itemView = layoutInflater.inflate(R.layout.item_radio_button, bs_date_format_options, false)
            itemView.radioButton.text = dateFormat
            itemView.setOnClickListener {
                views.forEach { itemView ->
                    if(itemView != it) {
                        itemView?.radioButton?.isChecked = false
                    }
                }
                itemView?.radioButton?.isChecked = true
                selectedDateFormat = dateFormats[index]
            }
            views[index] = itemView
        }
        views[dateFormatIndex]?.radioButton?.let {
            it.post {
                it.isChecked = true
            }
        }
        bs_date_format_options.removeAllViews()
        views.forEach {
            bs_date_format_options.addView(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SELECTED_FORMAT, selectedDateFormat)
    }

}