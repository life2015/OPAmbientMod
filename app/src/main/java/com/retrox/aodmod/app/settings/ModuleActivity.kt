package com.retrox.aodmod.app.settings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.fragments.GenericPreferenceFragment
import com.retrox.aodmod.app.util.isDarkTheme
import com.retrox.aodmod.app.view.NewMainActivity
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_module.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip

class ModuleActivity : BaseSettingsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            title = ""
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
        saveStaticTranslations()
    }

    override fun loadPreview(overriddenTheme: String?, updateHeight: Boolean, currentFragment: GenericPreferenceFragment?, disableAnimation: Boolean) {
        //No preview on this screen
    }

    override fun setToolbarElevationEnabled(enabled: Boolean) {
        val color = if(enabled) ContextCompat.getColor(this, R.color.toolbar_color) else ContextCompat.getColor(this, R.color.toolbar_color_solid)
        toolbar.elevation = dip(4).toFloat()
        toolbar.outlineProvider = ViewOutlineProvider.BACKGROUND
        toolbar.backgroundColor = color
        fakeStatusBar.backgroundColor = color
        fakeStatusBar.elevation = dip(4).toFloat()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_module, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_module_old_settings -> {
                startActivity(Intent(this, NewMainActivity::class.java))
            }
            R.id.menu_module_third_party -> {
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.settings_module_menu_third_party_licences))
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveStaticTranslations(){
        AppPref.translationConstantLightMode = getString(R.string.xposed_constant_light_mode_7pro)
        AppPref.translationConstantLightModeNS = getString(R.string.xposed_constant_light_mode_7pro_ns)
    }

}