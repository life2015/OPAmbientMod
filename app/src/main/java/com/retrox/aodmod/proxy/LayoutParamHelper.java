package com.retrox.aodmod.proxy;

import android.os.Build;
import android.view.WindowManager;
import de.robv.android.xposed.XposedHelpers;

public class LayoutParamHelper {

    public static WindowManager.LayoutParams getAodViewLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = 2303;
        params.layoutInDisplayCutoutMode = 1;
        if (Build.VERSION.SDK_INT >= 27) {
            int privateFlags = 16;
            privateFlags |= 2097152;
            XposedHelpers.setIntField(params, "privateFlags", privateFlags);
        }
        params.flags = 1280;
        params.format = -2;
        params.width = -1;
        params.height = -1;
        params.gravity = 17;
        params.screenOrientation = 1;
        params.setTitle("OPAod");
        params.softInputMode = 3;
        return params;
    }
}
