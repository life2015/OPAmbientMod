package com.retrox.aodmod.app

import android.annotation.DrawableRes
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.appendSpace
import com.retrox.aodmod.opimports.OPUtilsBridge
import com.retrox.aodmod.opimports.OpClockViewCtrl
import com.retrox.aodmod.util.CustomTypefaceSpan
import org.jetbrains.anko.dip

class FontActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OPUtilsBridge.init(this)
        window.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        val dateTimeView = layoutInflater.inflate(R.layout.op_aod_date_time_view, null, false)
        setContentView(dateTimeView)
        val clockControl = OpClockViewCtrl(this, dateTimeView as ViewGroup)
        clockControl.onTimeChanged()
    }

}