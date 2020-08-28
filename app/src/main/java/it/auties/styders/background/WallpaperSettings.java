package it.auties.styders.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.TypedValue;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import it.auties.styders.R;
import it.auties.styders.main.MainActivity;
import it.auties.styders.utils.SerializableBitmap;
import it.auties.styders.wallpaper.WallPaperService;

import static it.auties.styders.background.WallpaperSetting.ACTIVATE_CONDITIONS;
import static it.auties.styders.background.WallpaperSetting.BACKGROUND_STYLE;
import static it.auties.styders.background.WallpaperSetting.BORDER_ENABLED;
import static it.auties.styders.background.WallpaperSetting.BORDER_HOME;
import static it.auties.styders.background.WallpaperSetting.BORDER_SPEED;
import static it.auties.styders.background.WallpaperSetting.BORDER_STYLE;
import static it.auties.styders.background.WallpaperSetting.ENDING_HOURS;
import static it.auties.styders.background.WallpaperSetting.ENDING_MINUTES;
import static it.auties.styders.background.WallpaperSetting.IMAGE_BRIGHTNESS;
import static it.auties.styders.background.WallpaperSetting.NOTCH_BOTTOM;
import static it.auties.styders.background.WallpaperSetting.NOTCH_BOTTOM_FULL;
import static it.auties.styders.background.WallpaperSetting.NOTCH_ENABLED;
import static it.auties.styders.background.WallpaperSetting.NOTCH_HEIGHT;
import static it.auties.styders.background.WallpaperSetting.NOTCH_TOP;
import static it.auties.styders.background.WallpaperSetting.NOTCH_WIDTH;
import static it.auties.styders.background.WallpaperSetting.RADIUS_BOTTOM;
import static it.auties.styders.background.WallpaperSetting.RADIUS_TOP;
import static it.auties.styders.background.WallpaperSetting.RESTART;
import static it.auties.styders.background.WallpaperSetting.SEQUENCES;
import static it.auties.styders.background.WallpaperSetting.STARTING_HOURS;
import static it.auties.styders.background.WallpaperSetting.STARTING_MINUTES;
import static it.auties.styders.background.WallpaperSetting.STYLE;
import static it.auties.styders.background.WallpaperSetting.TIMER_DAYS;
import static it.auties.styders.background.WallpaperSetting.TIMER_ENABLED;
import static it.auties.styders.background.WallpaperSetting.TIMER_QUICK_OPTION;

public class WallpaperSettings {
    private static WallpaperSettings instance;
    private boolean borderEnabled;
    private boolean timerEnabled;
    private int borderSizeHomeScreen;
    private int borderSpeed;
    private boolean notch;
    private int notchHeight;
    private int notchWidth;
    private int notchBottomFull;
    private int notchBottom;
    private int notchTop;
    private int radiusBottom;
    private int radiusTop;
    private SerializableBitmap background;
    private int imageBorderBrightness;
    private ColorSequence sequence;
    private BorderStyle borderStyle;
    private BackgroundStyle backgroundStyle;
    private StydersStyle stydersStyle;
    private Set<ToggleBorderOption> activateLiveBorderOnlyWhen;
    private RestartOption restartOption;
    private boolean newColor;
    private AppState appState;
    private int startingHours;
    private int startingMinutes;
    private int endingHours;
    private int endingMinutes;
    private Set<Day> timerDays;
    private TimerQuickOptions timerQuickOption;
    private Bitmap black;
    private BorderStyle lastBorder;

    private WallpaperSettings() {

    }

