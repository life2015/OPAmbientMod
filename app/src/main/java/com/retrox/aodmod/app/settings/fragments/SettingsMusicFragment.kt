package com.retrox.aodmod.app.settings.fragments

import android.os.Bundle
import androidx.preference.forEach
import com.retrox.aodmod.R
import com.retrox.aodmod.app.XposedUtils
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.extensions.resetPrefPermissions
import com.retrox.aodmod.shared.global.GlobalKV

class SettingsMusicFragment : GenericPreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_music)
        findSwitchPreference("settings_music_system_music"){
            it.isChecked = AppPref.useSystemMusic
            it.listen { value ->
                AppPref.useSystemMusic = value
            }
        }
        findSwitchPreference("settings_music_pixel_icon"){
            it.isChecked = AppPref.usePixelMusicIcon
            it.listen { value ->
                AppPref.usePixelMusicIcon = value
            }
        }
        findSwitchPreference("settings_music_offset"){
            it.isChecked = AppPref.musicDisplayOffset
            it.listen { value ->
                AppPref.musicDisplayOffset = value
            }
        }
        findSwitchPreference("settings_lyrics_translation"){
            it.isChecked = GlobalKV.get("lrc_trans")?.toBoolean() ?: false
            it.listen { value ->
                GlobalKV.put("lrc_trans", value.toString())
            }
        }
        findPreference("settings_music_taichi"){
            if(!XposedUtils.isExpModuleActive(context)){
                it.isVisible = false
            }
            it.setOnPreferenceClickListener {
                val intent = context?.packageManager?.getLaunchIntentForPackage(XposedUtils.TAICHI_PACKAGE_NAME)
                startActivity(intent)
                true
            }
        }
        setAllEnabled(AppPref.musicShowOnAod)
    }

    fun setAllEnabled(enabled: Boolean){
        preferenceScreen.forEach {
            it.isEnabled = enabled
        }
    }

    override fun getMasterSwitchTitle(): Int? {
        return R.string.settings_music_master_switch
    }

    override fun onMasterSwitchCheckedChange(checked: Boolean) {
        super.onMasterSwitchCheckedChange(checked)
        setAllEnabled(checked)
        AppPref.musicShowOnAod = checked
        resetPrefPermissions(context)
    }

    override fun isMasterSwitchChecked(): Boolean {
        return AppPref.musicShowOnAod
    }

}