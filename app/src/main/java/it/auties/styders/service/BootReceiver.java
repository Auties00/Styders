package it.auties.styders.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import it.auties.styders.background.RestartOption;
import it.auties.styders.background.WallpaperSetting;
import it.auties.styders.splash.SplashScreen;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        SharedPreferences preferences = context.getSharedPreferences("Styders", Context.MODE_PRIVATE);
        if (RestartOption.values()[preferences.getInt(WallpaperSetting.RESTART, 1)] == RestartOption.RESTART) {
            Intent start = new Intent(context, SplashScreen.class);
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(start);
        }
    }
}
