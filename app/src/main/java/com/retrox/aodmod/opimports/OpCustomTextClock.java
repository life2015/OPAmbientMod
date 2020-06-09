package com.retrox.aodmod.opimports;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan.Standard;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;

import com.retrox.aodmod.R;
import com.retrox.aodmod.extensions.ResourceUtils;
import com.retrox.aodmod.pref.XPref;
import com.retrox.aodmod.util.CustomTypefaceSpan;
import com.retrox.aodmod.util.LineHeightSpanStandard;

import java.util.Calendar;
import java.util.TimeZone;

@SuppressLint("AppCompatCustomView")
public class OpCustomTextClock extends TextView {
    private int mColorBottom;
    private int mColorTop;
    private int mGradientEndColor;
    private int mGradientStartColor;
    private int mGradientStyle;
    private String[] mHours = null;
    private String[] mMinutes = null;
    private int mTextClockStringTemplate;
    private int mTextClockStyle = 0;
    private Calendar mTime = null;
    private TimeZone mTimeZone;
    private ResourceUtils resourceUtils;

    public OpCustomTextClock(Context arg2) {
        super(arg2);
        this.resourceUtils = ResourceUtils.getInstance(arg2);
    }

    public OpCustomTextClock(Context arg3, int mGradientStyle, int mGradientStartColor, int mGradientEndColor, int mColorTop, int mColorBottom, int mTextClockStringTemplate) {
        super(arg3);
        this.resourceUtils = ResourceUtils.getInstance(arg3);
        this.mTime = Calendar.getInstance(TimeZone.getDefault());
        this.mGradientStyle = mGradientStyle;
        this.mGradientStartColor = mGradientStartColor;
        this.mGradientEndColor = mGradientEndColor;
        this.mColorTop = mColorTop;
        this.mColorBottom = mColorBottom;
        this.mTextClockStringTemplate = mTextClockStringTemplate;
        this.mHours = resourceUtils.getStringArray(R.array.type_clock_hours);
        this.mMinutes = resourceUtils.getStringArray(R.array.type_clock_minutes);
    }

    private void loadDimensions() {
        Typeface v0_1;
        int v1_1;
        Typeface v0 = resourceUtils.getFont(R.font.oneplus_aod_font);
        int v1 = this.mTextClockStyle;
        if(v1 == 1) {
            v1_1 = R.dimen.aod_clock_typographic_font_size;
            v0_1 = TypefaceCompat.create(getContext(), v0, 100);
        }
        else if(v1 == 0) {
            v1_1 = R.dimen.aod_clock_digital_font_size;
            v0_1 = TypefaceCompat.create(getContext(), v0, 200);
        }
        else {
            v0_1 = null;
            v1_1 = -1;
        }

        this.setTypeface(v0_1);
        this.setLineSpacing(((float)OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.aod_clock_typographic_font_line_space))), 1f);
        this.setTextSize(0, ((float)OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(v1_1))));
    }

    private void loadProperties() {
        if(this.mTextClockStyle == 0) {
            String v1 = OPUtilsBridge.getSystemProperty("sys.aod.gradient.color");
            if(v1 == null) v1 = "";
            if(!TextUtils.isEmpty(v1)) {
                String[] v1_1 = v1.trim().split(",");
                if(v1_1 != null && v1_1.length > 1) {
                    try {
                        this.mGradientStartColor = Color.parseColor("#" + v1_1[0]);
                        this.mGradientEndColor = Color.parseColor("#" + v1_1[1]);
                    }
                    catch(IllegalArgumentException v5) {
                        Log.e("OpCustomTextClock", "parseColor occur exception", v5);
                    }

                    return;
                }
            }
        }
    }

    @Override  // android.widget.TextView
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setTimeZone(TimeZone.getDefault());
    }

    @Override  // android.widget.TextView
    protected void onLayout(boolean arg10, int arg11, int arg12, int arg13, int arg14) {
        int v10_1;
        super.onLayout(arg10, arg11, arg12, arg13, arg14);
        if(arg10) {
            if(this.mTextClockStyle == 0) {
                int v10 = this.mGradientStyle;
                if(v10 != 0) {
                    int v12 = 0;
                    if(v10 == 1) {
                        v10_1 = this.getHeight();
                    }
                    else {
                        if(v10 == 2) {
                            v12 = this.getWidth();
                        }

                        v10_1 = 0;
                    }

                    this.getPaint().setShader(new LinearGradient(0f, 0f, ((float)v12), ((float)v10_1), this.mGradientStartColor, this.mGradientEndColor, Shader.TileMode.CLAMP));
                    return;
                }
            }

            this.getPaint().setShader(null);
        }
    }

    @SuppressLint("WrongConstant")
    public void onTimeChanged() {
        this.mTime.setTimeInMillis(System.currentTimeMillis());
        int v0 = this.mTextClockStyle;
        if(v0 == 1) {
            int v0_1 = this.mTime.get(10) % 12;
            int v2 = this.mTime.get(12) % 60;
            SpannableString v3 = new SpannableString(resourceUtils.getText(this.mTextClockStringTemplate));
            Annotation[] v4 = (Annotation[])v3.getSpans(0, v3.length(), Annotation.class);
            int v5 = v4.length;
            int v7;
            for(v7 = 0; v7 < v5; ++v7) {
                Annotation v8 = v4[v7];
                String v9 = v8.getValue();
                if("color".equals(v9)) {
                    v3.setSpan(new ForegroundColorSpan(this.mColorTop), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33);
                }
                else if("bold".equals(v9)) {
                    v3.setSpan(new CustomTypefaceSpan(TypefaceCompat.create(getContext(), this.getPaint().getTypeface(), 400)), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33);
                    v3.setSpan(new ForegroundColorSpan(this.mColorBottom), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33);
                }
                else if("line-height1".equals(v9)) {
                    Paint.FontMetrics v9_1 = this.getPaint().getFontMetrics();
                    v3.setSpan(new LineHeightSpanStandard(((int)(v9_1.descent - v9_1.ascent + ((float)OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.oneplus_contorl_margin_bottom1))) - (v9_1.ascent - v9_1.top)))), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33);
                }
                else if("line-height2".equals(v9)) {
                    Paint.FontMetrics v9_2 = this.getPaint().getFontMetrics();
                    v3.setSpan(new LineHeightSpanStandard(((int)(v9_2.descent - v9_2.ascent + ((float)OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.aod_clock_typographic_font_line_space))) - (v9_2.ascent - v9_2.top)))), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33);
                }
            }
            this.setTextColor(Color.WHITE);
            this.setText(TextUtils.expandTemplate(v3, new CharSequence[]{this.mHours[v0_1], this.mMinutes[v2]}));
            return;
        }

        if(v0 == 0) {
            this.setText(DateFormat.format(XPref.INSTANCE.getIs24h() ? "HHmm" : "hhmm", this.mTime.getTime()).toString());
        }
    }

    public void setClockStyle(int arg2) {
        if(arg2 == 3) {
            this.mTextClockStyle = 0;
            this.loadProperties();
            this.loadDimensions();
            this.onTimeChanged();
        }
        else if(arg2 == 4) {
            this.mTextClockStyle = 1;
            this.loadProperties();
            this.loadDimensions();
            this.onTimeChanged();
        }
    }

    public void setTimeZone(TimeZone arg2) {
        this.mTimeZone = arg2;
        this.mTime.setTimeZone(arg2);
        this.onTimeChanged();
    }
}

