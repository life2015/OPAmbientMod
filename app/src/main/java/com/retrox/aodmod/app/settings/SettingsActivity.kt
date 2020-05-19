package com.retrox.aodmod.app.settings

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.fragments.*
import com.retrox.aodmod.app.settings.fragments.bottomsheet.ThemePickerBottomSheetFragment
import com.retrox.aodmod.app.util.isDarkTheme
import com.retrox.aodmod.extensions.getTopY
import com.retrox.aodmod.extensions.resetPrefPermissions
import com.retrox.aodmod.fluidresize.FluidContentResizer
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.pref.XPref.context
import com.retrox.aodmod.proxy.view.aodMainView
import com.retrox.aodmod.proxy.view.aodMusicView
import com.retrox.aodmod.proxy.view.custom.dvd.dvdAodMainView
import com.retrox.aodmod.proxy.view.custom.flat.flatAodMainView
import com.retrox.aodmod.proxy.view.custom.music.clockView
import com.retrox.aodmod.proxy.view.custom.word.wordClockView
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.state.AodClockTick
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import java.util.*


class SettingsActivity : BaseSettingsActivity(), AppBarLayout.OnOffsetChangedListener {

    private val minuteHandler = Handler()
    private val minuteRunnable = Runnable {
        AodClockTick.tickLiveData.postValue(0)
        setupMinuteTimer()
    }

    private val generalFragment by lazy {
        SettingsGeneralFragment()
    }

    private val clockFragment by lazy {
        SettingsClockFragment()
    }

    private val musicFragment by lazy {
        SettingsMusicFragment()
    }

    private val weatherFragment by lazy {
        SettingsWeatherFragment()
    }