    private WallpaperSettings init(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Styders", Context.MODE_PRIVATE);
        if (!preferences.getBoolean("setup", false)) {
            preferences
                    .edit()
                    .putBoolean(BORDER_ENABLED, true)
                    .putBoolean(TIMER_ENABLED, false)
                    .putInt(BORDER_SPEED, 80)
                    .putInt(BORDER_HOME, 50)
                    .putInt(RADIUS_TOP, 96)
                    .putInt(RADIUS_BOTTOM, 76)
                    .putInt(IMAGE_BRIGHTNESS, 100)
                    .putBoolean(NOTCH_ENABLED, false)
                    .putString(SEQUENCES, new Gson().toJson(ColorSequence.empty(), ColorSequence.class))
                    .putInt(BORDER_STYLE, BorderStyle.CONTINUOUS_LIGHTING.ordinal())
                    .putInt(BACKGROUND_STYLE, BackgroundStyle.BLACK.ordinal())
                    .putInt(RESTART, RestartOption.NONE.ordinal())
                    .putInt(STYLE, StydersStyle.DARK_GRAY.ordinal())
                    .putStringSet(ACTIVATE_CONDITIONS, new HashSet<>())
                    .putInt(STARTING_HOURS, 7)
                    .putInt(STARTING_MINUTES, 0)
                    .putInt(ENDING_HOURS, 22)
                    .putInt(ENDING_MINUTES, 0)
                    .putInt(TIMER_QUICK_OPTION, TimerQuickOptions.NIGHT.ordinal())
                    .putInt(NOTCH_HEIGHT, 80)
                    .putInt(NOTCH_WIDTH, 88)
                    .putInt(NOTCH_BOTTOM_FULL, 92)
                    .putInt(NOTCH_TOP, 43)
                    .putInt(NOTCH_BOTTOM, 98)
                    .putStringSet(TIMER_DAYS, new HashSet<>(Day.valuesAsString()))
                    .putBoolean("setup", true)
                    .apply();

            this.borderEnabled = true;
            this.timerEnabled = false;
            this.borderSpeed = 80;
            this.borderSizeHomeScreen = 50;
            this.radiusBottom = 76;
            this.radiusTop = 96;
            this.imageBorderBrightness = 100;
            this.notch = false;
            this.background = new SerializableBitmap(getBitmapFromColor());
            this.sequence = ColorSequence.empty();
            this.borderStyle = BorderStyle.CONTINUOUS_LIGHTING;
            this.backgroundStyle = BackgroundStyle.BLACK;
            this.restartOption = RestartOption.NONE;
            this.stydersStyle = StydersStyle.DARK_GRAY;
            this.activateLiveBorderOnlyWhen = new HashSet<>();

            this.appState = AppState.IN;
            this.startingHours = 7;
            this.startingMinutes = 0;
            this.endingHours = 22;
            this.endingMinutes = 0;
            this.timerQuickOption = TimerQuickOptions.NIGHT;
            this.timerDays = new HashSet<>();
            timerDays.addAll(Arrays.asList(Day.values()));
            this.notchHeight = 80;
            this.notchWidth = 88;
            this.notchBottomFull = 92;
            this.notchTop = 43;
            this.notchBottom = 98;
        }else {
            Gson gson = new Gson();
            this.borderEnabled = preferences.getBoolean(WallpaperSetting.BORDER_ENABLED, true);
            this.timerEnabled = preferences.getBoolean(WallpaperSetting.TIMER_ENABLED, false);
            this.borderSpeed = preferences.getInt(WallpaperSetting.BORDER_SPEED, 80);
            this.borderSizeHomeScreen = preferences.getInt(WallpaperSetting.BORDER_HOME, 50);
            this.radiusBottom = preferences.getInt(WallpaperSetting.RADIUS_BOTTOM, 76);
            this.radiusTop = preferences.getInt(WallpaperSetting.RADIUS_TOP, 96);
            this.imageBorderBrightness = preferences.getInt(WallpaperSetting.IMAGE_BRIGHTNESS, 80);
            this.notch = preferences.getBoolean(WallpaperSetting.NOTCH_ENABLED, false);
            this.background = new SerializableBitmap(getBitmapFromColor());
            this.sequence = preferences.contains(WallpaperSetting.SEQUENCES) ? gson.fromJson(preferences.getString(WallpaperSetting.SEQUENCES, ""), ColorSequence.class) : ColorSequence.empty();
            this.borderStyle = BorderStyle.values()[preferences.getInt(WallpaperSetting.BORDER_STYLE, 1)];
            this.backgroundStyle = BackgroundStyle.values()[preferences.getInt(WallpaperSetting.BACKGROUND_STYLE, 0)];
            this.restartOption = RestartOption.values()[preferences.getInt(WallpaperSetting.RESTART, 1)];
            this.stydersStyle = StydersStyle.values()[preferences.getInt(WallpaperSetting.STYLE, 2)];
            if (preferences.contains(WallpaperSetting.ACTIVATE_CONDITIONS)) {
                Set<ToggleBorderOption> set = new HashSet<>();
                for (String s : Objects.requireNonNull(preferences.getStringSet(WallpaperSetting.ACTIVATE_CONDITIONS, null))) {
                    ToggleBorderOption toggleBorderOption = ToggleBorderOption.values()[Integer.parseInt(s)];
                    set.add(toggleBorderOption);
                }

                this.activateLiveBorderOnlyWhen = set;
            } else {
                this.activateLiveBorderOnlyWhen = new HashSet<>();
            }

            this.appState = AppState.IN;
            this.startingHours = preferences.getInt(WallpaperSetting.STARTING_HOURS, 7);
            this.startingMinutes = preferences.getInt(WallpaperSetting.STARTING_MINUTES, 0);
            this.endingHours = preferences.getInt(WallpaperSetting.ENDING_HOURS, 22);
            this.endingMinutes = preferences.getInt(WallpaperSetting.ENDING_MINUTES, 0);
            this.timerQuickOption = TimerQuickOptions.values()[preferences.getInt(WallpaperSetting.TIMER_QUICK_OPTION, 0)];
            if (preferences.contains(WallpaperSetting.TIMER_DAYS)) {
                Set<Day> set = new HashSet<>();
                for (String s : Objects.requireNonNull(preferences.getStringSet(WallpaperSetting.TIMER_DAYS, null))) {
                    Day toggleBorderOption = Day.valueOf(s);
                    set.add(toggleBorderOption);
                }

                this.timerDays = set;
            } else {
                this.timerDays = new HashSet<>();
                timerDays.addAll(Arrays.asList(Day.values()));
            }

            this.notchHeight = preferences.getInt(WallpaperSetting.NOTCH_HEIGHT, 80);
            this.notchWidth = preferences.getInt(WallpaperSetting.NOTCH_WIDTH, 88);
            this.notchBottomFull = preferences.getInt(WallpaperSetting.NOTCH_BOTTOM_FULL, 92);
            this.notchTop = preferences.getInt(WallpaperSetting.NOTCH_TOP, 43);
            this.notchBottom = preferences.getInt(WallpaperSetting.NOTCH_BOTTOM, 98);
        }

        this.black = getBitmapFromColor();
        instance = this;
        return this;
    }

