package com.retrox.aodmod.opimports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;

import com.retrox.aodmod.R;
import com.retrox.aodmod.extensions.ResourceUtils;
import com.retrox.aodmod.pref.XPref;
import com.retrox.aodmod.state.AodClockTick;

public class OpClockViewCtrl {
    private OpAnalogClock mAnalogClockView;
    private int mClockStyle;
    private OpTextClock mClockView;
    private final Context mContext;
    private OpCustomTextClock mCustomTextClockView;
    private OpDateTimeView mDateTimeView;
    private String mDisplayText;
    private boolean mDreaming;
    private TextView mOwnerInfo;
    private OpOneRedStyleClock mRedClockView;
    private int mUserId;
    private ResourceUtils resourceUtils;

    public OpClockViewCtrl(Context arg1, ViewGroup arg2) {
        this.resourceUtils = ResourceUtils.getInstance(arg1);
        this.mContext = arg1;
        this.mUserId = 0;
        this.initViews(arg2);
    }

    public int getClockStyle() {
        return this.mClockStyle;
    }

    private String getDisplayText() {
        return this.mDisplayText;
    }

    public void initViews(ViewGroup arg3) {
        this.mDateTimeView = (OpDateTimeView)arg3.findViewById(R.id.date_time_view);
        this.mClockView = (OpTextClock)arg3.findViewById(R.id.clock_view);
        this.mClockView.setShowCurrentUserTime(true);
        this.mAnalogClockView = (OpAnalogClock)arg3.findViewById(R.id.analog_clock_view);
        this.mCustomTextClockView = (OpCustomTextClock)arg3.findViewById(R.id.custom_clock_view); //null
        this.mRedClockView = (OpOneRedStyleClock)arg3.findViewById(R.id.red_clock_view);
        this.mOwnerInfo = new TextView(mContext); //(TextView)arg3.findViewById(R.id.owner_info);
        mOwnerInfo.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        this.updateClockDB();
        this.updateDisplayTextDB();
    }

    public void onDreamingStateChanged(boolean arg1) {
        this.mDreaming = arg1;
    }

    public void onTimeChanged() {
        this.mDateTimeView.refresh();
        this.refreshTime();
    }

    public void onUserInfoChanged(int arg1) {
        this.updateOwnerInfo();
    }

    public void onUserSwitchComplete(int arg2) {
        this.mDateTimeView.refresh();
        this.mUserId = arg2;
        this.updateClockDB();
        this.updateDisplayTextDB();
        this.updateOwnerInfo();
    }

    private void refreshTime() {
        int v0 = this.mClockStyle;
        if(v0 != 0) {
            if(v0 != 40) {
                switch(v0) {
                    case 2: {
                        this.mRedClockView.onTimeChanged();
                        return;
                    }
                    case 3:
                    case 4: {
                        this.mCustomTextClockView.onTimeChanged();
                        return;
                    }
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10: {
                        this.mAnalogClockView.refreshTime();
                        return;
                    }
                }

                return;
            }

            this.mAnalogClockView.refreshTime();
            return;
        }

        this.mClockView.setFormat12Hour(OpDateTimeView.Patterns.clockView12);
        this.mClockView.setFormat24Hour(OpDateTimeView.Patterns.clockView24);
    }

    public void startDozing() {
        this.mDateTimeView.refresh();
        this.refreshTime();
        this.updateOwnerInfo();
    }

    public void updateClockDB() {
        int v0 = XPref.INSTANCE.getOnePlusClockStyle();
        if(!OPUtilsBridge.isMCLVersion() && v0 == 40) {
            Log.d("ClockViewCtrl", "Set clock style failed. Invalid clock style: " + v0);
            return;
        }

        this.mClockStyle = v0;
        this.mAnalogClockView.setClockStyle(this.mClockStyle);
        this.mCustomTextClockView.setClockStyle(this.mClockStyle);
        this.mDateTimeView.setClockStyle(this.mClockStyle);
        this.updateOwnerInfoTypeface();
        OpDateTimeView.Patterns.update(this.mContext, false, this.mClockStyle);
        this.updateLayout();
        this.updateClockVisibility();
        Log.d("ClockViewCtrl", "updateClock: style = " + this.mClockStyle + ", user = " + this.mUserId);
    }

