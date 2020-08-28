package it.auties.styders.utils;

import android.content.Context;
import android.provider.Settings;

public class NotificationUtils {
    public static boolean shouldAskForPermission(Context context) {
        String notificationListenerString = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");

        return notificationListenerString == null || !notificationListenerString.contains(context.getPackageName());
    }
}
