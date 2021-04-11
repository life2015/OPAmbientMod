package com.retrox.aodmod.app.settings

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.retrox.aodmod.R
import com.retrox.aodmod.app.settings.fragments.GenericPreferenceFragment
import com.retrox.aodmod.app.util.isDarkTheme
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_settings_clock_alignment.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip

class SettingsClockAlignmentActivity : BaseSettingsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_clock_alignment)
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        Insetter.setEdgeToEdgeSystemUiFlags(window.decorView, true)
        if(!isDarkTheme()){
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.or(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR).or(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
        window.decorView.doOnApplyWindowInsets { view, insets, initialState ->
            toolbar.layoutParams.apply {
                this as FrameLayout.LayoutParams
                topMargin = insets.stableInsetTop
            }
            fakeStatusBar.layoutParams.height = insets.stableInsetTop
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun loadPreview(overriddenTheme: String?, updateHeight: Boolean, currentFragment: GenericPreferenceFragment?, disableAnimation: Boolean) {
        //No preview in here
    }

    override fun setToolbarElevationEnabled(enabled: Boolean) {
        val color = if(enabled) ContextCompat.getColor(this, R.color.toolbar_color) else ContextCompat.getColor(this, R.color.toolbar_color_solid)
        toolbar.elevation = dip(4).toFloat()
        toolbar.outlineProvider = ViewOutlineProvider.BACKGROUND
        toolbar.backgroundColor = color
        fakeStatusBar.backgroundColor = color
        fakeStatusBar.elevation = dip(4).toFloat()
    }

}