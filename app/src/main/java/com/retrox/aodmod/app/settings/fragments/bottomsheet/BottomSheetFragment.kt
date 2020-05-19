package com.retrox.aodmod.app.settings.fragments.bottomsheet

import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.retrox.aodmod.R
import com.retrox.aodmod.app.util.isDarkTheme
import kotlinx.android.synthetic.main.bottom_sheet_buttons.*


open class BottomSheetFragment : BottomSheetDialogFragment() {

    @LayoutRes
    var layout: Int? = null

    @StringRes
    var okLabel : Int? = null

    @StringRes
    var cancelLabel : Int? = null

    var okListener: (() -> Boolean)? = null
    var cancelListener: (() -> Boolean)? = null
    var neutralListener: (() -> Boolean)? = null

    var isSwipeable = false

    var showListener: ((View) -> Unit)? = null
    var dismissListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = isSwipeable
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layout?.let{
            return inflater.inflate(it, container, false)
        }
        return View(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(okListener != null){
            bottom_sheet_ok.visibility = View.VISIBLE
            bottom_sheet_ok.setOnClickListener {
                if(okListener?.invoke() == true){
                    dismiss()
                }
            }
        }
        if(cancelListener != null){
            bottom_sheet_cancel.visibility = View.VISIBLE
            bottom_sheet_cancel.setOnClickListener {
                if(cancelListener?.invoke() == true){
                    dismiss()
                }
            }
        }
        if(neutralListener != null){
            bottom_sheet_neutral.visibility = View.VISIBLE
            bottom_sheet_neutral.setOnClickListener {
                if(neutralListener?.invoke() == true){
                    dismiss()
                }
            }
        }
        okLabel?.let {
            bottom_sheet_ok.text = getString(it)
        }
        cancelLabel?.let {
            bottom_sheet_cancel.text = getString(it)
        }
        showListener?.invoke(view)
    }

    override fun getTheme(): Int {
        activity?.let {
            return if(it.isDarkTheme()) R.style.BaseBottomSheetDialog_Dark
            else R.style.BaseBottomSheetDialog
        }
        return R.style.BaseBottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog = BottomSheetDialog(requireContext(), theme)

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }

}