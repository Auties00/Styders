package it.auties.styders.utils;

import android.content.Context;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import it.auties.styders.R;
import it.auties.styders.background.TimerQuickOptions;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.fragment.TimerFragment;
import it.auties.styders.main.MainActivity;

public class TimePickerView extends BottomSheetDialog {
    private final MainActivity mainActivity = MainActivity.getMainActivity();
    private final WallpaperSettings settings = mainActivity.getSettings();
    private TimerFragment fragment;
    private TimePickerMode mode;
    private int currentHour;
    private int currentMinutes;

    public TimePickerView(@NonNull Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.time_picker);
    }

    public void setMode(TimePickerMode mode) {
        this.mode = mode;
    }


    public void setFragment(TimerFragment fragment) {
        this.fragment = fragment;
        init();
    }

    private void init() {
        this.currentHour = mode == TimePickerMode.STARTING_TIME ? settings.getStartingHours() : settings.getEndingHours();
        this.currentMinutes = mode == TimePickerMode.STARTING_TIME ? settings.getStartingMinutes() : settings.getEndingMinutes();

        updateCurrentTimeView();

        NumberPicker hours = findViewById(R.id.hour);
        NumberPicker minutes = findViewById(R.id.minute);
        if (hours == null || minutes == null) {
            throw new NullPointerException("Missing NumberPicker!");
        }

        hours.setMinValue(0);
        hours.setMaxValue(23);
        hours.setValue(currentHour);

        minutes.setMinValue(0);
        minutes.setMaxValue(59);
        minutes.setValue(currentMinutes);

        hours.setOnValueChangedListener((numberPicker, i, newVal) -> {
            this.currentHour = newVal;

            updateCurrentTimeView();
        });

        minutes.setOnValueChangedListener((numberPicker, i, newVal) -> {
            this.currentMinutes = newVal;

            updateCurrentTimeView();
        });

        AppCompatButton cancelButton = findViewById(R.id.cancelButton);
        if (cancelButton == null) {
            throw new NullPointerException("Missing cancel button!");
        }

        cancelButton.setOnClickListener(view -> dismiss());

        AppCompatButton continueButton = findViewById(R.id.confirmButton);
        if (continueButton == null) {
            throw new NullPointerException("Missing confirm button!");
        }

        continueButton.setOnClickListener(view -> {
            switch (mode) {
                case STARTING_TIME:
                    settings.setStartingHours(currentHour);
                    settings.setStartingMinutes(currentMinutes);
                    break;
                case ENDING_TIME:
                    settings.setEndingHours(currentHour);
                    settings.setEndingMinutes(currentMinutes);
                    break;
            }

            if (fragment.getView() != null) {
                fragment.bindText(fragment.getView());
                fragment.setChecked(fragment.getView(), R.id.timerOptionInvisible);
                settings.setTimerQuickOption(TimerQuickOptions.CUSTOM);
            }

            dismiss();
        });

    }

    private void updateCurrentTimeView() {
        AppCompatTextView currentTime = findViewById(R.id.currentTime);
        if (currentTime == null) {
            throw new NullPointerException("Missing TextView!");
        }

        String currentHoursAsString = String.valueOf(currentHour);
        if (currentHoursAsString.length() == 1) {
            currentHoursAsString = "0" + currentHoursAsString;
        }

        String currentMinutesAsString = String.valueOf(currentMinutes);
        if (currentMinutesAsString.length() == 1) {
            currentMinutesAsString = "0" + currentMinutesAsString;
        }

        String time = currentHoursAsString + ":" + currentMinutesAsString;
        currentTime.setText(time);
    }
}
