package com.retrox.aodmod.opimports;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.icu.text.DisplayContext;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;

import com.retrox.aodmod.R;
import com.retrox.aodmod.pref.XPref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

@RemoteViews.RemoteView
public class OpTextDate extends View {
    public static final CharSequence DEFAULT_FORMAT_12_HOUR = "h:mm a";
    public static final CharSequence DEFAULT_FORMAT_24_HOUR = "H:mm";
    private int mClockStyle;
    private float mDateFontBaseLineY;
    private Paint mDatePaint;
    private CharSequence mDescFormat;
    private CharSequence mDescFormat12;
    private CharSequence mDescFormat24;
    @ViewDebug.ExportedProperty
    private CharSequence mFormat;
    private CharSequence mFormat12;
    private CharSequence mFormat24;
    @ViewDebug.ExportedProperty
    private boolean mHasSeconds;
    private int mMarginTopAnalog;
    private int mMarginTopAnalogMcl;
    private int mMarginTopDefault;
    private boolean mShowCurrentUserTime;
    private float mTextSize;
    private final Runnable mTicker;
    private Calendar mTime;
    private String mTimeZone;

    public OpTextDate(Context arg1) {
        super(arg1);
        this.mDatePaint = new Paint();
        this.mTicker = new Runnable() {
            @Override
            public void run() {
                OpTextDate.this.onTimeChanged();
                long v0 = SystemClock.uptimeMillis();
                OpTextDate.this.getHandler().postAtTime(OpTextDate.this.mTicker, v0 + (1000L - v0 % 1000L));
            }
        };
        this.init();
    }

    public OpTextDate(Context arg2, AttributeSet arg3) {
        this(arg2, arg3, 0);
    }

    public OpTextDate(Context arg2, AttributeSet arg3, int arg4) {
        this(arg2, arg3, arg4, 0);
    }

    @SuppressLint("ResourceType")
    public OpTextDate(Context arg2, AttributeSet arg3, int arg4, int arg5) {
        super(arg2, arg3, arg4, arg5);
        this.mDatePaint = new Paint();
        this.mTicker = new Runnable() {
            @Override
            public void run() {
                OpTextDate.this.onTimeChanged();
                long v0 = SystemClock.uptimeMillis();
                OpTextDate.this.getHandler().postAtTime(OpTextDate.this.mTicker, v0 + (1000L - v0 % 1000L));
            }
        };
        TypedArray v2 = arg2.obtainStyledAttributes(arg3, android.R.styleable.TextClock, arg4, arg5);
        try {
            this.mFormat12 = v2.getText(0);
            this.mFormat24 = v2.getText(1);
            this.mTimeZone = v2.getString(2);
        }
        catch(Throwable v1) {
            v2.recycle();
            throw v1;
        }

        v2.recycle();
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
            this.mFormat = OpTextDate.abc(this.mFormat24, this.mFormat12, OPUtilsBridge.getLocaleDataHm(getContext()));
            this.mDescFormat = OpTextDate.abc(this.mDescFormat24, this.mDescFormat12, this.mFormat);
        }
        else {
            this.mFormat = OpTextDate.abc(this.mFormat12, this.mFormat24, OPUtilsBridge.getLocaleDatahm(getContext()));
            this.mDescFormat = OpTextDate.abc(this.mDescFormat12, this.mDescFormat24, this.mFormat);
        }

