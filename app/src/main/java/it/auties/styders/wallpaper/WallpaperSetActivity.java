package it.auties.styders.wallpaper;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import it.auties.styders.R;
import it.auties.styders.main.MainActivity;

public class WallpaperSetActivity extends AppCompatActivity {
    private static final int REQUEST_SET_LIVE_WALLPAPER = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SET_LIVE_WALLPAPER) {
            launchHome();
        }
    }

    private void launchHome() {
        Intent intent = new Intent(WallpaperSetActivity.this, MainActivity.class);
        if (getIntent().getBooleanExtra("firstStart", false)) {
            intent.putExtra("stydersStyle", getIntent().getStringExtra("stydersStyle"));
            intent.putExtra("firstStart", true);
        }

        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageManager pm = getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_LIVE_WALLPAPER)) {
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.blue_dark)
                    .setIcon(R.drawable.ic_info_outline_white_36dp)
                    .setTitle(getString(R.string.unsupported))
                    .setMessage(getString(R.string.unsupported_desc))
                    .setCancelable(false)
                    .setPositiveButton(R.string.exit, view -> System.exit(0))
                    .show();
            return;
        }

        WallpaperInfo info = WallpaperManager.getInstance(this).getWallpaperInfo();
        if (info != null && info.getPackageName().equals(this.getPackageName())) {
            launchHome();
            return;
        }

        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(getApplicationContext(), WallPaperService.class));
        intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
        startActivityForResult(intent, REQUEST_SET_LIVE_WALLPAPER);
    }
}
