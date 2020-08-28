package it.auties.styders.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.Arrays;
import java.util.List;

import it.auties.styders.BuildConfig;
import it.auties.styders.R;
import it.auties.styders.main.MainActivity;

public class PowerUtils {
    private static final List<Intent> POWER_MANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity" : "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart")),
            new Intent().setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.SHOW_APPSEC")).addCategory(Intent.CATEGORY_DEFAULT).putExtra("packageName", BuildConfig.APPLICATION_ID)
    );

    private int intentToStart = 0;

    public void startPowerSaverIntentIfAny(Context context) {
        boolean hasIntent = false;
        for (Intent intent : POWER_MANAGER_INTENTS) {
            if (!IntentUtils.isCallable(context, intent)) {
                continue;
            }


            hasIntent = true;
            intentToStart = POWER_MANAGER_INTENTS.indexOf(intent);
            break;
        }


        if (hasIntent) {
            SharedPreferences preferences = context.getSharedPreferences("power_options", Context.MODE_PRIVATE);
            boolean hasAccepted = preferences.getBoolean("accepted", false);
            if (!hasAccepted) {
                String description = context.getString(R.string.boot_manager_description).replace("${oem}", capitalizeFirstLetter(Build.MANUFACTURER.toLowerCase()));
                new LovelyStandardDialog(MainActivity.getMainActivity())
                        .setTopColorRes(R.color.blue_dark)
                        .setIcon(R.drawable.ic_info_outline_white_36dp)
                        .setTitle(context.getString(R.string.boot_manager))
                        .setMessage(description)
                        .setPositiveButton(context.getString(R.string.settings_msg), view -> {
                            preferences.edit().putBoolean("accepted", true).apply();
                            context.startActivity(POWER_MANAGER_INTENTS.get(intentToStart));
                        })
                        .setCancelable(true)
                        .show();
            }
        }
    }

    private String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static boolean isPlugged(Context context) {
        if (context == null) {
            return false;
        }

        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent == null) {
            return false;
        }

        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        return isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }
}
