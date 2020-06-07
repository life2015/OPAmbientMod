package com.retrox.aodmod.proxy.view.theme

import java.lang.Exception


object ThemeManager {

    val defaultColorPack = getPresetThemes()[1]
    private var currentColorPack = defaultColorPack

    fun getPresetThemes(): List<ThemeClockPack> {
        return listOf(
            ThemeClockPack("white_default", "#FFFFFF", "#FFFFFF", "#FFFFFF"),
            ThemeClockPack("colorful_default", "#F6CDB0", "#93C4EF", "#DFCCC0"),
            ThemeClockPack("colorful_1", "#64CBFB", "#64CBFB", "#64CBFB"),
            ThemeClockPack("colorful_2", "#85AEFD", "#85AEFD", "#85AEFD"),
            ThemeClockPack("colorful_3", "#FF8181", "#FF8181", "#FF8181"),
            ThemeClockPack("colorful_4", "#EEBD70", "#EEBD70", "#EEBD70"),
            ThemeClockPack("colorful_5", "#ABD87D", "#ABD87D", "#ABD87D"),
            ThemeClockPack("colorful_6", "#F79CC5", "#F79CC5", "#F79CC5"),
            ThemeClockPack("colorful_7", "#D2A1E5", "#D2A1E5", "#D2A1E5"),
            ThemeClockPack("colorful_8", "#82DCCB", "#82DCCB", "#82DCCB"),
            ThemeClockPack("colorful_9", "#A4CCE7", "#BFE4DA", "#A4CCE7"),
            ThemeClockPack("colorful_10", "#8EDFAC", "#F0A6AB", "#D9DA85")

        )
    }

    fun getCurrentColorPack() = currentColorPack

    fun setCurrentColorPack(currentColorPack: ThemeClockPack){
        this.currentColorPack = currentColorPack
    }

    fun setThemePackSync(themeClockPack: ThemeClockPack) {
        themeClockPack.writeToFile()
    }

    /**
     * 为了IO性能 只进行一次Load 在DreamProxy里面处理
     */
    fun loadThemePackFromDisk(): ThemeClockPack {
        return try {
            val pack = ThemeClockPack.readFromFile()
            currentColorPack = pack
            pack
        } catch (e: Exception) {
            e.printStackTrace()
            defaultColorPack
        }
    }

    /*
        Does the theme have a clock at all
     */
    fun doesThemeSupportClock(theme: String): Boolean {
        return when(theme){
            "DVD" -> false
            else -> true
        }
    }

    /*
        Does the theme have the date shown
     */
    fun doesThemeSupportDate(theme: String): Boolean {
        return when(theme){
            "DVD", "PureMusic" -> false
            else -> true
        }
    }

    /*
        Does the theme have the today text, including the alarm, weather etc.
     */
    fun doesThemeSupportToday(theme: String) : Boolean {
        return when(theme){
            "DVD", "PureMusic", "FlatMusic", "Word" -> false
            else -> true
        }
    }

    /*
        Does the theme have the today text, including the alarm, weather etc.
     */
    fun doesThemeSupportWeatherCity(theme: String) : Boolean {
        return when(theme){
            "Default", "Pixel", "Flat" -> false
            else -> true
        }
    }

    /*
        Does the theme support displaying music at all
     */
    fun doesThemeSupportMusic(theme: String) : Boolean {
        return when(theme){
            "DVD", "Word" -> false
            else -> true
        }
    }

    /*
        Does the theme show the music at the bottom of the screen
     */
    fun doesThemeSupportMusicBottom(theme: String) : Boolean {
        return when(theme){
            "DVD", "Word", "Flat", "PureMusic", "FlatMusic" -> false
            else -> true
        }
    }

    /*
        Does the theme show the music icon
     */
    fun doesThemeSupportMusicIcon(theme: String) : Boolean {
        return when(theme){
            "DVD", "Word", "Flat", "PureMusic", "FlatMusic" -> false
            else -> true
        }
    }

    /*
        Does the theme have the today text, including the alarm, weather etc.
     */
    fun doesThemeSupportLyrics(theme: String) : Boolean {
        return when(theme){
            "Flat", "FlatMusic", "PureMusic" -> true
            else -> false
        }
    }

}