    public static WallpaperSettings getInstance(Context context) {
        if (instance == null) {
            return new WallpaperSettings().init(context);
        }

        return instance;
    }

    private Bitmap getBitmapFromColor() {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        return bitmap;
    }

    public void enableNotch(boolean b) {
        this.notch = b;
        onSettingChanged(WallpaperSetting.NOTCH_ENABLED);
    }

    public void setCustomBackground(Bitmap bitmap) {
        this.background = new SerializableBitmap(bitmap);
        onSettingChanged(WallpaperSetting.BACKGROUND_STYLE);
    }

    public boolean isBorderEnabled() {
        return borderEnabled;
    }

    public void setBorderEnabled(boolean borderEnabled) {
        this.borderEnabled = borderEnabled;
        onSettingChanged(WallpaperSetting.BORDER_ENABLED);
    }

    public int getBorderSizeHomeScreen() {
        return borderSizeHomeScreen;
    }

    public void setBorderSizeHomeScreen(int borderSizeHomeScreen) {
        this.borderSizeHomeScreen = borderSizeHomeScreen;
        onSettingChanged(WallpaperSetting.BORDER_HOME);
    }

    public int getBorderSpeed() {
        return borderSpeed;
    }

    public void setBorderSpeed(int borderSpeed) {
        this.borderSpeed = borderSpeed;
        onSettingChanged(WallpaperSetting.BORDER_SPEED);
    }

