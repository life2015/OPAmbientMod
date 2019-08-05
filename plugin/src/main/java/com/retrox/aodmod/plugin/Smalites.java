package com.retrox.aodmod.plugin;

import android.content.ComponentName;
import android.os.Binder;
import android.os.PowerManager;

public class Smalites {
    public void startDream(Binder token, ComponentName name,
                           boolean isTest, boolean canDoze, int userId, PowerManager.WakeLock wakeLock) {

        name = new ComponentName("com.retrox.aodmod", "com.retrox.aodmod.doze.RetroDream");
    }
}

