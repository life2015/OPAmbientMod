package com.retrox.aodmod.opimports;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.retrox.aodmod.R;
import com.retrox.aodmod.extensions.ExtensionKt;
import com.retrox.aodmod.extensions.ResourceUtils;
import com.retrox.aodmod.pref.XPref;
import com.retrox.aodmod.proxy.view.theme.ThemeClockPack;
import com.retrox.aodmod.proxy.view.theme.ThemeManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

@RemoteViews.RemoteView
public class OpTextClock extends View {
    public static final CharSequence DEFAULT_FORMAT_12_HOUR = "h:mm a";
    public static final CharSequence DEFAULT_FORMAT_24_HOUR = "H:mm";
    private int mClockStyle;
    private Context mContext;
    private CharSequence mDescFormat;
    private CharSequence mDescFormat12;
    private CharSequence mDescFormat24;
    private int mDigitColorRed;
    private int mDigitColorWhite;
    private float mFontBaseLineY;
    @ViewDebug.ExportedProperty
    private CharSequence mFormat;
    private CharSequence mFormat12;
    private CharSequence mFormat24;
    @ViewDebug.ExportedProperty
    private boolean mHasSeconds = false;
    public Paint mHourPaint;
    public Paint mMinPaint;
    private boolean mShowCurrentUserTime;
    private Calendar mTime;
    private String mTimeZone;
    private ResourceUtils resourceUtils;

    public OpTextClock(Context arg2) {
        super(arg2);
        this.resourceUtils = ResourceUtils.getInstance(arg2);
        this.mHourPaint = new Paint();
        this.mMinPaint = new Paint();
        this.mContext = arg2;
        this.init();
    }

    @SuppressLint("ResourceType")
    public OpTextClock(Context arg2, String mFormat12, String mFormat24, String mTimeZone) {
        super(arg2);
        this.resourceUtils = ResourceUtils.getInstance(arg2);
        this.mHourPaint = new Paint();
        this.mMinPaint = new Paint();
        this.mContext = arg2;
        this.mFormat12 = mFormat12;
        this.mFormat24 = mFormat24;
        this.mTimeZone = mTimeZone;
        this.init();
    }

    private static CharSequence abc(CharSequence arg0, CharSequence arg1, CharSequence arg2) {
        if(arg0 == null) {
            return arg1 == null ? arg2 : arg1;
        }

        return arg0;
    }

    private void chooseFormat() {
        this.chooseFormat(true);
    }

