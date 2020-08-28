package it.auties.styders.fragment;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.radiobutton.MaterialRadioButton;

import it.auties.styders.R;
import it.auties.styders.background.Day;
import it.auties.styders.background.TimerQuickOptions;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.main.MainActivity;
import it.auties.styders.utils.TimePickerMode;
import it.auties.styders.utils.TimePickerView;

public class TimerFragment extends Fragment {
    private final MainActivity mainActivity;
    private final WallpaperSettings settings;

    public TimerFragment() {
        this.mainActivity = MainActivity.getMainActivity();
        this.settings = mainActivity.getSettings();
    }

    public TimerFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.settings = mainActivity.getSettings();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        bindText(view);
        bindSwitch(view);
        bindTime(view);
        bindDays(view);
        bindRadio(view);
        checkDays(view);

        return view;
    }

    public void bindText(View fragment) {
        AppCompatTextView startingTime = fragment.findViewById(R.id.startingTime);
        String starting = (settings.getStartingHours() == 0 ? "00" : settings.getStartingHours()) + ":" + (String.valueOf(settings.getStartingMinutes()).length() == 1 ? "0" + settings.getStartingMinutes() : settings.getStartingMinutes());
        startingTime.setText(starting);

        AppCompatTextView endingTime = fragment.findViewById(R.id.endingTime);
        String ending = (settings.getEndingHours() == 0 ? "00" : settings.getEndingHours()) + ":" + (String.valueOf(settings.getEndingMinutes()).length() == 1 ? "0" + settings.getEndingMinutes() : settings.getEndingMinutes());
        endingTime.setText(ending);
    }

    private void bindDays(View fragment) {
        for (int x = 0; x < 7; x++) {
            TypedValue enabled = new TypedValue();
            MainActivity.getMainActivity().getTheme().resolveAttribute(R.attr.enabled_day, enabled, true);

            TypedValue disabled = new TypedValue();
            MainActivity.getMainActivity().getTheme().resolveAttribute(R.attr.disabled_day, disabled, true);

            Day day = Day.values()[x];
            AppCompatTextView textView = fragment.findViewWithTag(day.name());
            textView.setOnClickListener(view -> {
                if (settings.getTimerDays().contains(day)) {
                    textView.setTextColor(disabled.data);
                    settings.getTimerDays().remove(day);
                } else {
                    textView.setTextColor(enabled.data);
                    settings.getTimerDays().add(day);
                }
            });
        }
    }

    private void checkDays(View fragment) {
        for (int x = 0; x < 7; x++) {
            TypedValue enabled = new TypedValue();
            MainActivity.getMainActivity().getTheme().resolveAttribute(R.attr.enabled_day, enabled, true);

            TypedValue disabled = new TypedValue();
            MainActivity.getMainActivity().getTheme().resolveAttribute(R.attr.disabled_day, disabled, true);

            Day day = Day.values()[x];
            AppCompatTextView textView = fragment.findViewWithTag(day.name());

            if (settings.getTimerDays().contains(day)) {
                textView.setTextColor(enabled.data);
            } else {
                textView.setTextColor(disabled.data);
            }

        }
    }

    private void bindTime(View fragment) {
        AppCompatTextView startingTime = fragment.findViewById(R.id.startingTime);
        startingTime.setOnClickListener(view -> {
            TimePickerView dialog = new TimePickerView(mainActivity, R.style.CustomBottomSheetDialog);
            dialog.setMode(TimePickerMode.STARTING_TIME);
            dialog.setFragment(this);
            dialog.show();
        });

        AppCompatTextView endingTime = fragment.findViewById(R.id.endingTime);
        endingTime.setOnClickListener(view -> {
            TimePickerView dialog = new TimePickerView(mainActivity, R.style.CustomBottomSheetDialog);
            dialog.setMode(TimePickerMode.ENDING_TIME);
            dialog.setFragment(this);
            dialog.show();
        });
    }

    private void bindSwitch(View fragment) {
        SwitchCompat switchCompat = fragment.findViewById(R.id.timerSwitch);
        switchCompat.setChecked(settings.isTimerEnabled());
        if (!switchCompat.isChecked()) {
            fragment.findViewById(R.id.timerSettingsTwo).setVisibility(View.GONE);
            fragment.findViewById(R.id.timerSettingsThree).setVisibility(View.GONE);
        }

        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            if (settings.isTimerEnabled()) {
                fragment.findViewById(R.id.timerSettingsTwo).setVisibility(View.GONE);
                fragment.findViewById(R.id.timerSettingsThree).setVisibility(View.GONE);
                settings.setTimerEnabled(false);
            } else {
                fragment.findViewById(R.id.timerSettingsTwo).setVisibility(View.VISIBLE);
                fragment.findViewById(R.id.timerSettingsThree).setVisibility(View.VISIBLE);
                settings.setTimerEnabled(true);
                checkDays(fragment);
            }
        });
    }

    private void bindRadio(View fragment) {
        switch (settings.getTimerQuickOption()) {
            case NIGHT:
                setChecked(fragment, R.id.timerOptionOne);
                break;
            case DAY:
                setChecked(fragment, R.id.timerOptionTwo);
                break;
            case CUSTOM:
                setChecked(fragment, R.id.timerOptionInvisible);
                break;
        }

        RadioButton button = fragment.findViewById(R.id.timerOptionOne);
        RadioButton button1 = fragment.findViewById(R.id.timerOptionTwo);
        if (button.getLineCount() > 1) {
            button.setGravity(Gravity.TOP);
        }

        if (button1.getLineCount() > 1) {
            button1.setGravity(Gravity.TOP);
        }

        RadioGroup group = fragment.findViewById(R.id.timerOptionGroup);
        group.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i != R.id.timerOptionInvisible) {
                switch (i) {
                    case R.id.timerOptionOne:
                        settings.setStartingHours(7);
                        settings.setStartingMinutes(0);
                        settings.setEndingHours(22);
                        settings.setEndingMinutes(0);
                        settings.setTimerQuickOption(TimerQuickOptions.NIGHT);
                        bindText(fragment);
                        break;
                    case R.id.timerOptionTwo:
                        settings.setStartingHours(22);
                        settings.setStartingMinutes(0);
                        settings.setEndingHours(7);
                        settings.setEndingMinutes(0);
                        settings.setTimerQuickOption(TimerQuickOptions.DAY);
                        bindText(fragment);
                        break;
                }
            }
        });
    }

    public void setChecked(View fragment, int id) {
        MaterialRadioButton radioButton = fragment.findViewById(id);
        radioButton.setChecked(true);
    }
}
