package com.retrox.aodmod.app.settings.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import androidx.preference.PreferenceViewHolder
import com.google.android.material.button.MaterialButton
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.getDrawableC
import org.jetbrains.anko.*

class ButtonsPreference : androidx.preference.Preference {

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    private val buttons = ArrayList<MaterialButton>()

    var buttonsOnNewLine = false

    private var hasClickListener = false

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val titleView = holder?.findViewById(android.R.id.title) as TextView
        val summaryView = holder.findViewById(android.R.id.summary) as? TextView
        summaryView?.maxLines = Int.MAX_VALUE
        titleView.singleLine = false
        titleView.typeface = ResourcesCompat.getFont(context, R.font.googlesans)
        containerView = titleView.parent as RelativeLayout
        containerView?.updateLayoutParams<LinearLayout.LayoutParams> {
            topMargin = 0
            bottomMargin = 0
            leftMargin = 0
            rightMargin = 0
        }
        containerView?.setPadding(0, context.dip(6), context.dip(6), context.dip(12))
        containerView?.clipToPadding = false
        clearButtons()
        addButtonsInternal()
        containerView?.post {
            if(!hasClickListener){
                holder.itemView.isClickable = false
                holder.itemView.isFocusable = false
            }
        }
    }

    fun clearButtons() {
        containerView?.removeViewOptional(containerView?.findViewById<MaterialButton>(android.R.id.button1))
        containerView?.removeViewOptional(containerView?.findViewById<MaterialButton>(android.R.id.button2))
        containerView?.removeViewOptional(containerView?.findViewById<MaterialButton>(android.R.id.button3))
    }

    fun clearButtonsList(){
        buttons.clear()
    }

    override fun setOnPreferenceClickListener(onPreferenceClickListener: OnPreferenceClickListener?) {
        super.setOnPreferenceClickListener(onPreferenceClickListener)
        hasClickListener = onPreferenceClickListener != null
    }

    private fun RelativeLayout.removeViewOptional(view: View?) {
        if (view != null) removeView(view)
    }

    fun addButton(@StringRes title: Int, @DrawableRes icon: Int?, @ColorRes backgroundColor: Int = R.color.colorAccent, callback: () -> Unit) {
        val materialButton = MaterialButton(context)
        materialButton.text = context.getString(title)
        materialButton.setBackgroundColor(context.getColor(backgroundColor))
        materialButton.typeface = ResourcesCompat.getFont(context, R.font.googlesans_medium)
        materialButton.allCaps = false
        icon?.let {
            materialButton.icon = context.getDrawableC(it)
            materialButton.iconGravity = MaterialButton.ICON_GRAVITY_START
        }
        materialButton.setOnClickListener {
            callback.invoke()
        }
        buttons.add(materialButton)
    }

    private var containerView: RelativeLayout? = null
    private fun addButtonsInternal() {
        buttons.forEachIndexed { index, materialButton ->
            val id = when (index) {
                0 -> android.R.id.button1
                1 -> android.R.id.button2
                2 -> android.R.id.button3
                else -> throw Exception("Only 3 buttons are supported")
            }
            materialButton.id = id
            containerView?.addView(applyButton(materialButton) {
                if (buttonsOnNewLine) {
                    when (index) {
                        0 -> {
                            //Just below summary
                            it.below(android.R.id.summary)
                            it.marginStart = context.dip(8)
                            it.topMargin = context.dip(8)
                        }
                        1 -> {
                            //Below button1
                            it.below(android.R.id.button1)
                            it.marginStart = context.dip(8)
                        }
                        2 -> {
                            //Below button2
                            it.below(android.R.id.button2)
                            it.marginStart = context.dip(8)
                        }
                    }
                } else {
                    when (index) {
                        0 -> {
                            //Just below summary
                            it.below(android.R.id.summary)
                            it.topMargin = context.dip(8)
                            it.marginStart = context.dip(8)
                        }
                        1 -> {
                            //Below summary, to right of button1
                            it.below(android.R.id.summary)
                            it.rightOf(android.R.id.button1)
                            it.topMargin = context.dip(8)
                            it.marginStart = context.dip(8)
                        }
                        2 -> {
                            //Below button1
                            it.below(android.R.id.button1)
                            it.marginStart = context.dip(8)
                        }
                    }
                }

            })
        }
    }

    private fun applyButton(
        materialButton: MaterialButton,
        paramsCallback: (RelativeLayout.LayoutParams) -> Unit
    ): MaterialButton {
        materialButton.post {
            materialButton.updateLayoutParams<RelativeLayout.LayoutParams> {
                paramsCallback.invoke(this)
            }
        }
        return materialButton
    }


}