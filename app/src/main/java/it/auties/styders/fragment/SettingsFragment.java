package it.auties.styders.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import it.auties.styders.R;
import it.auties.styders.background.BackgroundStyle;
import it.auties.styders.background.BorderStyle;
import it.auties.styders.background.RestartOption;
import it.auties.styders.background.StydersStyle;
import it.auties.styders.background.ToggleBorderOption;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.customization.LockscreenMessageActivity;
import it.auties.styders.main.MainActivity;
import it.auties.styders.utils.IntentUtils;
import it.auties.styders.utils.NotificationUtils;
import it.auties.styders.utils.PowerUtils;
import it.auties.styders.wallpaper.WallpaperSetActivity;

public class SettingsFragment extends Fragment {
    private final MainActivity mainActivity;
    private final WallpaperSettings settings;

    public SettingsFragment() {
        this.mainActivity = MainActivity.getMainActivity();
        this.settings = mainActivity.getSettings();
    }

    public SettingsFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.settings = mainActivity.getSettings();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        bindOptions(view);
        setListeners(view);

        return view;
    }

    private void bindOptions(View fragment) {
        switch (settings.getBackgroundStyle()) {
            case BLACK:
                setChecked(fragment, R.id.backgroundStyleOptionOne);
                break;
            case GRAY:
                setChecked(fragment, R.id.backgroundStyleOptionTwo);
                break;
            case WHITE:
                setChecked(fragment, R.id.backgroundStyleOptionThree);
                break;
            case CUSTOM:
                setChecked(fragment, R.id.backgroundStyleOptionFour);
                break;

        }

        switch (settings.getBorderStyle()) {
            case STATIC_LIGHTING:
                setChecked(fragment, R.id.borderStyleOptionOne);
                break;
            case CONTINUOUS_LIGHTING:
                setChecked(fragment, R.id.borderStyleOptionTwo);
                break;
            case BREATH_LIGHTING:
                setChecked(fragment, R.id.borderStyleOptionThree);
                break;
            case DISAPPEARANCE_LIGHTING:
                setChecked(fragment, R.id.borderStyleOptionFour);
                break;
            case CIRCUIT_LIGHTING:
                setChecked(fragment, R.id.borderStyleOptionFive);
                break;
        }

        switch (settings.getRestartOption()) {
            case RESTART:
                setChecked(fragment, R.id.restartStyleOptionOne);
                break;
            case NONE:
                setChecked(fragment, R.id.restartStyleOptionTwo);
                break;
        }

        switch (settings.getStydersStyle()) {
            case DARK:
                setChecked(fragment, R.id.stydersStyleOptionOne);
                break;
            case DARK_GRAY:
                setChecked(fragment, R.id.stydersStyleOptionTwo);
                break;
            case WHITE:
                setChecked(fragment, R.id.stydersStyleOptionThree);
                break;
        }

        for (ToggleBorderOption opt : settings.getActivateLiveBorderOnlyWhen()) {
            switch (opt) {
                case NOTIFICATION:
                    setCheckedBox(fragment, R.id.notificationCheckBox);
                    break;
                case CHARGER:
                    setCheckedBox(fragment, R.id.chargingCheckBox);
                    break;
                case HEADPHONES:
                    setCheckedBox(fragment, R.id.headphonesCheckBox);
                    break;
            }
        }
    }

    private void setChecked(View fragment, int id) {
        MaterialRadioButton radioButton = fragment.findViewById(id);
        radioButton.setChecked(true);
    }

    private void setCheckedBox(View fragment, int id) {
        CheckBox radioButton = fragment.findViewById(id);
        radioButton.setChecked(true);
    }

    private void setListeners(View fragment) {
        RadioGroup radioGroup = fragment.findViewById(R.id.backgroundStyleGroup);
        radioGroup.setOnCheckedChangeListener((rg, i) -> {
            switch (i) {
                case R.id.backgroundStyleOptionOne:
                    settings.setBackgroundStyle(BackgroundStyle.BLACK);
                    settings.setCustomBackground(getBitmapFromColor(Color.BLACK));
                    break;
                case R.id.backgroundStyleOptionTwo:
                    settings.setBackgroundStyle(BackgroundStyle.GRAY);
                    settings.setCustomBackground(getBitmapFromColor(getResources().getColor(R.color.gray_background)));
                    break;
                case R.id.backgroundStyleOptionThree:
                    settings.setBackgroundStyle(BackgroundStyle.WHITE);
                    settings.setCustomBackground(getBitmapFromColor(Color.WHITE));
                    break;
                case R.id.backgroundStyleOptionFour:
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.putExtra("return-data", true);
                    mainActivity.startActivityForResult(Intent.createChooser(intent, "Choose a background..."), 8909);
                    break;
            }
        });

        RadioGroup radioGroup1 = fragment.findViewById(R.id.borderStyleGroup);
        radioGroup1.setOnCheckedChangeListener((rg, i) -> {
            settings.setNewColor(true);
            switch (i) {
                case R.id.borderStyleOptionOne:
                    settings.setLastBorder(settings.getBorderStyle());
                    settings.setBorderStyle(BorderStyle.STATIC_LIGHTING);
                    break;
                case R.id.borderStyleOptionTwo:
                    settings.setBorderStyle(BorderStyle.CONTINUOUS_LIGHTING);
                    break;
                case R.id.borderStyleOptionThree:
                    settings.setBorderStyle(BorderStyle.BREATH_LIGHTING);
                    break;
                case R.id.borderStyleOptionFour:
                    settings.setBorderStyle(BorderStyle.DISAPPEARANCE_LIGHTING);
                    break;
                case R.id.borderStyleOptionFive:
                    settings.setBorderStyle(BorderStyle.CIRCUIT_LIGHTING);
                    break;
            }
        });

        RadioGroup radioGroup2 = fragment.findViewById(R.id.stydersStyleGroup);
        radioGroup2.setOnCheckedChangeListener((radioGroup3, i) -> {
            switch (i) {
                case R.id.stydersStyleOptionOne:
                    settings.setStydersStyle(StydersStyle.DARK);
                    break;
                case R.id.stydersStyleOptionTwo:
                    settings.setStydersStyle(StydersStyle.DARK_GRAY);
                    break;
                case R.id.stydersStyleOptionThree:
                    settings.setStydersStyle(StydersStyle.WHITE);
                    break;
            }
        });

        RadioGroup radioGroup3 = fragment.findViewById(R.id.restartStyleGroup);
        radioGroup3.setOnCheckedChangeListener((radioGroup4, i) -> {
            switch (i) {
                case R.id.restartStyleOptionOne:
                    settings.setRestartOption(RestartOption.RESTART);
                    new PowerUtils().startPowerSaverIntentIfAny(mainActivity);
                    break;
                case R.id.restartStyleOptionTwo:
                    settings.setRestartOption(RestartOption.NONE);
                    break;
            }
        });

        RadioGroup radioGroup4 = fragment.findViewById(R.id.otherOptionsGroup);
        radioGroup4.setOnCheckedChangeListener((t, i) -> {
            switch (i) {
                case R.id.resetBorderButton:
                    setChecked(fragment, R.id.invLastOpt);

                    Intent intent = new Intent(mainActivity, WallpaperSetActivity.class);
                    mainActivity.startActivity(intent);
                    break;
                case R.id.showErrorButton:
                    setChecked(fragment, R.id.invLastOpt);

                    Intent intent1 = new Intent(mainActivity, LockscreenMessageActivity.class);
                    intent1.putExtra("repeat", true);
                    intent1.putExtra("stydersStyle", mainActivity.getSettings().getStydersStyle().name());
                    mainActivity.startActivity(intent1);
                    break;
            }
        });

        CheckBox checkBox = fragment.findViewById(R.id.notificationCheckBox);
        CheckBox checkBox1 = fragment.findViewById(R.id.chargingCheckBox);
        CheckBox checkBox2 = fragment.findViewById(R.id.headphonesCheckBox);
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if(NotificationUtils.shouldAskForPermission(mainActivity)){
                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    if(IntentUtils.isCallable(mainActivity, intent)) {
                        new LovelyStandardDialog(MainActivity.getMainActivity())
                                .setTopColorRes(R.color.blue_dark)
                                .setIcon(R.drawable.ic_info_outline_white_36dp)
                                .setTitle(mainActivity.getString(R.string.listen_to_notification))
                                .setMessage(mainActivity.getString(R.string.listen_description))
                                .setPositiveButton(mainActivity.getString(R.string.settings_msg), view -> {
                                    mainActivity.setPermissionCallback(() -> {
                                        if(NotificationUtils.shouldAskForPermission(mainActivity)){
                                            compoundButton.setChecked(false);
                                        }
                                    });
                                    startActivity(intent);
                                })
                                .setCancelable(false)
                                .show();
                    }else{
                        new LovelyStandardDialog(MainActivity.getMainActivity())
                                .setTopColorRes(R.color.blue_dark)
                                .setIcon(R.drawable.ic_info_outline_white_36dp)
                                .setTitle(mainActivity.getString(R.string.unsupported))
                                .setMessage(mainActivity.getString(R.string.unsupported_notification_desc))
                                .setPositiveButton(mainActivity.getString(R.string.ok), view ->  compoundButton.setChecked(false))
                                .setCancelable(false)
                                .show();
                    }
                }

                settings.addToggleOption(ToggleBorderOption.NOTIFICATION);
            } else {
                settings.removeToggleOption(ToggleBorderOption.NOTIFICATION);
            }
        });
        checkBox1.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                settings.addToggleOption(ToggleBorderOption.CHARGER);
            } else {
                settings.removeToggleOption(ToggleBorderOption.CHARGER);
            }

        });
        checkBox2.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                settings.addToggleOption(ToggleBorderOption.HEADPHONES);
            } else {
                settings.removeToggleOption(ToggleBorderOption.HEADPHONES);
            }
        });
    }

    private Bitmap getBitmapFromColor(int color) {
        Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return bitmap;
    }
}
