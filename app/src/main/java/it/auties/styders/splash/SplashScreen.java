package it.auties.styders.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.auties.styders.customization.SelectThemeActivity;
import it.auties.styders.utils.AppUtils;
import it.auties.styders.wallpaper.WallpaperSetActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils utils = new AppUtils(getApplicationContext());

        if (!utils.isFirstLaunch()) {
            Intent intent = new Intent(SplashScreen.this, SelectThemeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashScreen.this, WallpaperSetActivity.class);
            startActivity(intent);
        }
    }
}
