package com.retrox.aodmod.app.settings.fragments

import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.retrox.aodmod.app.settings.BaseSettingsActivity
import com.retrox.aodmod.app.settings.preference.ButtonsPreference
import com.retrox.aodmod.app.settings.preference.ListPreference
import com.retrox.aodmod.app.settings.preference.Preference
import com.retrox.aodmod.app.settings.preference.SwitchPreference
import com.retrox.aodmod.extensions.getBottomY
import com.retrox.aodmod.extensions.getToolbarHeight
import com.retrox.aodmod.extensions.resetPrefPermissions
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import org.jetbrains.anko.support.v4.dip


abstract class GenericPreferenceFragment : PreferenceFragmentCompat() {

    private val sActivity by lazy { activity as? BaseSettingsActivity }

    fun findSwitchPreference(key: String, invocation: (SwitchPreference) -> Unit){
        invocation.invoke(findPreference(key)!!)
    }

    fun findPreference(key: String, invocation: (Preference) -> Unit){
        invocation.invoke(findPreference(key)!!)
    }

    fun findListPreference(key: String, invocation: (ListPreference) -> Unit){
        invocation.invoke(findPreference(key)!!)
    }

    fun findButtonsPreference(key: String, invocation: (ButtonsPreference) -> Unit){
        invocation.invoke(findPreference(key)!!)
    }

    fun SwitchPreference.listen(callback: (Boolean) -> Unit){
        setOnPreferenceChangeListener { _, newValue ->
            callback.invoke(newValue as Boolean)
            sActivity?.loadPreview()
            resetPrefPermissions(context)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        listView.setPadding(0, getToolbarHeight(), 0, 0)
        listView.post {
            checkNestedScroll()
            listView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                sActivity?.setToolbarElevationEnabled(!isRecyclerViewAtTop(listView))
            }
        }
        listView.doOnApplyWindowInsets { view, insets, initialState ->
            val extraPadding = when(this){
                is SettingsMusicFragment -> dip(64)
                is SettingsWeatherFragment -> dip(64)
                else -> 0
            }
            val bottomPadding = if(this is SettingsClockAlignmentFragment) insets.stableInsetBottom else getToolbarHeight() + dip(16)
            listView.setPadding(0, getToolbarHeight() + insets.stableInsetTop + extraPadding, 0, bottomPadding)
        }
        listView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        listView.scrollBarStyle = RecyclerView.SCROLLBARS_INSIDE_INSET
    }

    fun checkNestedScroll() {
        listView.isNestedScrollingEnabled = isRecyclerScrollable(listView)
    }

    private fun getToolbarHeight(): Int {
        return context?.getToolbarHeight() ?: 0
    }

    private fun isRecyclerScrollable(recyclerView: RecyclerView): Boolean {
        val layoutManager = listView.layoutManager as LinearLayoutManager
        val adapter: RecyclerView.Adapter<*> = listView.adapter as RecyclerView.Adapter<*>
        val bottomNavigationTop = sActivity?.getBottomNavigationTop() ?: 0
        for(i in 0 until adapter.itemCount){
            val view = layoutManager.findViewByPosition(i)
            if(view != null) {
                if(view.getBottomY() >= bottomNavigationTop) return true
            }else{
                return true
            }
        }
        return false
    }

    private fun isRecyclerViewAtTop(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        return layoutManager.findFirstCompletelyVisibleItemPosition() == 0
    }

    fun scrollToTop() {
        listView.smoothScrollToPosition(0)
    }

    open fun getMasterSwitchTitle(): Int? {
        return null
    }

    open fun onMasterSwitchCheckedChange(checked: Boolean){
        //Do nothing
    }

    open fun isMasterSwitchChecked() : Boolean {
        return false
    }

}