    private void chooseFormat(boolean arg4) {
        boolean v0 = this.is24HourModeEnabled();
        if(v0) {
            this.mFormat = OpTextClock.abc(this.mFormat24, this.mFormat12, OPUtilsBridge.getLocaleDataHm(getContext()));
            this.mDescFormat = OpTextClock.abc(this.mDescFormat24, this.mDescFormat12, this.mFormat);
        }
        else {
            this.mFormat = OpTextClock.abc(this.mFormat12, this.mFormat24, OPUtilsBridge.getLocaleDatahm(getContext()));
            this.mDescFormat = OpTextClock.abc(this.mDescFormat12, this.mDescFormat24, this.mFormat);
        }

        boolean v0_1 = this.mHasSeconds;
        try {
            Method hasSeconds = DateFormat.class.getMethod("hasSeconds", CharSequence.class);
            this.mHasSeconds = (boolean) hasSeconds.invoke(null, this.mFormat);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void createTime(String arg2) {
        if(arg2 != null) {
            this.mTime = Calendar.getInstance(TimeZone.getTimeZone(arg2), Locale.ENGLISH);
            return;
        }

        this.mTime = Calendar.getInstance();
    }

    private void drawClockDefault(Canvas arg13) {
        Rect v0 = new Rect();
        char[] v2 = new char[2];
        String v3 = DateFormat.format(this.is24HourModeEnabled() ? "HH" : "hh", this.mTime).toString();
        String v4 = DateFormat.format("mm", this.mTime).toString();
        float[] v5 = new float[2];
        this.mHourPaint.getTextWidths(v3, v5);
        float v7 = v5[0];
        int v5_1 = (int)(((float)(arg13.getWidth() / 2)) - v5[0]);
        this.mHourPaint.setTextAlign(Paint.Align.LEFT);
        this.mHourPaint.setColor(v3.charAt(0) == 49 ? this.mDigitColorRed : this.mDigitColorWhite);
        v2[0] = v3.charAt(0);
        this.mHourPaint.getTextBounds(String.valueOf(v2[0]), 0, 1, v0);
        this.mFontBaseLineY = (float)resourceUtils.getDimensionPixelSize(R.dimen.clock_view_default_font_base_line1_y);
        float v5_2 = (float)v5_1;
        arg13.drawText(v2[0] + "", v5_2, this.mFontBaseLineY, this.mHourPaint);
        v2[1] = v3.charAt(1);
        this.mHourPaint.setColor(v3.charAt(1) == 49 ? this.mDigitColorRed : this.mDigitColorWhite);
        arg13.drawText(v2[1] + "", v5_2 + v7, this.mFontBaseLineY, this.mHourPaint);
        this.mFontBaseLineY = (float)resourceUtils.getDimensionPixelSize(R.dimen.clock_view_default_font_base_line2_y);
        this.mHourPaint.setTextAlign(Paint.Align.CENTER);
        this.mHourPaint.setColor(this.mDigitColorWhite);
        arg13.drawText(v4, ((float)(arg13.getWidth() / 2)), this.mFontBaseLineY, this.mHourPaint);
    }

    @SuppressLint("WrongConstant")
    private void init() {
        if(this.mFormat12 == null || this.mFormat24 == null) {
            if(this.mFormat12 == null) {
                this.mFormat12 = OPUtilsBridge.getLocaleDatahm(getContext());
            }

            if(this.mFormat24 == null) {
                this.mFormat24 =OPUtilsBridge.getLocaleDataHm(getContext());
            }
        }

        this.createTime(this.mTimeZone);
        this.chooseFormat(false);
        this.mDigitColorRed = resourceUtils.getColor(R.color.clock_ten_digit_red);
        this.mDigitColorWhite = resourceUtils.getColor(R.color.clock_ten_digit_white);
        ThemeClockPack theme = ThemeManager.INSTANCE.getCurrentColorPack();
        if(theme.isGradient()) {
            ExtensionKt.setGradientTest(this, ThemeManager.INSTANCE.getCurrentColorPack());
        }else{
            mDigitColorWhite = Color.parseColor(theme.getTintColor());
        }
        this.mHourPaint.setAntiAlias(true);
        this.mMinPaint.setAntiAlias(true);
        this.mMinPaint.setColor(this.mDigitColorWhite);
        Typeface v0_1 = null;
        if(OPUtilsBridge.isMCLVersion()) {
            v0_1 = OPUtilsBridge.getMclTypeface(2);
        }

        if(v0_1 == null) {
            v0_1 = Typeface.create("sans-serif", 1);
        }

        this.mHourPaint.setTypeface(v0_1);
        this.mMinPaint.setTypeface(v0_1);
        this.updateTextSize();
    }

    public boolean is24HourModeEnabled() {
        return XPref.INSTANCE.getIs24h();
    }

    @Override  // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setTimeZone(TimeZone.getDefault().getID());
    }

    @Override  // android.view.View
    protected void onDraw(Canvas arg4) {
        super.onDraw(arg4);
        this.mTime.setTimeInMillis(System.currentTimeMillis());
        if(this.mClockStyle == 0) {
            this.drawClockDefault(arg4);
        }
    }

    private void onTimeChanged() {
        float v0_1;
        this.updateTextSize();
        Paint.FontMetrics v0 = this.mHourPaint.getFontMetrics();
        if(this.mClockStyle != 0) {
            v0_1 = v0.descent + Math.abs(v0.ascent);
        }
        else if(OPUtilsBridge.isMCLVersion()) {
            v0_1 = Math.abs(v0.top) * 2f;
        }
        else {
            v0_1 = Math.abs(v0.ascent) * 2f + 2f;
        }

        ViewGroup.LayoutParams v1 = this.getLayoutParams();
        v1.height = (int)v0_1;
        this.setLayoutParams(v1);
        this.setContentDescription(DateFormat.format(this.mDescFormat, this.mTime));
    }

    public void setClockStyle(int arg1) {
        this.mClockStyle = arg1;
    }

    public void setFormat12Hour(CharSequence arg1) {
        this.mFormat12 = arg1;
        this.chooseFormat();
        this.onTimeChanged();
    }

    public void setFormat24Hour(CharSequence arg1) {
        this.mFormat24 = arg1;
        this.chooseFormat();
        this.onTimeChanged();
    }

    public void setShowCurrentUserTime(boolean arg1) {
        this.mShowCurrentUserTime = arg1;
        this.chooseFormat();
        this.onTimeChanged();
    }

    public void setTimeZone(String arg1) {
        this.mTimeZone = arg1;
        this.createTime(arg1);
        this.onTimeChanged();
    }

    private void updateTextSize() {
        this.mHourPaint.setTextSize(resourceUtils.getDimension(R.dimen.clock_view_default_font_size));
        this.mMinPaint.setTextSize(resourceUtils.getDimension(R.dimen.clock_view_default_font_size));
    }
}

