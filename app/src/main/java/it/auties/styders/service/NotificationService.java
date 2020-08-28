package it.auties.styders.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import it.auties.styders.background.ToggleBorderOption;
import it.auties.styders.background.WallpaperSetting;

public class NotificationService extends NotificationListenerService {
    private static boolean isNotificationActive = false;
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        SharedPreferences preferences = getBaseContext().getSharedPreferences("Styders", Context.MODE_PRIVATE);
        if (!getOptions(preferences).contains(ToggleBorderOption.NOTIFICATION)) {
            return;
        }

        if (!isNotificationActive) {
            isNotificationActive = true;
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        SharedPreferences preferences = getBaseContext().getSharedPreferences("Styders", Context.MODE_PRIVATE);
        if (!getOptions(preferences).contains(ToggleBorderOption.NOTIFICATION)) {
            return;
        }

        if (isNotificationActive) {
            isNotificationActive = false;
        }
    }

    private Set<ToggleBorderOption> getOptions(SharedPreferences preferences){
        if(preferences.contains(WallpaperSetting.ACTIVATE_CONDITIONS)) {
            Set<ToggleBorderOption> set = new HashSet<>();
            for (String s : Objects.requireNonNull(preferences.getStringSet(WallpaperSetting.ACTIVATE_CONDITIONS, null))) {
                ToggleBorderOption toggleBorderOption = ToggleBorderOption.values()[Integer.parseInt(s)];
                set.add(toggleBorderOption);
            }

            return set;
        }else{
            return new HashSet<>();
        }
    }

    public static boolean IsNotificationActive() {
        return isNotificationActive;
    }
}