    public int getNotchBottomFull() {
        return notchBottomFull;
    }

    public void setNotchBottomFull(int notchBottomFull) {
        this.notchBottomFull = notchBottomFull;
        onSettingChanged(WallpaperSetting.NOTCH_BOTTOM_FULL);
    }

    public int getNotchHeight() {
        return notchHeight;
    }

    public void setNotchHeight(int notchHeight) {
        this.notchHeight = notchHeight;
        onSettingChanged(WallpaperSetting.NOTCH_HEIGHT);
    }

    public int getNotchBottom() {
        return notchBottom;
    }

    public void setNotchBottom(int notchBottom) {
        this.notchBottom = notchBottom;
        onSettingChanged(WallpaperSetting.NOTCH_BOTTOM);
    }

    public int getNotchTop() {
        return notchTop;
    }

    public void setNotchTop(int notchTop) {
        this.notchTop = notchTop;
        onSettingChanged(WallpaperSetting.NOTCH_TOP);
    }

    public int getNotchWidth() {
        return notchWidth;
    }

    public void setNotchWidth(int notchWidth) {
        this.notchWidth = notchWidth;
        onSettingChanged(WallpaperSetting.NOTCH_WIDTH);
    }

    public int getRadiusBottom() {
        return radiusBottom;
    }

    public void setRadiusBottom(int radiusBottom) {
        this.radiusBottom = radiusBottom;
        onSettingChanged(WallpaperSetting.RADIUS_BOTTOM);
    }

    public int getRadiusTop() {
        return radiusTop;
    }

    public void setRadiusTop(int radiusTop) {
        this.radiusTop = radiusTop;
        onSettingChanged(WallpaperSetting.RADIUS_TOP);
    }

    public Bitmap getBackground() {
        if (appState == AppState.UNKNOWN) {
            return black;
        }

        if (appState == AppState.IN) {
            if (MainActivity.getMainActivity() != null) {
                TypedValue value = new TypedValue();
                MainActivity.getMainActivity().getTheme().resolveAttribute(R.attr.background, value, true);

                Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(value.data);

                return bitmap;
            } else {
                return black;
            }
        }

        return background.getBitmap();
    }

    private void onSettingChanged(String setting) {
        WallPaperService.setUpdate(setting);
    }

    public void setAppState(AppState appState) {
        this.appState = appState;
    }

    public boolean isNotch() {
        return notch;
    }

    public int getImageBorderBrightness() {
        return imageBorderBrightness;
    }

    public void setImageBorderBrightness(int imageBorderBrightness) {
        this.imageBorderBrightness = imageBorderBrightness;
        setNewColor(true);
        onSettingChanged(WallpaperSetting.IMAGE_BRIGHTNESS);
    }

    public RestartOption getRestartOption() {
        return restartOption;
    }

    public void setRestartOption(RestartOption restartOption) {
        this.restartOption = restartOption;
        onSettingChanged(WallpaperSetting.RESTART);
    }