        boolean v0_1 = this.mHasSeconds;
        this.mHasSeconds = DateFormat.hasSeconds(this.mFormat);
        if((arg4) && v0_1 != this.mHasSeconds) {
            if(v0_1) {
                this.getHandler().removeCallbacks(this.mTicker);
                return;
            }

            this.mTicker.run();
        }
    }

    private void createTime(String arg1) {
        if(arg1 != null) {
            this.mTime = Calendar.getInstance(TimeZone.getTimeZone(arg1));
            return;
        }

        this.mTime = Calendar.getInstance();
    }

    private void drawText(Canvas arg10) {
        String v1_1;
        int v0 = arg10.getWidth() / 2;
        Rect v2 = new Rect();
        LinearLayout.LayoutParams v3 = (LinearLayout.LayoutParams)this.getLayoutParams();
        Locale v4 = this.mClockStyle == 4 ? Locale.ENGLISH : Locale.getDefault();
        boolean v6 = v4.toString().contains("zh_");
        int v7 = this.mClockStyle;
        if(v7 == 2) {
            android.icu.text.DateFormat v1 = android.icu.text.DateFormat.getInstanceForSkeleton(this.getContext().getString(R.string.system_ui_aod_date_pattern), v4);
            v1.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
            v1_1 = v1.format(this.mTime.getTime());
        }
        else if(v6) {
            String v1_2 = DateFormat.getBestDateTimePattern(v4, "MMMMd");
            String v6_1 = DateFormat.getBestDateTimePattern(v4, "EEE");
            v1_1 = new SimpleDateFormat(v1_2.toString(), v4).format(this.mTime.getTime()) + " " + new SimpleDateFormat(v6_1.toString(), v4).format(this.mTime.getTime());
        }
        else {
            v1_1 = new SimpleDateFormat(v7 == 3 || v7 == 4 ? DateFormat.getBestDateTimePattern(v4, "EEEE, MMM d") : DateFormat.getBestDateTimePattern(v4, "EEE, MMM d").toString(), v4).format(this.mTime.getTime());
        }

        arg10.drawText(v1_1, ((float)v0), this.mDateFontBaseLineY, this.mDatePaint);
        this.mDatePaint.getTextBounds(v1_1, 0, v1_1.length(), v2);
        v3.width = (int)this.mDatePaint.measureText(v1_1);
        Paint.FontMetrics v10 = this.mDatePaint.getFontMetrics();
        v3.height = (int)Math.ceil(((double)(v10.bottom - v10.top)));
        if(OPUtilsBridge.isMCLVersion()) {
            v3.height += 4;
        }
        else if(this.mClockStyle == 3 || this.mClockStyle == 4) {
            v3.height += 6;
        }

        this.setLayoutParams(v3);
    }

    private void init() {
        if(this.mFormat12 == null || this.mFormat24 == null) {
            if(this.mFormat12 == null) {
                this.mFormat12 = OPUtilsBridge.getLocaleDatahm(getContext());
            }

            if(this.mFormat24 == null) {
                this.mFormat24 = OPUtilsBridge.getLocaleDataHm(getContext());
            }
        }

        this.reloadDimen();
        this.createTime(this.mTimeZone);
        this.chooseFormat(false);
        this.mDatePaint.setAntiAlias(true);
        this.mDatePaint.setLetterSpacing(Float.parseFloat("0.025"));
        this.mDatePaint.setColor(this.getResources().getColor(R.color.date_view_white));
        this.mDatePaint.setTextAlign(Paint.Align.CENTER);
    }

    public boolean is24HourModeEnabled() {
        return XPref.INSTANCE.getIs24h();
    }

    @Override  // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setTimeZone(TimeZone.getDefault().getID());
        this.updateMarginTop();
    }

    @Override  // android.view.View
    protected void onDraw(Canvas arg4) {
        this.mTime.setTimeInMillis(System.currentTimeMillis());

        super.onDraw(arg4);
    }

    public static boolean isVisible(final View view) {
        if (view == null) {
            return false;
        }
        if (!view.isShown()) {
            return false;
        }
        final Rect actualPosition = new Rect();
        view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
        return actualPosition.intersect(screen);
    }

    private void onTimeChanged() {
        this.setContentDescription(DateFormat.format(this.mDescFormat, this.mTime));
        this.invalidate();
    }

    private void reloadDimen() {
        this.mDateFontBaseLineY = this.getResources().getDimension(R.dimen.date_view_font_base_line_y);
        this.mMarginTopDefault = this.getResources().getDimensionPixelSize(R.dimen.date_view_default_marginTop);
        this.mMarginTopAnalog = this.getResources().getDimensionPixelSize(R.dimen.date_view_analog_marginTop);
        this.mMarginTopAnalogMcl = OPUtilsBridge.convertDpToFixedPx(this.getResources().getDimension(R.dimen.date_view_analog_mcl_marginTop));
        this.mTextSize = this.mClockStyle == 2 ? ((float)this.getResources().getDimensionPixelSize(R.dimen.op_owner_info_font_size)) : ((float)this.getResources().getDimensionPixelSize(R.dimen.date_view_font_size));
        this.mDatePaint.setTextSize(this.mTextSize);
        this.resetTypeface();
    }

    private void resetTextSize() {
        this.mTextSize = this.mClockStyle == 2 ? ((float)this.getResources().getDimensionPixelSize(R.dimen.op_owner_info_font_size)) : ((float)this.getResources().getDimensionPixelSize(R.dimen.date_view_font_size));
        this.mDatePaint.setTextSize(this.mTextSize);
    }

    @SuppressLint("WrongConstant")
    private void resetTypeface() {
        Typeface v0_1;
        Locale v0 = Locale.getDefault();
        if(this.mClockStyle == 4 || this.mClockStyle == 3 && (Locale.ENGLISH.getLanguage().equals(v0.getLanguage()))) {
            v0_1 = TypefaceCompat.create(getContext(), ResourcesCompat.getFont(this.getContext(), R.font.oneplus_aod_font), 400);
        }
        else if(OPUtilsBridge.isMCLVersion()) {
            v0_1 = OPUtilsBridge.getMclTypeface(3);
        }
        else {
            v0_1 = null;
        }

        if(v0_1 == null) {
            v0_1 = Typeface.create("sans-serif-medium", 0);
        }

        this.mDatePaint.setTypeface(v0_1);
    }

    public void setClockStyle(int arg2) {
        if(this.mClockStyle != arg2) {
            this.mClockStyle = arg2;
            this.updateMarginTop();
            this.resetTextSize();
            this.resetTypeface();
        }
    }

    @RemotableViewMethod
    public void setFormat12Hour(CharSequence arg1) {
        this.mFormat12 = arg1;
        this.chooseFormat();
        this.onTimeChanged();
    }

    @RemotableViewMethod
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

    @RemotableViewMethod
    public void setTimeZone(String arg1) {
        this.mTimeZone = arg1;
        this.createTime(arg1);
        this.onTimeChanged();
    }

    private void updateMarginTop() {
        int v1_1;
        LinearLayout.LayoutParams v0 = (LinearLayout.LayoutParams)this.getLayoutParams();
        int v1 = this.mClockStyle;
        if(v1 == 0) {
            v1_1 = this.mMarginTopDefault;
        }
        else if(v1 == 6) {
            v1_1 = this.mMarginTopAnalog;
        }
        else {
            v1_1 = v1 == 40 ? this.mMarginTopAnalogMcl : 0;
        }

        v0.topMargin = v1_1;
        if(OPUtilsBridge.isMCLVersion()) {
            v0.topMargin += -4;
        }

        this.setLayoutParams(v0);
    }
}

