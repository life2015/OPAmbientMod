package com.retrox.aodmod.app.settings.fragments

import android.os.Bundle
import androidx.preference.forEach
import com.retrox.aodmod.R
import com.retrox.aodmod.app.XposedUtils
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.extensions.resetPrefPermissions
import com.retrox.aodmod.extensions.runAfter
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.shared.global.GlobalKV

class SettingsMusicFragment : GenericPreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_music)
        setupPreferences()
    }

    private fun setupPreferences(){
        val currentTheme = AppPref.aodLayoutTheme
        val isMusicSupported = ThemeManager.doesThemeSupportMusic(currentTheme)
        val isBottomMusicSupported = ThemeManager.doesThemeSupportMusicBottom(currentTheme)
        val isMusicIconSupported = ThemeManager.doesThemeSupportMusicIcon(currentTheme)
        val isLyricsSupported = ThemeManager.doesThemeSupportLyrics(currentTheme)
        findSwitchPreference("settings_music_system_music"){
            it.isChecked = AppPref.useSystemMusic
            it.isVisible = isMusicIconSupported
            it.listen { value ->
                AppPref.useSystemMusic = value
            }
        }
        findSwitchPreference("settings_music_pixel_icon"){
            it.isChecked = AppPref.usePixelMusicIcon
            it.isEnabled = AppPref.aodLayoutTheme != "Pixel" || !AppPref.pixelSmallMusic
            it.isVisible = isMusicIconSupported
            it.listen { value ->
                AppPref.usePixelMusicIcon = value
            }
        }
        findSwitchPreference("settings_music_offset"){
            it.isChecked = AppPref.musicDisplayOffset
            it.isVisible = isBottomMusicSupported
            it.listen { value ->
                AppPref.musicDisplayOffset = value
            }
        }
        findSwitchPreference("settings_lyrics"){
            it.isVisible = isLyricsSupported
            it.isChecked = AppPref.lyricsEnabled
            it.listen { value ->
                AppPref.lyricsEnabled = value
            }
        }
        findSwitchPreference("settings_lyrics_translation"){
            it.isVisible = isLyricsSupported
            it.isChecked = GlobalKV.get("lrc_trans")?.toBoolean() ?: false
            it.listen { value ->
                GlobalKV.put("lrc_trans", value.toString())
            }
        }
        findPreference("settings_music_taichi"){
            if(!XposedUtils.isExpModuleActive(context)){
                it.isVisible = false
            }else{
                it.isVisible = isMusicSupported
            }
            it.setOnPreferenceClickListener {
                val intent = context?.packageManager?.getLaunchIntentForPackage(XposedUtils.TAICHI_PACKAGE_NAME)
                startActivity(intent)
                true
            }
        }
        findSwitchPreference("settings_music_small_music"){
            it.isVisible = AppPref.aodLayoutTheme == "Pixel"
            it.isChecked = AppPref.pixelSmallMusic
            it.listen { value ->
                AppPref.pixelSmallMusic = value
                findSwitchPreference("settings_music_pixel_icon"){icon ->
                    icon.isEnabled = !AppPref.pixelSmallMusic
                }
            }
        }
        setAllEnabled(AppPref.musicShowOnAod)
        findPreference("generic_theme_unsupported"){
            it.isVisible = !isBottomMusicSupported && !isLyricsSupported && !isMusicSupported && !isMusicIconSupported
        }
        setMasterSwitchEnabled(isMusicSupported)
    }

    fun setAllEnabled(enabled: Boolean){
        preferenceScreen.forEach {
            if(it.key == "settings_music_pixel_icon"){
                it.isEnabled = AppPref.aodLayoutTheme != "Pixel" || !AppPref.pixelSmallMusic
            }else {
                it.isEnabled = enabled
            }
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

    override fun onLayoutChanged(newLayout: String) {
        super.onLayoutChanged(newLayout)
        setupPreferences()
        runAfter(0.5){
            checkNestedScroll()
        }
    }

}