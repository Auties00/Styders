package it.auties.styders.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it.auties.styders.background.RestartOption;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.splash.SplashScreen;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        if (WallpaperSettings.getInstance(context.getApplicationContext().getFilesDir()).getRestartOption() == RestartOption.RESTART) {
            Intent start = new Intent(context, SplashScreen.class);
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(start);
        }
    }
}
