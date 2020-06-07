package com.retrox.aodmod.app.settings.fragments.bottomsheet

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.SettingsActivity
import com.retrox.aodmod.extensions.resetPrefPermissions
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.view.theme.ThemeClockPack
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import kotlinx.android.synthetic.main.fragment_bottomsheet_theme_picker.*


class ThemePickerBottomSheetFragment : BottomSheetFragment() {

    init{
        layout = R.layout.fragment_bottomsheet_theme_picker
        okLabel = android.R.string.ok
        cancelLabel = android.R.string.cancel
        isSwipeable = true
        okListener = {
            AppPref.aodLayoutTheme = currentTheme
            ThemeManager.setThemePackSync(currentColour)
            resetPrefPermissions(context)
            sActivity?.onLayoutChanged()
            true
        }
        cancelListener = {true}
        dismissListener = {
            ThemeManager.loadThemePackFromDisk()
            sActivity?.loadPreview(updateHeight = true)
        }
    }

    private val themeImages by lazy { arrayOf(imageview_theme_default, imageview_theme_flat, imageview_theme_dvd, imageview_theme_pixel, imageview_theme_pure_music, imageview_theme_flat_music, imageview_theme_word, imageview_theme_oneplus) }

    private val themeNames = arrayOf("Default", "Flat", "DVD", "Pixel", "PureMusic", "FlatMusic", "Word", "OnePlus")

    private val circleViews by lazy { arrayOf(color_circle_1, color_circle_2, color_circle_3, color_circle_4, color_circle_5, color_circle_6, color_circle_7, color_circle_8, color_circle_9, color_circle_10, color_circle_11, color_circle_12)}

    private val sActivity by lazy { activity as? SettingsActivity }

    private var currentTheme: String = XPref.getAodLayoutTheme()

    private var currentColour: ThemeClockPack = ThemeManager.loadThemePackFromDisk()

    override fun onResume() {
        super.onResume()
        themeImages.forEach {
            it.clipToOutline = true
            it.setOnClickListener { item ->
                val position = themeImages.indexOf(item)
                setSelectedTheme(position)
                sActivity?.loadPreview(themeNames[position], true)
            }
        }
        val themeGradients = ThemeManager.getPresetThemes()
        circleViews.forEachIndexed { index, view ->
            val theme = themeGradients[index]
            val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.parseColor(theme.gradientStart), Color.parseColor(theme.gradientEnd)))
            view.setImageDrawable(gradient)
            view.clipToOutline = true
            view.setOnClickListener { item ->
                setSelectedColour(index)
                sActivity?.loadPreview(currentTheme, true)
            }
        }
        setSelectedTheme(themeNames.indexOf(XPref.getAodLayoutTheme()))
        setSelectedColour(themeGradients.indexOf(currentColour))
    }

    private fun setSelectedTheme(position: Int){
        themeImages.forEach {
            it.foreground = null
        }
        currentTheme = themeNames[position]
        themeImages[position].foreground = ContextCompat.getDrawable(themeImages[position].context, R.drawable.rounded_clip_selected)
    }

    private fun setSelectedColour(position: Int){
        circleViews.forEach {
            it.foreground = ContextCompat.getDrawable(circleViews[position].context, R.drawable.circle_clip_foreground)
        }
        val themes = ThemeManager.getPresetThemes()
        currentColour = themes[position]
        ThemeManager.setCurrentColorPack(currentColour)
        circleViews[position].foreground = ContextCompat.getDrawable(circleViews[position].context, R.drawable.circle_clip_foreground_selected)
    }

}