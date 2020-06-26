package it.auties.styders.customization;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import it.auties.styders.R;
import it.auties.styders.background.StydersStyle;
import it.auties.styders.main.MainActivity;
import it.auties.styders.wallpaper.WallpaperSetActivity;

public class LockscreenMessageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StydersStyle stydersStyle = StydersStyle.valueOf(getIntent().getStringExtra("stydersStyle"));
        switch (stydersStyle) {
            case WHITE:
                getWindow().setStatusBarColor(Color.WHITE);
                getWindow().setNavigationBarColor(Color.WHITE);
                break;
            case DARK:
                getWindow().setStatusBarColor(Color.BLACK);
                getWindow().setNavigationBarColor(Color.BLACK);
                break;

            case DARK_GRAY:
                getWindow().setStatusBarColor(getResources().getColor(R.color.gray_background));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.gray_background));
                break;
        }

        AppCompatButton button = null;
        switch (stydersStyle) {
            case WHITE:
                setContentView(R.layout.lockscreen_white_message);
                button = findViewById(R.id.whiteBtn);
                break;
            case DARK:
                setContentView(R.layout.lockscreen_black_message);
                button = findViewById(R.id.darkBtn);
                break;
            case DARK_GRAY:
                setContentView(R.layout.lockscreen_gray_message);
                button = findViewById(R.id.grayBtn);
                break;
        }


        button.setOnClickListener(v -> launchHomeScreen(stydersStyle));
    }

    private void launchHomeScreen(StydersStyle stydersStyle) {
        Intent intent;
        if (!getIntent().getBooleanExtra("repeat", false)) {
            intent = new Intent(LockscreenMessageActivity.this, WallpaperSetActivity.class);
            intent.putExtra("stydersStyle", stydersStyle.name());
            intent.putExtra("firstStart", true);
        } else {
            intent = new Intent(LockscreenMessageActivity.this, MainActivity.class);
        }


        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