    private var currentPreviewHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.loadThemePackFromDisk()
        currentPreviewHeight = getCurrentPreviewHeight()
        setContentView(R.layout.activity_settings)
        window.statusBarColor = Color.TRANSPARENT
        Insetter.setEdgeToEdgeSystemUiFlags(window.decorView, true)
        if(!isDarkTheme()) window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.or(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            title = ""
        }
        bottom_navigation_view.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_nav_bar_settings_aod -> moveToFragment(generalFragment, it.title)
                R.id.menu_nav_bar_settings_weather -> moveToFragment(weatherFragment, it.title)
                R.id.menu_nav_bar_settings_music -> moveToFragment(musicFragment, it.title)
                R.id.menu_nav_bar_settings_clock -> moveToFragment(clockFragment, it.title)
            }
            setFilledIcon(it.itemId)
            true
        }
        if(savedInstanceState != null) {
            bottom_navigation_view.post {
                toolbar_title.setText(bottom_navigation_view.menu.findItem(bottom_navigation_view.selectedItemId).title)
            }
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            (currentFragment as? GenericPreferenceFragment)?.let {
                setupSwitch(it)
            }
        }else{
            moveToFragment(generalFragment, getString(R.string.menu_nav_bar_settings_aod))
        }
        toolbar_title.setInAnimation(this, R.anim.fade_in)
        toolbar_title.setOutAnimation(this, R.anim.fade_out)
        loadPreview(disableAnimation = true)
        setToolbarShadowEnabled()
        window.navigationBarColor = ContextCompat.getColor(this, R.color.navbar_color)
        FluidContentResizer.listen(this)
        appBarLayout.addOnOffsetChangedListener(this)
        window.decorView.doOnApplyWindowInsets { view, insets, initialState ->
            aod_preview_container.setPadding(0, insets.stableInsetTop + dip(16), 0, 0)
            toolbar.layoutParams.apply {
                this as FrameLayout.LayoutParams
                topMargin = insets.stableInsetTop
            }
            fakeStatusBar.layoutParams.height = insets.stableInsetTop
        }
        fakeStatusBar.backgroundColor = Color.BLACK
        button_back.setOnClickListener {
            finish()
        }
        button_theme.setOnClickListener {
            showThemeBottomSheet()
        }
    }

    private fun moveToFragment(fragment: Fragment, title: CharSequence){
        supportFragmentManager.findFragmentById(R.id.fragment_container)?.let {
            if(it == fragment) return
            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).remove(it).commit()
        }
        setToolbarShadowEnabled(true)
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).add(R.id.fragment_container, fragment).commit()
        toolbar_title.setText(title)
        appBarLayout.setExpanded(true, true)
        delayScrollCheck()
        delayStatusBarAnimation()
        (fragment as? GenericPreferenceFragment)?.let {
            setupSwitch(it)
            loadPreview(currentFragment = fragment)
        }
    }

    private fun setupSwitch(it: GenericPreferenceFragment){
        val switchTitle = it.getMasterSwitchTitle()
        if(switchTitle != null){
            master_switch.visibility = View.VISIBLE
            master_switch.text = getString(switchTitle)
            master_switch.setOnCheckedChangeListener(null)
            master_switch.isChecked = it.isMasterSwitchChecked()
            master_switch.setOnCheckedChangeListener { _, isChecked ->
                it.onMasterSwitchCheckedChange(isChecked)
                loadPreview()
            }
        }else{
            master_switch.visibility = View.GONE
        }
    }

    private fun delayScrollCheck() {
        Handler().postDelayed({
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if(currentFragment is GenericPreferenceFragment) currentFragment.checkNestedScroll()
        }, 500)
    }

    override fun loadPreview(overriddenTheme: String?, updateHeight: Boolean, currentFragment: GenericPreferenceFragment?, disableAnimation: Boolean){
        val isMusicFragment = currentFragment ?: supportFragmentManager.findFragmentById(R.id.fragment_container) is SettingsMusicFragment
        val currentTheme = overriddenTheme ?: if(isMusicFragment) "PureMusic" else XPref.getAodLayoutTheme()
        val dream = when (currentTheme){
            "Flat" -> flatAodMainView(this)
            "Default" -> aodMainView(this)
            "DVD" -> dvdAodMainView(this)
            "PureMusic" -> aodMusicView(this)
            "FlatMusic" -> clockView(this)
            "Word" -> wordClockView(this)
            else -> aodMainView(this)
        }

        currentPreviewHeight = getCurrentPreviewHeight(currentTheme)

        AodClockTick.tickLiveData.postValue(0)
        if(!disableAnimation) {
            aod_preview_container.layoutParams.apply {
                val animator = ValueAnimator.ofInt(height, currentPreviewHeight)
                animator.addUpdateListener {
                    this.height = it.animatedValue as Int
                    aod_preview_container.requestLayout()
                }
                animator.duration = 250
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.start()
            }
        }else{
            aod_preview_container.layoutParams.apply {
                this.height = currentPreviewHeight
            }
        }
        aod_preview_container.removeAllViews()
        aod_preview_container.addView(dream)
        dream.layoutParams.apply {
            this as FrameLayout.LayoutParams
            if(currentTheme == "FlatMusic"){
                marginStart = dip(16)
                topMargin = dip(15)
            }else if(currentTheme == "PureMusic"){
                topMargin = dip(32)
            }
        }
        if(updateHeight || isMusicFragment){
            //Animate the height change
            //Disabled for now - TransitionManager.beginDelayedTransition(appBarLayout)
        }else{
            if(animationState == AnimationState.WHITE) {
                delayStatusBarAnimation(ContextCompat.getColor(this, R.color.toolbar_color), AnimationState.WHITE)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showThemeBottomSheet() {
        appBarLayout.setExpanded(true, true)
        when(val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)){
            is GenericPreferenceFragment -> currentFragment.scrollToTop()
        }
        ThemePickerBottomSheetFragment().show(supportFragmentManager, "theme_sheet")
    }

    private fun setToolbarShadowEnabled(forceEnable: Boolean? = null) {
        val isEnabled = forceEnable ?: true
        toolbar.outlineProvider = if(isEnabled) ViewOutlineProvider.BACKGROUND else null
    }

    private fun setFilledIcon(selectedItem: Int){
        bottom_navigation_view.menu.run {
            forEach {
                it.setIcon(when(it.itemId){
                    R.id.menu_nav_bar_settings_aod -> R.drawable.ic_aod
                    R.id.menu_nav_bar_settings_clock -> R.drawable.ic_clock
                    R.id.menu_nav_bar_settings_music -> R.drawable.ic_music_nav
                    R.id.menu_nav_bar_settings_weather -> R.drawable.ic_weather
                    else -> 0
                })
            }
            when(selectedItem){
                R.id.menu_nav_bar_settings_music -> findItem(selectedItem).setIcon(R.drawable.ic_music_filled)
                R.id.menu_nav_bar_settings_weather -> findItem(selectedItem).setIcon(R.drawable.ic_weather_filled)
                else -> null
            }
        }
    }

    private fun getCurrentPreviewHeight(currentTheme: String? = null): Int {
        return when (currentTheme ?: XPref.getAodLayoutTheme()){
            "Flat" -> if(AppPref.forceShowWordClockOnFlat) dip(250) else dip(200)
            "Default" -> dip(175)
            "DVD" -> dip(150)
            "PureMusic" -> dip(150)
            "FlatMusic" -> dip(150)
            "Word" -> dip(250)
            else -> dip(150)
        }
    }

    override fun setToolbarElevationEnabled(enabled: Boolean){
        val color = if(enabled) ContextCompat.getColor(this, R.color.toolbar_color) else ContextCompat.getColor(this, R.color.toolbar_color_solid)
        toolbar.elevation = dip(4).toFloat()
        toolbar.outlineProvider = ViewOutlineProvider.BACKGROUND
        toolbar.backgroundColor = color
        fakeStatusBar.elevation = dip(4).toFloat()
    }

    override fun getBottomNavigationTop(): Int {
        return bottom_navigation_view.getTopY()
    }

    private var animationState = AnimationState.BLACK
    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if(animationState != AnimationState.ANIMATING) {
            if (-verticalOffset == currentPreviewHeight && animationState == AnimationState.BLACK) {
                //Animate the status bar to the toolbar colour
                val colorStatusAnimation = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    Color.BLACK,
                    ContextCompat.getColor(this, R.color.toolbar_color)
                )
                colorStatusAnimation.duration = 250
                colorStatusAnimation.addUpdateListener {
                    if(it.animatedFraction == 1f) animationState = AnimationState.WHITE
                    if(it.animatedFraction > 0.5f && !isDarkTheme()) window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.or(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    fakeStatusBar.backgroundColor = it.animatedValue as Int
                }
                animationState = AnimationState.ANIMATING
                colorStatusAnimation.start()
            }else if(animationState == AnimationState.WHITE){
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.rem(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                fakeStatusBar.backgroundColor = Color.BLACK
                animationState = AnimationState.BLACK
            }
        }
    }

    private fun delayStatusBarAnimation(color: Int = Color.BLACK, postAnimationState: AnimationState = AnimationState.BLACK){
        //Hack to fix the fake status bar not hiding
        animationState = AnimationState.ANIMATING
        fakeStatusBar.backgroundColor = color
        if(postAnimationState == AnimationState.BLACK){
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.rem(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }else{
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.or(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
        Handler().postDelayed({
            runOnUiThread {
                animationState = postAnimationState
            }
        }, 500)
    }

    fun setMasterSwitchChecked(checked: Boolean) {
        master_switch?.isChecked = checked
    }

    override fun onResume() {
        super.onResume()
        setupMinuteTimer()
    }

    override fun onPause() {
        super.onPause()
        resetPrefPermissions(this)
        cancelMinuteTimer()
    }

    private fun setupMinuteTimer(){
        minuteHandler.postDelayed(minuteRunnable, DateUtils.MINUTE_IN_MILLIS - System.currentTimeMillis() % DateUtils.MINUTE_IN_MILLIS);
    }

    private fun cancelMinuteTimer(){
        minuteHandler.removeCallbacks(minuteRunnable)
    }

    enum class AnimationState {
        WHITE,
        ANIMATING,
        BLACK
    }

}