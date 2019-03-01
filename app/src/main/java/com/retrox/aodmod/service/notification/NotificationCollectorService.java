package com.retrox.aodmod.service.notification;

import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.Log;
import com.retrox.aodmod.MainHook;
import com.retrox.aodmod.state.AodState;
import de.robv.android.xposed.XposedHelpers;

public class NotificationCollectorService {
    private final String TAG = "NotificationCollectorService";

    private void listenToDreamStateChange() {
        AodState.INSTANCE.getDreamState().observeForever(new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                try {
                    if (AodState.DreamState.ACTIVE.equals(s)) {
                        if (!NotificationCollectorService.this.mRegisted) {
                            // 反编译找出来的  牛批
                            NotificationCollectorService.this.mRegisted = true;
                            XposedHelpers.callMethod(NotificationCollectorService.this.mNotificationListenerService, "registerAsSystemService", NotificationCollectorService.this.mContext, new ComponentName(NotificationCollectorService.this.mContext.getPackageName(), NotificationCollectorService.this.mContext.getClass().getCanonicalName()), -1);
                            MainHook.INSTANCE.logD("绑定通知服务成功", MainHook.TAG);
                        }
                    } else if (AodState.DreamState.STOP.equals(s)) {
                        XposedHelpers.callMethod(NotificationCollectorService.this.mNotificationListenerService, "unregisterAsSystemService");
                        NotificationCollectorService.this.mRegisted = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainHook.INSTANCE.logE("绑定通知服务出现问题", MainHook.TAG, e);
                }

            }
        });
    }


    private Context mContext;
    private Handler mHandler = new Handler();
    private NotificationListenerService mNotificationListenerService = new NotificationListenerService() {
        public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
            Log.d("NotificationCollectorService", "onNotificationPosted");
            if (sbn != null) {
                NotificationManager.INSTANCE.onNotificationPosted(sbn, rankingMap);
                if (!"后台服务图标".equals(sbn.getNotification().getChannelId())) {
//                    sbn.getNotification().getSmallIcon()
//                    MainHook.INSTANCE.logD("Posted 收到通知" + sbn.toString(), MainHook.TAG);
                }

            }
        }

        public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
            Log.d("NotificationCollectorService", "onNotificationRemoved");
            if (sbn != null) {
                NotificationManager.INSTANCE.removeNotification(sbn, rankingMap);
            }
        }

        public void onListenerConnected() {
            Log.d("NotificationCollectorService", "onListenerConnected");
            if (NotificationCollectorService.this.mRegisted) {
                final StatusBarNotification[] notifications = getActiveNotifications();
                if (notifications != null) {
                    final RankingMap currentRanking = getCurrentRanking();
                    NotificationManager.INSTANCE.resetState();
                    NotificationCollectorService.this.mHandler.post(new Runnable() {
                        public void run() {
                            StatusBarNotification[] statusBarNotificationArr = notifications;
                            int length = statusBarNotificationArr.length;
                            int i = 0;
                            while (i < length) {
                                StatusBarNotification sbn = statusBarNotificationArr[i];
                                if (NotificationCollectorService.this.mPm.isInteractive()) {
                                    Log.d("NotificationCollectorService", "stop updating notification, since device is already interactive");
                                    return;
                                } else {
//                                    MainHook.INSTANCE.logD("Connected 收到通知" + sbn.toString(), MainHook.TAG);
                                    NotificationManager.INSTANCE.addNotification(sbn, currentRanking);
                                    i++;
                                }
                            }
                            NotificationManager.INSTANCE.notifyRefresh();
//                            MainHook.INSTANCE.logD("Manger:"  + MainHook.TAG, NotificationManager.INSTANCE.getNotificationMap().toString());
                        }
                    });
                    return;
                }
                return;
            }
            Log.d("NotificationCollectorService", "onListenerConnected called but notification listener service was unregistered");
        }

        public void onListenerDisconnected() {
            Log.d("NotificationCollectorService", "onListenerDisconnected");
        }

        public void onNotificationRankingUpdate(final RankingMap rankingMap) {
            Log.d("NotificationCollectorService", "onNotificationRankingUpdate");
            if (rankingMap != null) {
                NotificationCollectorService.this.mHandler.post(new Runnable() {
                    public void run() {
//             todo           NotificationCollectorService.this.mNotificationManager.onNotificationRankingUpdate(rankingMap);
                    }
                });
            }
        }
    };
    private PowerManager mPm;
    private boolean mRegisted;

    public NotificationCollectorService(Context context) {
        this.mContext = context;
        this.mPm = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        listenToDreamStateChange();
    }
}
