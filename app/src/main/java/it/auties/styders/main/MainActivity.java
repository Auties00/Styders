package it.auties.styders.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import it.auties.styders.R;
import it.auties.styders.background.AppState;
import it.auties.styders.background.BackgroundStyle;
import it.auties.styders.background.StydersStyle;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.utils.AppUtils;
import it.auties.styders.utils.BlockedViewPager;
import it.auties.styders.utils.ClosableTabLayout;
import it.auties.styders.utils.PageAdapter;
import it.auties.styders.utils.Tutorial;

public class MainActivity extends AppCompatActivity {
    private static MainActivity mainActivity;
    private BlockedViewPager viewPager;
    private LifecycleObserver observer;
    private WallpaperSettings settings;
    private boolean started;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = this;

        this.settings = WallpaperSettings.getInstance(getFilesDir());

        boolean firstRun = getIntent().getBooleanExtra("firstStart", false);
        if (firstRun) {
            settings.setStydersStyle(StydersStyle.valueOf(getIntent().getStringExtra("stydersStyle")));

            switch (StydersStyle.valueOf(getIntent().getStringExtra("stydersStyle"))) {
                case DARK:
                    settings.setBackgroundStyle(BackgroundStyle.BLACK);
                    settings.setCustomBackground(getBitmapFromColor(Color.BLACK));
                    break;
                case DARK_GRAY:
                    settings.setBackgroundStyle(BackgroundStyle.GRAY);
                    settings.setBackgroundStyle(BackgroundStyle.GRAY);
                    settings.setCustomBackground(getBitmapFromColor(getResources().getColor(R.color.gray_background)));
                    break;
                case WHITE:
                    settings.setBackgroundStyle(BackgroundStyle.WHITE);
                    settings.setCustomBackground(getBitmapFromColor(Color.WHITE));
                    break;
            }

            AppUtils utils = new AppUtils(getApplicationContext());
            utils.setFirstLaunch();
            try {
                settings.serialize(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (savedInstanceState != null && savedInstanceState.containsKey("newTheme")) {
            setTheme(StydersStyle.valueOf(savedInstanceState.getString("newTheme")).getTheme());
        } else {
            try {
                setTheme(settings.getStydersStyle().getTheme());
            } catch (NullPointerException e) {
                settings.setStydersStyle(StydersStyle.DARK_GRAY);
                setTheme(settings.getStydersStyle().getTheme());
            }
        }

        setContentView(R.layout.activity_main);
        ClosableTabLayout tabLayout = findViewById(R.id.tabLayout);
        this.viewPager = findViewById(R.id.viewPager);

        PageAdapter pageAdapter = new PageAdapter(this, getSupportFragmentManager(), 4);
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(4);
        viewPager.setPagingEnabled(false);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{-android.R.attr.state_selected}
        };

        boolean dark = settings.getStydersStyle() == StydersStyle.DARK_GRAY || settings.getStydersStyle() == StydersStyle.DARK;
        int[] colors = new int[]{
                (dark ? Color.WHITE : Color.BLACK),
                getResources().getColor(R.color.gray_seek_bar_background)
        };

        ColorStateList color = new ColorStateList(states, colors);
        tabLayout.setTabTextColors(color);

        int[] id = {R.drawable.ic_home, R.drawable.ic_notch, R.drawable.ic_timer, R.drawable.ic_settings};
        String[] names = {getResources().getString(R.string.home), getResources().getString(R.string.notch), getResources().getString(R.string.timer), getResources().getString(R.string.settings)};
        for (int x = 0; x < tabLayout.getTabCount(); x++) {
            TabLayout.Tab tab = tabLayout.getTabAt(x);

            if (tab != null) {
                tab.setCustomView(R.layout.tab_icon);

                assert tab.getCustomView() != null : "Tab view is null!";

                AppCompatImageView image = tab.getCustomView().findViewById(R.id.tabIconView);
                image.setImageIcon(Icon.createWithResource(this, id[x]));
                image.setImageTintList(color);

                AppCompatTextView textView = tab.getCustomView().findViewById(R.id.tabTextView);
                textView.setTextColor(color);

                textView.setText(Html.fromHtml("<b>" + names[x] + "</b>"));
            }
        }

        this.observer = (LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_RESUME) {
                settings.setAppState(AppState.IN);
                settings.setNewImage(true);
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                settings.setAppState(AppState.OUT);
                settings.setNewImage(true);
            }
        };

        getLifecycle().addObserver(observer);

        hideNavBar();

        File errors = new File(getFilesDir(), "errors.txt");
        if (!errors.exists()) {
            try {
                if (errors.createNewFile()) {
                    Log.d("[FileIO]", "Couldn't create a new file!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("[FILE]", errors.getPath());
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(errors))) {
                writer.write("[EX]Caught an exception on thread with id: " + thread.getId() + " and name: " + thread.getName());
                writer.newLine();
                writer.write("Message: " + e.getMessage());
                writer.newLine();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        new Handler().postDelayed(() -> Toast.makeText(this, "Path: " + errors.getPath(), Toast.LENGTH_LONG).show(), 1000L);

        this.started = true;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    private Bitmap getBitmapFromColor(int color) {
        Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8909) {
            if (resultCode != RESULT_OK) {
                goBack();
                return;
            }

            Bitmap newBack;
            try {
                newBack = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                Toast.makeText(this, "The image wasn't found!", Toast.LENGTH_LONG).show();
                goBack();
                return;
            }

            settings.setBackgroundStyle(BackgroundStyle.CUSTOM);
            settings.setCustomBackground(newBack);
        }
    }

    private void goBack() {
        int id = 0;
        switch (settings.getBackgroundStyle()) {
            case WHITE:
                id = R.id.backgroundStyleOptionThree;
                break;
            case BLACK:
                id = R.id.backgroundStyleOptionOne;
                break;
            case GRAY:
                id = R.id.backgroundStyleOptionTwo;
                break;
        }

        RadioButton button = findViewById(id);
        if (button != null) {
            button.setChecked(true);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(observer);

        try {
            settings.serialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("newTheme", settings.getStydersStyle().name());
    }

    public void refreshUI() {
        try {
            settings.serialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();
        overridePendingTransition(0, 0);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    @Override
    public void onBackPressed() {
        if (started) {
            super.onBackPressed();
        }
    }

    public WallpaperSettings getSettings() {
        return settings;
    }

    @Override
    protected void onResume() {
        hideNavBar();
        super.onResume();
    }


    private void hideNavBar() {
        final View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY));
    }
}