    @SuppressLint("WrongConstant")
    private void updateClockVisibility() {
        Log.d("ClockViewCtrl", "updateClockVisibility: mClockStyle=" + this.mClockStyle);
        int v0 = this.mClockStyle;
        if(v0 == 0) {
            this.mAnalogClockView.setVisibility(8);
            this.mCustomTextClockView.setVisibility(8);
            this.mClockView.setVisibility(0);
            this.mRedClockView.setVisibility(8);
        }
        else if(v0 == 6 || v0 == 7 || v0 == 10 || v0 == 9 || v0 == 8 || v0 == 5) {
            this.mAnalogClockView.setVisibility(0);
            this.mCustomTextClockView.setVisibility(8);
            this.mClockView.setVisibility(8);
            this.mRedClockView.setVisibility(8);
        }
        else if(v0 == 40) {
            this.mAnalogClockView.setVisibility(0);
            this.mCustomTextClockView.setVisibility(8);
            this.mClockView.setVisibility(8);
            this.mRedClockView.setVisibility(8);
        }
        else if(v0 == 1) {
            this.mAnalogClockView.setVisibility(8);
            this.mCustomTextClockView.setVisibility(8);
            this.mClockView.setVisibility(8);
            this.mRedClockView.setVisibility(8);
        }
        else if(v0 == 3 || v0 == 4) {
            this.mCustomTextClockView.setVisibility(0);
            this.mAnalogClockView.setVisibility(8);
            this.mClockView.setVisibility(8);
            this.mRedClockView.setVisibility(8);
        }
        else if(v0 == 2) {
            this.mCustomTextClockView.setVisibility(8);
            this.mAnalogClockView.setVisibility(8);
            this.mClockView.setVisibility(8);
            this.mRedClockView.setVisibility(0);
        }

        this.mClockView.setClockStyle(this.mClockStyle);
        this.mDateTimeView.setClockStyle(this.mClockStyle);
    }

    public void updateDisplayTextDB() {
        this.mDisplayText = Settings.Secure.getString(this.mContext.getContentResolver(), "aod_display_text");
        Log.d("ClockViewCtrl", "updateClock: updateDisplayTextDB = " + this.mDisplayText + ", user = " + this.mUserId);
    }

    public void updateLayout() {
        ViewGroup.MarginLayoutParams v0 = (ViewGroup.MarginLayoutParams)this.mOwnerInfo.getLayoutParams();
        v0.topMargin = this.mClockStyle == 0 ? resourceUtils.getDimensionPixelSize(R.dimen.owner_info_default_marginTop) : OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.owner_info_analog_marginTop));
        int v1 = this.mClockStyle == 4 ? OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.aod_clock_typographic_margin_start_end)) : OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.main_view_horizontal_margin));
        v0.setMarginStart(v1);
        v0.setMarginEnd(v1);
        this.mOwnerInfo.setLayoutParams(v0);
    }

    @SuppressLint("WrongConstant")
    public void updateOwnerInfo() {
        Log.d("ClockViewCtrl", "updateOwnerInfo");
        if(this.mOwnerInfo == null) {
            return;
        }

        if(this.mClockStyle != 1 && this.mClockStyle != 3 && this.mClockStyle != 7) {
            String v0 = this.getDisplayText();
            if(!TextUtils.isEmpty(v0)) {
                this.mOwnerInfo.setVisibility(0);
                this.mOwnerInfo.setText(v0);
                return;
            }

            this.mOwnerInfo.setVisibility(8);
            return;
        }

        this.mOwnerInfo.setVisibility(8);
    }

    private void updateOwnerInfoTypeface() {
        Typeface v0;
        if(this.mClockStyle == 4) {
            v0 = TypefaceCompat.create(mContext, resourceUtils.getFont(R.font.oneplus_aod_font), 400);
        }
        else {
            v0 = OPUtilsBridge.isMCLVersion() ? OPUtilsBridge.getMclTypeface(3) : Typeface.DEFAULT;
        }

        if(v0 != null) {
            this.mOwnerInfo.setTypeface(v0);
        }
    }
}

