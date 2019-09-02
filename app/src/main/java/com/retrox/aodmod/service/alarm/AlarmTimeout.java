package com.retrox.aodmod.service.alarm;


import android.app.AlarmManager;
import android.os.Handler;
import android.os.SystemClock;

/**
 * Schedules a timeout through AlarmManager. Ensures that the timeout is called even when
 * the device is asleep.
 */
public class AlarmTimeout implements AlarmManager.OnAlarmListener {

    public static final int MODE_CRASH_IF_SCHEDULED = 0;
    public static final int MODE_IGNORE_IF_SCHEDULED = 1;
    public static final int MODE_RESCHEDULE_IF_SCHEDULED = 2;

    private final AlarmManager mAlarmManager;
    private final AlarmManager.OnAlarmListener mListener;
    private final String mTag;
    private final Handler mHandler;
    private boolean mScheduled;

    public AlarmTimeout(AlarmManager alarmManager, AlarmManager.OnAlarmListener listener,
                        String tag, Handler handler) {
        mAlarmManager = alarmManager;
        mListener = listener;
        mTag = tag;
        mHandler = handler;
    }

    public void schedule(long timeout, int mode) {
        switch (mode) {
            case MODE_CRASH_IF_SCHEDULED:
                if (mScheduled) {
                    throw new IllegalStateException(mTag + " timeout is already scheduled");
                }
                break;
            case MODE_IGNORE_IF_SCHEDULED:
                if (mScheduled) {
                    return;
                }
                break;
            case MODE_RESCHEDULE_IF_SCHEDULED:
                if (mScheduled) {
                    cancel();
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal mode: " + mode);
        }

        mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + timeout, mTag, this, mHandler);
        mScheduled = true;
    }

    public boolean isScheduled() {
        return mScheduled;
    }

    public void cancel() {
        if (mScheduled) {
            mAlarmManager.cancel(this);
            mScheduled = false;
        }
    }

    @Override
    public void onAlarm() {
        if (!mScheduled) {
            // We canceled the alarm, but it still fired. Ignore.
            return;
        }
        mScheduled = false;
        mListener.onAlarm();
    }
}

