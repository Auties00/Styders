package it.auties.styders.service;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import it.auties.styders.background.ShowState;
import it.auties.styders.background.ToggleBorderOption;
import it.auties.styders.background.WallpaperSettings;

public class NotificationService extends NotificationListenerService {
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
        WallpaperSettings settings = WallpaperSettings.getInstance(getApplicationContext().getFilesDir());
        if (!settings.getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.NOTIFICATION)) {
            return;
        }

        if (settings.getShowState() != ShowState.APPEAR) {
            settings.setShowState(ShowState.APPEAR);
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        WallpaperSettings settings = WallpaperSettings.getInstance(getApplicationContext().getFilesDir());
        if (!settings.getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.NOTIFICATION)) {
            return;
        }

        if (settings.getShowState() != ShowState.HIDDEN) {
            settings.setShowState(ShowState.HIDDEN);
        }
    }
}
