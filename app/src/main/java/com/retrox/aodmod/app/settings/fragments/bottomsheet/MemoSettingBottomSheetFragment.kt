package com.retrox.aodmod.app.settings.fragments.bottomsheet

import android.os.Bundle
import android.view.View
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.SettingsActivity
import com.retrox.aodmod.extensions.resetPrefPermissions
import kotlinx.android.synthetic.main.fragment_bottomsheet_input.*

class MemoSettingBottomSheetFragment : BottomSheetFragment() {

    init{
        layout = R.layout.fragment_bottomsheet_input
        okLabel = android.R.string.ok
        cancelLabel = android.R.string.cancel
        isSwipeable = true
        cancelListener = {true}
        okListener = {
            val memoContent = bs_edittext.text?.toString()
            if(memoContent?.isNotBlank() == true){
                AppPref.aodNoteContent = memoContent
                AppPref.aodShowNote = true
            }else{
                AppPref.aodNoteContent = ""
                AppPref.aodShowNote = true
            }
            (activity as? SettingsActivity)?.loadPreview()
            resetPrefPermissions(context)
            true
        }
    }

    companion object {
        const val KEY_TEXT = "text"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bs_edittext.setText(savedInstanceState?.getString(KEY_TEXT) ?: AppPref.aodNoteContent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TEXT, bs_edittext.text?.toString())
    }

}