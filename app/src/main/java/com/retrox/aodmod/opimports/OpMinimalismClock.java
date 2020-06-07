package com.retrox.aodmod.opimports;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.retrox.aodmod.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OpMinimalismClock extends RelativeLayout {
    private String TAG;
    private ImageView mHour;
    private ImageView mMin;

    public OpMinimalismClock(Context arg1) {
        super(arg1);
        this.TAG = "OpMinimalismClock";
        this.init();
    }

    public OpMinimalismClock(Context arg1, AttributeSet arg2) {
        super(arg1, arg2);
        this.TAG = "OpMinimalismClock";
        this.init();
    }

    public OpMinimalismClock(Context arg1, AttributeSet arg2, int arg3) {
        super(arg1, arg2, arg3);
        this.TAG = "OpMinimalismClock";
        this.init();
    }

    private void init() {
        Log.d(this.TAG, "init");
    }

    @Override  // android.view.ViewGroup
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(this.TAG, "onAttachedToWindow");
        this.refreshTime();
    }

    @Override  // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHour = (ImageView)this.findViewById(R.id.minimalism_hour);
        this.mMin = (ImageView)this.findViewById(R.id.minimalism_min);
    }

    public void refreshTime() {
        String[] v0 = new SimpleDateFormat("hh:mm").format(new Date()).toString().split(":");
        this.mHour.setImageResource(R.drawable.minimalism_hour);
        this.mMin.setImageResource(R.drawable.minimalism_min);
        float v0_1 = (float)Integer.parseInt(v0[1]);
        this.mHour.setRotation(((float)Integer.parseInt(v0[0])) * 360f / 12f + 30f * v0_1 / 60f);
        this.mMin.setRotation(v0_1 * 360f / 60f);
    }
}

