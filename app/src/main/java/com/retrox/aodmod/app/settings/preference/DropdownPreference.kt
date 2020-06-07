/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.retrox.aodmod.app.settings.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.retrox.aodmod.R
import org.jetbrains.anko.singleLine

/**
 * A [ListPreference] that presents the options in a drop down menu rather than a dialog.
 */
class DropdownPreference(
    private val mContext: Context?, attrs: AttributeSet?, defStyleAttr: Int,
    defStyleRes: Int
) :
    androidx.preference.ListPreference(mContext, attrs, defStyleAttr, defStyleRes) {
    private val mAdapter: ArrayAdapter<String>?
    private var mSpinner: Spinner? = null
    private val mItemSelectedListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                v: View,
                position: Int,
                id: Long
            ) {
                if (position >= 0) {
                    val value = entryValues[position].toString()
                    if (value != getValue() && callChangeListener(value)) {
                        setValue(value)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // noop
            }
        }

    @JvmOverloads
    constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyle: Int = R.attr.dropdownPreferenceStyle
    ) : this(context, attrs, defStyle, 0) {
    }

    override fun onClick() {
        mSpinner!!.performClick()
    }

    override fun setEntries(entries: Array<CharSequence>) {
        super.setEntries(entries)
        updateEntries()
    }

    /**
     * By default, this class uses a simple [ArrayAdapter]. But if you need a more
     * complicated adapter, this method can be overridden to create a custom one.
     *
     *
     * Note: This method is called from the constructor. Overridden methods will get called
     * before any subclass initialization.
     *
     * @return The custom [ArrayAdapter] that needs to be used with this class
     */
    protected fun createAdapter(): ArrayAdapter<String> {
        return ArrayAdapter<String>(
            mContext,
            R.layout.list_item_spinner
        )
    }

    private fun updateEntries() {
        mAdapter!!.clear()
        if (entries != null) {
            for (c in entries) {
                mAdapter.add(c.toString())
            }
        }
    }

    override fun setValueIndex(index: Int) {
        value = entryValues[index].toString()
    }

    override fun notifyChanged() {
        super.notifyChanged()
        // When setting a SummaryProvider for this Preference, this method may be called before
        // mAdapter has been set in ListPreference's constructor.
        mAdapter?.notifyDataSetChanged()
    }

    override fun onBindViewHolder(view: androidx.preference.PreferenceViewHolder) {
        mSpinner = view.itemView.findViewById(R.id.spinner)
        mSpinner!!.adapter = mAdapter
        mSpinner!!.onItemSelectedListener = mItemSelectedListener
        mSpinner!!.setSelection(findSpinnerIndexOfValue(value))
        val titleView = view.findViewById(android.R.id.title) as TextView
        titleView.singleLine = false
        titleView.typeface = ResourcesCompat.getFont(context, R.font.googlesans)
        val summaryView = view.findViewById(android.R.id.summary) as? TextView
        summaryView?.maxLines = Int.MAX_VALUE
        super.onBindViewHolder(view)
    }

    private fun findSpinnerIndexOfValue(value: String?): Int {
        val entryValues = entryValues
        if (value != null && entryValues != null) {
            for (i in entryValues.indices.reversed()) {
                if (entryValues[i] == value) {
                    return i
                }
            }
        }
        return Spinner.INVALID_POSITION
    }

    init {
        mAdapter = createAdapter()
        updateEntries()
    }
}