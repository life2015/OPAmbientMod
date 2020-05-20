package com.retrox.aodmod.app.settings.fragments.bottomsheet

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.SettingsActivity
import com.retrox.aodmod.extensions.resetPrefPermissions
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.view.theme.ThemeClockPack
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.shared.SharedContentManager
import kotlinx.android.synthetic.main.fragment_bottomsheet_debug_info.*
import kotlinx.android.synthetic.main.fragment_bottomsheet_input.*

class DebugInfoBottomSheetFragment : BottomSheetFragment() {

    init{
        layout = R.layout.fragment_bottomsheet_debug_info
        okLabel = android.R.string.ok
        isSwipeable = true
        okListener = {
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedState = SharedContentManager.getSharedState(view.context)
        content.text = getString(R.string.bottom_sheet_debug_info_desc, sharedState.workMode, sharedState.aodTimes, sharedState.lastTime)
    }

}