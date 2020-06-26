package it.auties.styders.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppUtils {
    private final SharedPreferences preferences;

    public AppUtils(Context context) {
        this.preferences = context.getSharedPreferences("StydersLaunchData", Context.MODE_PRIVATE);
    }

    public boolean isFirstLaunch() {
        return preferences.getBoolean("app", false);
    }

    public void setFirstLaunch() {
        preferences.edit().putBoolean("app", true).apply();
    }
}
