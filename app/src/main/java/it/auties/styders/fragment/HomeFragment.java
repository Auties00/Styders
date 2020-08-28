package it.auties.styders.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import it.auties.styders.R;
import it.auties.styders.background.StydersStyle;
import it.auties.styders.background.WallpaperSetting;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.main.MainActivity;
import it.auties.styders.utils.ColorSequencesView;
import it.auties.styders.wallpaper.WallPaperService;
import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog;

public class HomeFragment extends Fragment {
    private final MainActivity mainActivity;
    private final WallpaperSettings settings;

    public HomeFragment() {
        this.mainActivity = MainActivity.getMainActivity();
        this.settings = mainActivity.getSettings();
    }

    public HomeFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.settings = mainActivity.getSettings();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bindAllSeekBars(view);
        bindSwitch(view);
        bindColorIcons(view);
        bindColorSequences(view);

        return view;
    }

    private void bindColorSequences(View fragment) {
        fragment.findViewById(R.id.savedColors).setOnClickListener(view -> {
            ColorSequencesView colorSequencesView = new ColorSequencesView();
            colorSequencesView.show(mainActivity.getSupportFragmentManager(), "SavedColorSequences");
        });
    }

    private void bindSwitch(View fragment) {
        SwitchCompat switchCompat = fragment.findViewById(R.id.borderSwitch);
        switchCompat.setChecked(settings.isBorderEnabled());
        if (!switchCompat.isChecked()) {
            fragment.findViewById(R.id.appSettingsTwo).setVisibility(View.GONE);
            fragment.findViewById(R.id.appSettingsThree).setVisibility(View.GONE);
            fragment.findViewById(R.id.appSettingsFour).setVisibility(View.GONE);
            fragment.findViewById(R.id.appSettingsFive).setVisibility(View.GONE);
        }

        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            if (settings.isBorderEnabled()) {
                fragment.findViewById(R.id.appSettingsTwo).setVisibility(View.GONE);
                fragment.findViewById(R.id.appSettingsThree).setVisibility(View.GONE);
                fragment.findViewById(R.id.appSettingsFour).setVisibility(View.GONE);
                fragment.findViewById(R.id.appSettingsFive).setVisibility(View.GONE);
                settings.setBorderEnabled(false);
            } else {
                fragment.findViewById(R.id.appSettingsTwo).setVisibility(View.VISIBLE);
                fragment.findViewById(R.id.appSettingsThree).setVisibility(View.VISIBLE);
                fragment.findViewById(R.id.appSettingsFour).setVisibility(View.VISIBLE);
                fragment.findViewById(R.id.appSettingsFive).setVisibility(View.VISIBLE);
                settings.setBorderEnabled(true);

            }
        });
    }

    private void bindAllSeekBars(View fragment) {
        bindSeekBar(fragment, R.id.seekBarTwo, WallpaperSetting.BORDER_SPEED);
        bindSeekBar(fragment, R.id.seekBarFive, WallpaperSetting.BORDER_HOME);
        bindSeekBar(fragment, R.id.seekBarEight, WallpaperSetting.RADIUS_BOTTOM);
        bindSeekBar(fragment, R.id.seekBarSeven, WallpaperSetting.RADIUS_TOP);
        bindSeekBar(fragment, R.id.seekBarThree, WallpaperSetting.IMAGE_BRIGHTNESS);
    }

    private void bindSeekBar(View fragment, int seekBarId, final String preference) {
        SeekBar speedSeekBar = fragment.findViewById(seekBarId);
        switch (preference) {
            case WallpaperSetting.BORDER_SPEED:
                speedSeekBar.setProgress(settings.getBorderSpeed());
                break;
            case WallpaperSetting.BORDER_HOME:
                speedSeekBar.setProgress(settings.getBorderSizeHomeScreen());
                break;
            case WallpaperSetting.RADIUS_BOTTOM:
                speedSeekBar.setProgress(settings.getRadiusBottom());
                break;
            case WallpaperSetting.RADIUS_TOP:
                speedSeekBar.setProgress(settings.getRadiusTop());
                break;
            case WallpaperSetting.IMAGE_BRIGHTNESS:
                speedSeekBar.setProgress(settings.getImageBorderBrightness());
                break;
        }

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (preference) {
                    case WallpaperSetting.BORDER_SPEED:
                        settings.setBorderSpeed(i);
                        break;
                    case WallpaperSetting.BORDER_HOME:
                        settings.setBorderSizeHomeScreen(i);
                        break;
                    case WallpaperSetting.RADIUS_BOTTOM:
                        settings.setRadiusBottom(i);
                        break;
                    case WallpaperSetting.RADIUS_TOP:
                        settings.setRadiusTop(i);
                        break;
                    case WallpaperSetting.IMAGE_BRIGHTNESS:
                        settings.setImageBorderBrightness(i);
                        break;
                }
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

    private void bindColorIcons(View fragment) {
        for (int x = 0; x < 6; x++) {
            switch (x) {
                case 0:
                    bindColor(fragment.findViewById(R.id.colorOne), x);
                    break;
                case 1:
                    bindColor(fragment.findViewById(R.id.colorTwo), x);
                    break;
                case 2:
                    bindColor(fragment.findViewById(R.id.colorThree), x);
                    break;
                case 3:
                    bindColor(fragment.findViewById(R.id.colorFour), x);
                    break;
                case 4:
                    bindColor(fragment.findViewById(R.id.colorFive), x);
                    break;
                case 5:
                    bindColor(fragment.findViewById(R.id.colorSix), x);
                    break;
            }
        }
    }

    private void bindColor(CardView cardView, int x) {
        int targetColor = settings.getColorSequences().getSequenceInUse().getColors()[x];
        cardView.setCardBackgroundColor(targetColor);
        cardView.setOnClickListener(view -> {
            ColorPickerDialog dialog = new ColorPickerDialog()
                    .withColor(targetColor)
                    .withPresets(Color.BLUE, Color.YELLOW, Color.RED, Color.MAGENTA, Color.GREEN, Color.BLACK, Color.GRAY, Color.LTGRAY, Color.WHITE)
                    .withListener((pickerView, color) -> {
                        cardView.setCardBackgroundColor(color);
                        settings.getColorSequences().getSequenceInUse().getColors()[x] = color;
                        settings.getColorSequences().getSequenceInUse().setUpdate(true);
                        settings.setNewColor(true);
                    })
                    .withTheme(settings.getStydersStyle() != StydersStyle.WHITE ? R.style.ColorPickerDialogDark : R.style.ColorPickerDialog);
            if (dialog.getView() != null) {
                Button button = dialog.getView().findViewById(me.jfenn.colorpickerdialog.R.id.cancel);
                button.setText("CANCEL");
            }

            dialog.show(mainActivity.getSupportFragmentManager(), "ColorPicker");
        });
    }
}