    public BackgroundStyle getBackgroundStyle() {
        return backgroundStyle;
    }

    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
        onSettingChanged(WallpaperSetting.BORDER_STYLE);
    }

    public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
        onSettingChanged(BACKGROUND_STYLE);
    }

    public boolean isNewColor() {
        return newColor;
    }

    public void setNewColor(boolean newColor) {
        this.newColor = newColor;
    }

    public StydersStyle getStydersStyle() {
        return stydersStyle;
    }

    public void setStydersStyle(StydersStyle stydersStyle) {
        if (getStydersStyle() == stydersStyle) {
            return;
        }

        this.stydersStyle = stydersStyle;
        MainActivity mainActivity = MainActivity.getMainActivity();
        mainActivity.refreshUI();
        onSettingChanged(WallpaperSetting.STYLE);
    }

    public Set<ToggleBorderOption> getActivateLiveBorderOnlyWhen() {
        return activateLiveBorderOnlyWhen;
    }

    public void addToggleOption(ToggleBorderOption options) {
        getActivateLiveBorderOnlyWhen().add(options);
        onSettingChanged(ACTIVATE_CONDITIONS);
    }

    public void removeToggleOption(ToggleBorderOption options) {
        getActivateLiveBorderOnlyWhen().remove(options);
        onSettingChanged(ACTIVATE_CONDITIONS);
    }

    public boolean isTimerEnabled() {
        return timerEnabled;
    }

    public void setTimerEnabled(boolean timerEnabled) {
        this.timerEnabled = timerEnabled;
        onSettingChanged(TIMER_ENABLED);
    }

    public int getStartingHours() {
        return startingHours;
    }

    public void setStartingHours(int startingHours) {
        this.startingHours = startingHours;
        onSettingChanged(WallpaperSetting.STARTING_HOURS);
    }

    public int getStartingMinutes() {
        return startingMinutes;
    }

    public void setStartingMinutes(int startingMinutes) {
        this.startingMinutes = startingMinutes;
        onSettingChanged(WallpaperSetting.STARTING_MINUTES);
    }

    public int getEndingHours() {
        return endingHours;
    }

    public void setEndingHours(int endingHours) {
        this.endingHours = endingHours;
        onSettingChanged(WallpaperSetting.ENDING_HOURS);
    }

    public int getEndingMinutes() {
        return endingMinutes;
    }

    public void setEndingMinutes(int endingMinutes) {
        this.endingMinutes = endingMinutes;
        onSettingChanged(WallpaperSetting.ENDING_MINUTES);
    }

    public Set<Day> getTimerDays() {
        return timerDays;
    }

    public TimerQuickOptions getTimerQuickOption() {
        return timerQuickOption;
    }

    public void setTimerQuickOption(TimerQuickOptions timerQuickOption) {
        this.timerQuickOption = timerQuickOption;
        onSettingChanged(WallpaperSetting.TIMER_QUICK_OPTION);
    }

    public boolean isVisibleBecauseOfTimer(boolean b) {
        if (!timerEnabled) {
            return true;
        }

        Calendar now = Calendar.getInstance();

        Calendar nowAfter = Calendar.getInstance();
        nowAfter.set(Calendar.HOUR_OF_DAY, getStartingHours());
        nowAfter.set(Calendar.MINUTE, getStartingMinutes());
        nowAfter.set(Calendar.SECOND, 0);

        Calendar endAfter = Calendar.getInstance();
        endAfter.set(Calendar.HOUR_OF_DAY, getEndingHours());
        endAfter.set(Calendar.MINUTE, getEndingMinutes());
        endAfter.set(Calendar.SECOND, 0);

        if (b && !getTimerDays().contains(Day.fromInt(now.get(Calendar.DAY_OF_WEEK)))) {
            return false;
        }

        if (now.after(nowAfter)) {
            if (getEndingHours() >= getStartingHours()) {
                return now.before(endAfter);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void setBlack(Bitmap black) {
        this.black = black;
    }

    public BorderStyle getLastBorder() {
        return lastBorder;
    }

    public ColorSequence getColorSequences() {
        return sequence;
    }

    public void setColorSequences(ColorSequence sequence) {
        this.sequence = sequence;
        onSettingChanged(WallpaperSetting.SEQUENCES);
    }

    public void setLastBorder(BorderStyle lastBorder) {
        this.lastBorder = lastBorder;
    }
}

