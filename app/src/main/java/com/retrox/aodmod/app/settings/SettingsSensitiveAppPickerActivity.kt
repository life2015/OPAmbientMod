package com.retrox.aodmod.app.settings

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.retrox.aodmod.app.settings.fragments.SettingsSensitiveAppPickerFragment
import com.retrox.aodmod.app.util.isDarkTheme
import dev.chrisbanes.insetter.Insetter

class SettingsSensitiveAppPickerActivity: AppCompatActivity() {

    private val fragment by lazy {
        SettingsSensitiveAppPickerFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        Insetter.setEdgeToEdgeSystemUiFlags(window.decorView, true)
        if(!isDarkTheme()) window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.or(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR).or(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, fragment, "sensitive_apps_picker").commit()
    }

}