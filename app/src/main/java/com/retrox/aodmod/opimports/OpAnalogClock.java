package com.retrox.aodmod.opimports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.SystemProperties;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.retrox.aodmod.R;
import com.retrox.aodmod.extensions.ResourceUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OpAnalogClock extends FrameLayout {
    private static final int[][] ANALOG_RES = new int[][]{new int[]{R.drawable.analog_background, R.drawable.analog_hour, R.drawable.analog_min, 0, 0, R.drawable.analog_sec}, new int[]{R.drawable.analog_my_background, R.drawable.analog_my_hour, R.drawable.analog_my_min, R.drawable.analog_my_dot, R.drawable.analog_my_outer, 0}, new int[]{R.drawable.analog_min_background, R.drawable.analog_min_hour, R.drawable.analog_min_min, 0, 0, R.drawable.analog_min_sec}, new int[]{R.drawable.analog_min2_background, R.drawable.analog_min2_hour, R.drawable.analog_min2_min, R.drawable.analog_min2_dot, 0, 0}, new int[]{R.drawable.analog_geomtry_background, R.drawable.analog_geomtry_hour, R.drawable.analog_geomtry_min, 0, 0, R.drawable.analog_geomtry_sec}, new int[]{R.drawable.analog_roman_background, R.drawable.analog_roman_hour, R.drawable.analog_roman_min, 0, 0, R.drawable.analog_roman_sec}, new int[]{R.drawable.analog_numeral_background, R.drawable.analog_numeral_hour, R.drawable.analog_numeral_min, 0, 0, R.drawable.analog_numeral_sec}};;
    private ImageView mBackground;
    private int mClockSize;
    private TextView mDateView;
    private View mDot;
    private Handler mHandler;
    private View mHour;
    private View mMin;
    private View mOuter;
    private Runnable mRunnable;
    private View mSec;
    private boolean mStartSchedule;
    private int mStyle;
    private ResourceUtils resourceUtils;

    public OpAnalogClock(Context arg1) {
        super(arg1);
        this.resourceUtils = ResourceUtils.getInstance(arg1);
        this.mHandler = new Handler();
        this.mRunnable = () -> {
            if(!OpAnalogClock.this.mStartSchedule) {
                Log.d("OpAnalogClock", "end schedule, do not schedule next");
                return;
            }

            OpAnalogClock.this.refreshTime();
            @SuppressLint("WrongConstant") int v0 = 1000 - Calendar.getInstance().get(14);
            Log.d("OpAnalogClock", "scheduleNext: " + v0);
            OpAnalogClock.this.mHandler.postDelayed(OpAnalogClock.this.mRunnable, ((long)v0));
        };
    }

    public OpAnalogClock(Context arg1, AttributeSet arg2) {
        super(arg1, arg2);
        this.resourceUtils = ResourceUtils.getInstance(arg1);
        this.mHandler = new Handler();
        this.mRunnable = () -> {
            if(!OpAnalogClock.this.mStartSchedule) {
                Log.d("OpAnalogClock", "end schedule, do not schedule next");
                return;
            }

            OpAnalogClock.this.refreshTime();
            @SuppressLint("WrongConstant") int v0 = 1000 - Calendar.getInstance().get(14);
            Log.d("OpAnalogClock", "scheduleNext: " + v0);
            OpAnalogClock.this.mHandler.postDelayed(OpAnalogClock.this.mRunnable, ((long)v0));
        };
    }

    public OpAnalogClock(Context arg1, AttributeSet arg2, int arg3) {
        super(arg1, arg2, arg3);
        this.resourceUtils = ResourceUtils.getInstance(arg1);
        this.mHandler = new Handler();
        this.mRunnable = () -> {
            if(!OpAnalogClock.this.mStartSchedule) {
                Log.d("OpAnalogClock", "end schedule, do not schedule next");
                return;
            }

            OpAnalogClock.this.refreshTime();
            @SuppressLint("WrongConstant") int v0 = 1000 - Calendar.getInstance().get(14);
            Log.d("OpAnalogClock", "scheduleNext: " + v0);
            OpAnalogClock.this.mHandler.postDelayed(OpAnalogClock.this.mRunnable, ((long)v0));
        };
    }

    public void endSchedule() {
        this.mHandler.removeCallbacks(this.mRunnable);
        this.mStartSchedule = false;
    }

    private void loadDimensions() {
        int v0 = this.mStyle;
        if(v0 == 1) {
            this.mClockSize = OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.op_aod_clock_analog_my_size));
            return;
        }

        if(v0 != 0 && v0 != 2) {
            if(v0 == 6) {
                this.mClockSize = OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.aod_clock_analog_numeral_size));
                return;
            }

            this.mClockSize = OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.aod_clock_analog_min2_size));
            return;
        }

        this.mClockSize = OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.clock_analog_size));
    }

    @Override  // android.view.ViewGroup
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.refreshTime();
    }

    @Override  // android.view.ViewGroup
    protected void onDetachedFromWindow() {
        this.endSchedule();
        super.onDetachedFromWindow();
    }

    @Override  // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        Log.d("XAod", "onFinishInflate");
        this.mHour = this.findViewById(R.id.analog_hour);
        this.mMin = this.findViewById(R.id.analog_min);
        this.mBackground = (ImageView)this.findViewById(R.id.analog_background);
        this.mOuter = this.findViewById(R.id.analog_outer);
        this.mDot = this.findViewById(R.id.analog_dot);
        this.mSec = this.findViewById(R.id.analog_sec);
        this.mDateView = (TextView)this.findViewById(R.id.analog_date_view);
        this.loadDimensions();
        this.updateLayout();
    }

    @SuppressLint("WrongConstant")
    public void refreshTime() {
        String v0 = new SimpleDateFormat("hh:mm:ss").format(new Date());
        String[] v1 = v0.toString().split(":");
        Log.d("OpAnalogClock", "refreshTime: " + v0 + " hour = " + Integer.parseInt(v1[0]) + ", min = " + Integer.parseInt(v1[1]) + ", sec = " + Integer.parseInt(v1[2]));
        float v3 = (float)Integer.parseInt(v1[1]);
        float v3_1 = v3 * 360f / 60f;
        this.mHour.setRotation(this.mHour.getRotation());
        this.mMin.setRotation(this.mMin.getRotation());
        this.mOuter.setRotation(this.mOuter.getRotation());
        this.mSec.setRotation(this.mSec.getRotation());
        this.mHour.setRotation(((float)Integer.parseInt(v1[0])) * 360f / 12f + 30f * v3 / 60f);
        this.mMin.setRotation(v3_1);
        this.mOuter.setRotation(v3_1);
        this.mSec.setRotation(((float)Integer.parseInt(v1[2])) * 360f / 60f);
        if(this.mDateView.getVisibility() == 0) {
            Locale v0_1 = Locale.getDefault();
            this.mDateView.setText(v0_1.toString().contains("zh_") ? (CharSequence) new SimpleDateFormat(DateFormat.getBestDateTimePattern(v0_1, "MMMMd").toString(), v0_1) : new SimpleDateFormat("MMM d").format(new Date()));
        }
    }

    // Detected as a lambda impl.
    private void scheduleNext() {
        if(!this.mStartSchedule) {
            Log.d("OpAnalogClock", "end schedule, do not schedule next");
            return;
        }

        this.refreshTime();
        @SuppressLint("WrongConstant") int v0 = 1000 - Calendar.getInstance().get(14);
        Log.d("OpAnalogClock", "scheduleNext: " + v0);
        this.mHandler.postDelayed(this.mRunnable, ((long)v0));
    }

    @SuppressLint("WrongConstant")
    public void setClockStyle(int arg10) {
        if(arg10 == 6) {
            this.mStyle = 0;
        }

        if(arg10 == 40) {
            this.mStyle = 1;
        }

        if(arg10 == 7) {
            this.mStyle = 2;
        }

        if(arg10 == 10) {
            this.mStyle = 3;
        }

        if(arg10 == 9) {
            this.mStyle = 4;
        }

        if(arg10 == 8) {
            this.mStyle = 5;
        }

        if(arg10 == 5) {
            this.mStyle = 6;
        }

        this.loadDimensions();
        this.updateLayout();
        this.mHour.setBackground(resourceUtils.getDrawable(OpAnalogClock.ANALOG_RES[this.mStyle][1]));
        this.mMin.setBackground(resourceUtils.getDrawable(OpAnalogClock.ANALOG_RES[this.mStyle][2]));
        this.mBackground.setBackground(resourceUtils.getDrawable(OpAnalogClock.ANALOG_RES[this.mStyle][0]));
        int[][] v10 = OpAnalogClock.ANALOG_RES;
        int v1 = this.mStyle;
        if(v10[v1][3] == 0) {
            this.mDot.setVisibility(8);
        }
        else {
            this.mDot.setBackground(resourceUtils.getDrawable(v10[v1][3]));
            this.mDot.setVisibility(0);
        }

        if(this.shouldShowSeconds()) {
            this.mSec.setBackground(resourceUtils.getDrawable(OpAnalogClock.ANALOG_RES[this.mStyle][5]));
            this.mSec.setVisibility(0);
        }
        else {
            this.mSec.setVisibility(8);
        }

        this.mOuter.setBackground(resourceUtils.getDrawable(OpAnalogClock.ANALOG_RES[this.mStyle][4]));
        if(this.mStyle == 3) {
            ((ViewGroup.MarginLayoutParams)this.mDateView.getLayoutParams()).leftMargin = OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.aod_clock_min2_date_left));
            this.mDateView.setTextSize(0, ((float)OPUtilsBridge.convertDpToFixedPx(resourceUtils.getDimension(R.dimen.aod_clock_analog_min2_date_size))));
            this.mDateView.setVisibility(0);
        }
        else {
            this.mDateView.setVisibility(8);
        }

        this.refreshTime();
        return;
    }

    private boolean shouldShowSeconds() {
        return this.mStyle == 0 || this.mStyle == 2 || this.mStyle == 4 || this.mStyle == 5 || this.mStyle == 6 ? SystemProperties.getBoolean("sys.aod.show.seconds", false) : false;
    }

    public void startSchedule() {
        if(!this.shouldShowSeconds()) {
            return;
        }

        if(!this.mStartSchedule) {
            this.mStartSchedule = true;
            this.scheduleNext();
            return;
        }

        Log.d("OpAnalogClock", "already start scheduling...");
    }

    private void updateLayout() {
        ViewGroup.LayoutParams v0 = this.getLayoutParams();
        int v1 = this.mClockSize;
        v0.width = v1;
        v0.height = v1;
    }

}

