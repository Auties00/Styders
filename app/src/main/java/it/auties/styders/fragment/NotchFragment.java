package it.auties.styders.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import it.auties.styders.R;
import it.auties.styders.background.WallpaperSetting;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.main.MainActivity;
import it.auties.styders.wallpaper.WallPaperService;

public class NotchFragment extends Fragment {
    private final WallpaperSettings settings;

    public NotchFragment() {
        this.settings = MainActivity.getMainActivity().getSettings();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notch, container, false);
        View colorOne = view.findViewById(R.id.notchColorOne);
        colorOne.getLayoutParams().height = colorOne.getMeasuredWidth();
        colorOne.requestLayout();
        bindSwitch(view);
        bindAllSeekBars(view);
        return view;
    }

    private void bindSwitch(View fragment) {
        SwitchCompat switchCompat = fragment.findViewById(R.id.notchSwitch);
        switchCompat.setChecked(settings.isNotch());
        if (!switchCompat.isChecked()) {
            fragment.findViewById(R.id.notchSettingsTwo).setVisibility(View.GONE);
            fragment.findViewById(R.id.notchSettingsFive).setVisibility(View.GONE);
        }

        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            if (fragment.findViewById(R.id.notchSettingsTwo).getVisibility() == View.VISIBLE) {
                fragment.findViewById(R.id.notchSettingsTwo).setVisibility(View.GONE);
                fragment.findViewById(R.id.notchSettingsFive).setVisibility(View.GONE);
                settings.enableNotch(false);
            } else {
                fragment.findViewById(R.id.notchSettingsTwo).setVisibility(View.VISIBLE);
                fragment.findViewById(R.id.notchSettingsFive).setVisibility(View.VISIBLE);
                settings.enableNotch(true);
                bindAllSeekBars(fragment);
            }
        });
    }

    private void bindAllSeekBars(View fragment) {
        bindSeekBar(fragment, R.id.notchSeekBarTwo, WallpaperSetting.NOTCH_HEIGHT);
        bindSeekBar(fragment, R.id.notchSeekBarThree, WallpaperSetting.NOTCH_WIDTH);
        bindSeekBar(fragment, R.id.notchSeekBarFour, WallpaperSetting.NOTCH_BOTTOM_FULL);
        bindSeekBar(fragment, R.id.notchSeekBarSix, WallpaperSetting.NOTCH_TOP);
        bindSeekBar(fragment, R.id.notchSeekBarSeven, WallpaperSetting.NOTCH_BOTTOM);
    }

    private void bindSeekBar(View fragment, int seekBarId, final String preference) {
        SeekBar speedSeekBar = fragment.findViewById(seekBarId);
        switch (preference) {
            case WallpaperSetting.NOTCH_HEIGHT:
                speedSeekBar.setProgress(settings.getNotchHeight());
                break;
            case WallpaperSetting.NOTCH_WIDTH:
                speedSeekBar.setProgress(settings.getNotchWidth());
                break;
            case WallpaperSetting.NOTCH_BOTTOM_FULL:
                speedSeekBar.setProgress(settings.getNotchBottomFull());
                break;
            case WallpaperSetting.NOTCH_TOP:
                speedSeekBar.setProgress(settings.getNotchTop());
                break;
            case WallpaperSetting.NOTCH_BOTTOM:
                speedSeekBar.setProgress(settings.getNotchBottom());
                break;
        }

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (preference) {
                    case WallpaperSetting.NOTCH_HEIGHT:
                        settings.setNotchHeight(i);
                        break;
                    case WallpaperSetting.NOTCH_WIDTH:
                        settings.setNotchWidth(i);
                        break;
                    case WallpaperSetting.NOTCH_BOTTOM_FULL:
                        settings.setNotchBottomFull(i);
                        break;
                    case WallpaperSetting.NOTCH_TOP:
                        settings.setNotchTop(i);
                        break;
                    case WallpaperSetting.NOTCH_BOTTOM:
                        settings.setNotchBottom(i);
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                WallPaperService.setShowHelp(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                WallPaperService.setShowHelp(false);
            }
        });

    }

}
