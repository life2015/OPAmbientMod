package com.retrox.aodmod.opimports;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.GridLayout;

import com.retrox.aodmod.R;
import com.retrox.aodmod.extensions.ResourceUtils;

import java.util.Locale;

public class OpDateTimeView extends GridLayout {
    public static final class Patterns {
        static String clockView12;
        static String clockView24;
        static String dateView;

        static void update(Context arg4, boolean arg5, int arg6) {
            Locale v0 = Locale.getDefault();
            ResourceUtils v1 = ResourceUtils.getInstance(arg4);
            String v5 = v1.getString(arg5 ? R.string.abbrev_wday_month_day_no_year_alarm : R.string.abbrev_wday_month_day_no_year);
            String v2 = v1.getString(R.string.clock_12hr_format);
            String v1_1 = v1.getString(R.string.clock_24hr_format);
            Patterns.dateView = DateFormat.getBestDateTimePattern(v0, v5);
            Patterns.clockView12 = "hh:mm";
            if(!v1.getResources().getBoolean(R.bool.aod_config_showAmpm) && !v2.contains("a")) {
                Patterns.clockView12 = Patterns.clockView12.replaceAll("a", "").trim();
            }

            Patterns.clockView24 = DateFormat.getBestDateTimePattern(v0, v1_1);
            Log.d("DateTimeView", "updateClockPattern: " + arg6);
            if(arg6 == 0) {
                Patterns.clockView24 = Patterns.clockView24.replace(':', ' ');
                Patterns.clockView12 = Patterns.clockView12.replace(':', ' ');
            }

            Log.d("DateTimeView", "update clockView12: " + Patterns.clockView12 + " clockView24:" + Patterns.clockView24);
        }
    }

    private int mClockStyle;
    private Context mContext;

    public OpDateTimeView(Context arg3) {
        this(arg3, null, 0);
    }

    public OpDateTimeView(Context arg2, AttributeSet arg3) {
        this(arg2, arg3, 0);
    }

    public OpDateTimeView(Context arg1, AttributeSet arg2, int arg3) {
        super(arg1, arg2, arg3);
        this.mContext = arg1;
    }

    @Override  // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override  // android.view.ViewGroup
    protected void onAttachedToWindow() {
        int v7;
        int v2_1;
        super.onAttachedToWindow();
        Log.d("DateTimeView", "onAttachedToWindow");
        ResourceUtils v0 = ResourceUtils.getInstance(getContext());
        ViewGroup.MarginLayoutParams v1 = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
        int v2 = this.mClockStyle;
        if(v2 == 0) {
            v2_1 = v0.getDimensionPixelSize(R.dimen.date_time_view_default_marginTop);
        }
        else if(v2 == 6 || v2 == 7) {
            v2_1 = OPUtilsBridge.convertDpToFixedPx(v0.getDimension(R.dimen.date_time_view_analog_marginTop));
        }
        else if(v2 == 3) {
            v2_1 = OPUtilsBridge.convertDpToFixedPx(v0.getDimension(R.dimen.aod_clock_digital_margin_top));
        }
        else if(v2 == 4) {
            v2_1 = OPUtilsBridge.convertDpToFixedPx(v0.getDimension(R.dimen.aod_clock_typographic_margin_top));
        }
        else if(v2 == 10 || v2 == 9 || v2 == 8 || v2 == 5) {
            v2_1 = OPUtilsBridge.convertDpToFixedPx(v0.getDimension(R.dimen.aod_clock_analog_min2_top));
        }
        else if(v2 == 2) {
            v2_1 = OPUtilsBridge.convertDpToFixedPx(v0.getDimension(R.dimen.aod_clock_digital2_margin_top));
        }
        else {
            v2_1 = 0;
        }

        if(OPUtilsBridge.getDeviceTag().equals("17819")) {
            v7 = v0.getDimensionPixelSize(R.dimen.date_time_view_17819_additional_marginTop);
        }
        else if(OPUtilsBridge.getDeviceTag().equals("17801")) {
            v7 = v0.getDimensionPixelSize(R.dimen.date_time_view_17801_additional_marginTop);
        }
        else {
            v7 = this.mClockStyle == 0 ? v0.getDimensionPixelSize(R.dimen.date_time_view_additional_marginTop) : 0;
        }

        //v1.topMargin = this.mClockStyle == 40 ? OPUtilsBridge.convertDpToFixedPx(v0.getDimension(R.dimen.op_aod_clock_analog_my_margin_top)) : v2_1 + v7;
        this.setLayoutParams(v1);
    }

    @Override  // android.view.View
    protected void onConfigurationChanged(Configuration arg1) {
        super.onConfigurationChanged(arg1);
    }

    @Override  // android.view.ViewGroup
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d("DateTimeView", "onDetachedFromWindow");
    }

    @Override  // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("DateTimeView", "onFinishInflate: ");
    }

    public void refresh() {
        Patterns.update(this.mContext, false, this.mClockStyle);
        this.refreshTime();
    }

    public void refreshTime() {
        Log.d("DateTimeView", "refreshTime");
    }

    public void setClockStyle(int arg1) {
        this.mClockStyle = arg1;
        this.refresh();
    }
}

