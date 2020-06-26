package it.auties.styders.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.tabs.TabLayout;

import it.auties.styders.R;
import it.auties.styders.background.WallpaperSetting;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.main.MainActivity;
import it.auties.styders.wallpaper.WallPaperService;

public class Tutorial implements Runnable {
    private RelativeLayout rootView;
    private RelativeLayout wrapper;
    private int targetID;
    private int tutorialID;
    private int textID;
    private LayoutInflater inflater;
    private TabLayout tabLayout;
    private String text;
    private Handler handler;
    private long last;
    private View view;
    private int stage;

    private Tutorial(RelativeLayout rootView, RelativeLayout wrapper, TabLayout tabLayout, int targetID, int tutorialID, int stage, int textID, LayoutInflater inflater, String text) {
        this.rootView = rootView;
        this.targetID = targetID;
        this.inflater = inflater;
        this.wrapper = wrapper;
        this.tabLayout = tabLayout;
        this.text = text;
        this.textID = textID;
        this.handler = new Handler();
        this.tutorialID = tutorialID;
        this.stage = stage;
        this.last = -1;
    }

    @SuppressLint("InflateParams")
    public void showTutorial() {
        Tutorial instance = this;
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for (View v : tabLayout.getTouchables()) v.setClickable(false);

                view = inflater.inflate(R.layout.fragment_home, null);

                view.setAlpha(0.98F);
                view.setElevation(2);

                int[] ids = {R.id.appSettingsOne, R.id.appSettingsTwo, R.id.appSettingsThree, R.id.appSettingsFour, R.id.appSettingsFive};
                for (int id : ids) {
                    if (id == targetID) {
                        continue;
                    }

                    view.findViewById(id).setVisibility(View.INVISIBLE);
                }

                bindAllSeekBars(view);

                AppCompatTextView textView = view.findViewById(textID);
                textView.setText(text);

                view.findViewById(tutorialID).setVisibility(View.VISIBLE);
                rootView.findViewById(targetID).setVisibility(View.INVISIBLE);
                wrapper.addView(view);
                handler.post(instance);
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void bindAllSeekBars(View fragment) {
        bindSeekBar(fragment, R.id.seekBarTwo, WallpaperSetting.BORDER_SPEED);
        bindSeekBar(fragment, R.id.seekBarFive, WallpaperSetting.BORDER_SIZE_HOME);
        bindSeekBar(fragment, R.id.seekBarEight, WallpaperSetting.RADIUS_BOTTOM);
        bindSeekBar(fragment, R.id.seekBarSeven, WallpaperSetting.RADIUS_TOP);
        bindSeekBar(fragment, R.id.seekBarThree, WallpaperSetting.BORDER_BRIGHTNESS);
    }

    private void bindSeekBar(View fragment, int seekBarId, final String preference) {
        SeekBar speedSeekBar = fragment.findViewById(seekBarId);
        WallpaperSettings settings = MainActivity.getMainActivity().getSettings();
        switch (preference) {
            case WallpaperSetting.BORDER_SPEED:
                speedSeekBar.setProgress(settings.getBorderSpeed());
                break;
            case WallpaperSetting.BORDER_SIZE_HOME:
                speedSeekBar.setProgress(settings.getBorderSizeHomeScreen());
                break;
            case WallpaperSetting.RADIUS_BOTTOM:
                speedSeekBar.setProgress(settings.getRadiusBottom());
                break;
            case WallpaperSetting.RADIUS_TOP:
                speedSeekBar.setProgress(settings.getRadiusTop());
                break;
            case WallpaperSetting.BORDER_BRIGHTNESS:
                speedSeekBar.setProgress(settings.getImageBorderBrightness());
                break;
        }

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (preference) {
                    case WallpaperSetting.BORDER_SPEED:
                        settings.setBorderSpeed(i);
                        break;
                    case WallpaperSetting.BORDER_SIZE_HOME:
                        settings.setBorderSizeHomeScreen(i);
                        break;
                    case WallpaperSetting.RADIUS_BOTTOM:
                        settings.setRadiusBottom(i);
                        break;
                    case WallpaperSetting.RADIUS_TOP:
                        settings.setRadiusTop(i);
                        break;
                    case WallpaperSetting.BORDER_BRIGHTNESS:
                        settings.setImageBorderBrightness(i);
                        break;
                }

                last = System.currentTimeMillis();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (preference.equals(WallpaperSetting.RADIUS_TOP) || preference.equals(WallpaperSetting.RADIUS_BOTTOM)) {
                    WallPaperService.setShowHelp(true);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (preference.equals(WallpaperSetting.RADIUS_TOP) || preference.equals(WallpaperSetting.RADIUS_BOTTOM)) {
                    WallPaperService.setShowHelp(false);
                }

            }
        });
    }


    @Override
    public void run() {
        if (last != -1 && System.currentTimeMillis() - last >= 2500L) {
            wrapper.removeView(view);
            rootView.findViewById(targetID).setVisibility(View.VISIBLE);
            view.findViewById(targetID).setVisibility(View.INVISIBLE);

            switch (stage) {
                case 0:
                    Tutorial tutorial = new Tutorial.Builder()
                            .setRootView(rootView)
                            .setInflater(inflater)
                            .setTargetID(R.id.appSettingsFive)
                            .setWrapper(wrapper)
                            .setTabLayout(tabLayout)
                            .setText(MainActivity.getMainActivity().getText(R.string.tutorial_two).toString())
                            .setTutorialID(R.id.tutorialTwo)
                            .setStage(1)
                            .setTextID(R.id.explainTwo)
                            .build();
                    tutorial.showTutorial();
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }

            return;
        }

        handler.post(this);
    }


    public static class Builder {
        private RelativeLayout rootView;
        private RelativeLayout wrapper;
        private int targetID;
        private int tutorialID;
        private LayoutInflater inflater;
        private TabLayout tabLayout;
        private String text;
        private int stage;
        private int textID;

        public Builder setRootView(RelativeLayout rootView) {
            this.rootView = rootView;
            return this;
        }

        public Builder setWrapper(RelativeLayout wrapper) {
            this.wrapper = wrapper;
            return this;
        }

        public Builder setTargetID(int targetID) {
            this.targetID = targetID;
            return this;
        }

        public Builder setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
            return this;
        }

        public Builder setTabLayout(TabLayout tabLayout) {
            this.tabLayout = tabLayout;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setTutorialID(int tutorialID) {
            this.tutorialID = tutorialID;
            return this;
        }

        public Builder setStage(int stage) {
            this.stage = stage;
            return this;
        }

        public Builder setTextID(int textID) {
            this.textID = textID;
            return this;
        }

        public Tutorial build() {
            return new Tutorial(rootView, wrapper, tabLayout, targetID, tutorialID, stage, textID, inflater, text);
        }
    }
}
