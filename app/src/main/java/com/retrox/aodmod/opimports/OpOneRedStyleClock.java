package com.retrox.aodmod.opimports;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;
import com.retrox.aodmod.R;
import com.retrox.aodmod.extensions.ExtensionKt;
import com.retrox.aodmod.extensions.ResourceUtils;
import com.retrox.aodmod.pref.XPref;
import com.retrox.aodmod.proxy.view.theme.ThemeClockPack;
import com.retrox.aodmod.proxy.view.theme.ThemeManager;

import java.util.Calendar;
import java.util.TimeZone;

import static org.jetbrains.anko.support.v4.SupportDimensionsKt.dip;

@SuppressLint("AppCompatCustomView")
public class OpOneRedStyleClock extends TextView {
    private CharSequence mFormat12;
    private CharSequence mFormat24;
    private int mSpecialColor;
    private Calendar mTime = null;

    public OpOneRedStyleClock(Context arg2) {
        super(arg2);
    }

    public OpOneRedStyleClock(Context arg1, int mSpecialColor, String mFormat12, String mFormat24) {
        super(arg1);
        this.mTime = Calendar.getInstance(TimeZone.getDefault());
        this.mSpecialColor = mSpecialColor;
        this.mFormat12 = mFormat12;
        this.mFormat24 = mFormat24;
        if(OPUtilsBridge.isMCLVersionFont()) {
            this.setTypeface(OPUtilsBridge.getMclTypeface(3));
        }
    }

    private boolean is24HourModeEnabled() {
        return XPref.INSTANCE.getIs24h();
    }

    @Override  // android.widget.TextView
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setTextSize(0, ((float) ResourceUtils.getInstance(getContext()).getDimensionPixelSize(OPUtilsBridge.isMCLVersion() ? R.dimen.oneplus_widget_big_font_size_mcl : R.dimen.oneplus_widget_big_font_size)));
    }

    public void onTimeChanged() {
        this.mTime.setTimeInMillis(System.currentTimeMillis());
        String v0 = DateFormat.format(this.is24HourModeEnabled() ? this.mFormat24 : this.mFormat12, this.mTime.getTime()).toString();
        SpannableString v1 = new SpannableString(v0);
        int v2 = v0.indexOf(":");
        ThemeClockPack currentTheme = ThemeManager.INSTANCE.getCurrentColorPack();
        int v4;
        for(v4 = 0; v4 < v2; ++v4) {
            if(49 == v0.charAt(v4)) {
                v1.setSpan(new ForegroundColorSpan(this.mSpecialColor), v4, v4 + 1, 33);
            }
        }
        if(currentTheme.isGradient()) {
            ExtensionKt.setGradientTest(this, ThemeManager.INSTANCE.getCurrentColorPack(), true);
        }else{
            setTextColor(Color.parseColor(ThemeManager.INSTANCE.getCurrentColorPack().getTintColor()));
        }
        this.setText(TextUtils.expandTemplate(v1, new CharSequence[0]));
    }
}

