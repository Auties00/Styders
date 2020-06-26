package it.auties.styders.background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import it.auties.styders.R;
import it.auties.styders.main.MainActivity;
import it.auties.styders.utils.AudioUtils;
import it.auties.styders.utils.PowerUtils;
import it.auties.styders.utils.SerializableBitmap;
import it.auties.styders.wallpaper.WallPaperService;

public class WallpaperSettings {
    @Expose(serialize = false, deserialize = false)
    private static WallpaperSettings instance;
    @Expose
    private boolean borderEnabled;
    @Expose
    private boolean timerEnabled;
    @Expose
    private int borderSizeHomeScreen;
    @Expose
    private int borderSpeed;
    @Expose
    private boolean notch;
    @Expose
    private int notchHeight;
    @Expose
    private int notchWidth;
    @Expose
    private int notchBottomFull;
    @Expose
    private int notchBottom;
    @Expose
    private int notchTop;
    @Expose
    private int radiusBottom;
    @Expose
    private int radiusTop;
    @Expose
    private SerializableBitmap background;
    @Expose
    private boolean newImage;
    @Expose
    private int imageBorderBrightness;
    @Expose
    private ColorSequence sequence;
    @Expose
    private BorderStyle borderStyle;
    @Expose
    private BackgroundStyle backgroundStyle;
    @Expose
    private StydersStyle stydersStyle;
    @Expose
    private Set<ToggleBorderOption> activateLiveBorderOnlyWhen;
    @Expose
    private RestartOption restartOption;
    @Expose
    private boolean hiddenEnable;
    @Expose
    private boolean timerHidden;
    @Expose(serialize = false, deserialize = false)
    private boolean newColor;
    @Expose(serialize = false, deserialize = false)
    private AppState appState;
    @Expose(serialize = false, deserialize = false)
    private ShowState showState;
    @Expose
    private int startingHours;
    @Expose
    private int startingMinutes;
    @Expose
    private int endingHours;
    @Expose
    private int endingMinutes;
    @Expose
    private Set<Day> timerDays;
    @Expose
    private TimerQuickOptions timerQuickOption;
    @Expose(serialize = false, deserialize = false)
    private Bitmap black;
    @Expose
    private BorderStyle lastBorder;


    private WallpaperSettings() {

    }

    private WallpaperSettings init(File dir) {
        File check = new File(dir, "styders.txt");
        if (!check.exists()) {
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
            this.hiddenEnable = false;
            this.appState = AppState.IN;
            this.startingHours = 7;
            this.startingMinutes = 0;
            this.endingHours = 22;
            this.endingMinutes = 0;
            this.timerQuickOption = TimerQuickOptions.NIGHT;
            this.timerDays = new HashSet<>();
            this.timerHidden = false;
            this.notchHeight = 80;
            this.notchWidth = 88;
            this.notchBottomFull = 92;
            this.notchTop = 43;
            this.notchBottom = 98;
            this.black = getBitmapFromColor();
            timerDays.addAll(Arrays.asList(Day.values()));
            try {
                if (!check.createNewFile()) {
                    Log.d("[FileIO]", "Checker file couldn't be created!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            instance = this;
            return this;
        }

        Gson gson = new GsonBuilder()
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                        return expose != null && !expose.deserialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                })
                .create();

        File file = new File(dir, "SettingsStyders.json");
        if (!file.exists()) {
            return null;
        }

        WallpaperSettings objFromJson;
        try {
            objFromJson = gson.fromJson(new FileReader(file), WallpaperSettings.class);
        } catch (Exception e) {
            return null;
        }

        objFromJson.setBlack(getBitmapFromColor());
        objFromJson.setAppState(AppState.OUT);
        instance = objFromJson;
        return objFromJson;
    }

    public static WallpaperSettings getInstance(File dir) {
        if (instance == null) {
            return new WallpaperSettings().init(dir);
        }

        return instance;
    }

    public void adjustState(Context context) {
        if (getActivateLiveBorderOnlyWhen().size() > 0) {
            if ((getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.CHARGER) && PowerUtils.isPlugged(context)) || (getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.HEADPHONES) && AudioUtils.isWiredToHeadphones(context))) {
                setShowState(ShowState.STATIC);
            } else {
                setShowState(ShowState.HIDDEN);
            }
        } else {

            setShowState(null);
        }
    }

    private Bitmap getBitmapFromColor() {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        return bitmap;
    }


    public void serialize(Context activity) throws IOException {
        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                        return expose != null && !expose.serialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                })
                .create();
        String json = gson.toJson(this);

        File file = new File(activity.getFilesDir(), "SettingsStyders.json");
        if (file.exists()) {
            if (!file.delete()) {
                Log.d("[Styders]", "This file couldn't be removed!");
            }
        }

        if (!file.createNewFile()) {
            Log.d("[Styders]", "This file couldn't be created!");
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream);
        myOutWriter.append(json);
        myOutWriter.close();
        outputStream.close();
    }

    public void enableNotch(boolean b) {
        this.notch = b;
        onSettingChanged();
    }

    public void setCustomBackground(Bitmap bitmap) {
        this.background = new SerializableBitmap(bitmap);
    }


    public boolean isBorderEnabled() {
        return borderEnabled;
    }

    public void setBorderEnabled(boolean borderEnabled) {
        this.borderEnabled = borderEnabled;
        onSettingChanged();
    }

    public int getBorderSizeHomeScreen() {
        return borderSizeHomeScreen;
    }

    public void setBorderSizeHomeScreen(int borderSizeHomeScreen) {
        this.borderSizeHomeScreen = borderSizeHomeScreen;
        onSettingChanged();
    }

    public int getBorderSpeed() {
        return borderSpeed;
    }

    public void setBorderSpeed(int borderSpeed) {
        this.borderSpeed = borderSpeed;
        onSettingChanged();
    }

    public int getNotchBottomFull() {
        return notchBottomFull;
    }

    public void setNotchBottomFull(int notchBottomFull) {
        this.notchBottomFull = notchBottomFull;
        onSettingChanged();
    }

    public int getNotchHeight() {
        return notchHeight;
    }

    public void setNotchHeight(int notchHeight) {
        this.notchHeight = notchHeight;
        onSettingChanged();
    }

    public int getNotchBottom() {
        return notchBottom;
    }

    public void setNotchBottom(int notchBottom) {
        this.notchBottom = notchBottom;
        onSettingChanged();
    }

    public int getNotchTop() {
        return notchTop;
    }

    public void setNotchTop(int notchTop) {
        this.notchTop = notchTop;
        onSettingChanged();
    }

    public int getNotchWidth() {
        return notchWidth;
    }

    public void setNotchWidth(int notchWidth) {
        this.notchWidth = notchWidth;
        onSettingChanged();
    }

    public int getRadiusBottom() {
        return radiusBottom;
    }

    public void setRadiusBottom(int radiusBottom) {
        this.radiusBottom = radiusBottom;
        onSettingChanged();
    }

    public int getRadiusTop() {
        return radiusTop;
    }

    public void setRadiusTop(int radiusTop) {
        this.radiusTop = radiusTop;
        onSettingChanged();
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

    private void onSettingChanged() {
        WallPaperService.setUpdate(true);
    }

    public void setAppState(AppState appState) {
        this.appState = appState;
    }

    public boolean isNotch() {
        return notch;
    }

    public boolean isNewImage() {
        return newImage;
    }

    public void setNewImage(boolean newImage) {
        this.newImage = newImage;
    }

    public int getImageBorderBrightness() {
        return imageBorderBrightness;
    }

    public void setImageBorderBrightness(int imageBorderBrightness) {
        this.imageBorderBrightness = imageBorderBrightness;
        setNewColor(true);
    }

    public RestartOption getRestartOption() {
        return restartOption;
    }

    public void setRestartOption(RestartOption restartOption) {
        this.restartOption = restartOption;
    }

    public BackgroundStyle getBackgroundStyle() {
        return backgroundStyle;
    }

    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(BorderStyle borderStyle) {
        if (getBorderStyle() == BorderStyle.STATIC_LIGHTING) {
            onSettingChanged();
        }

        this.borderStyle = borderStyle;
    }

    public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
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
    }

    public Set<ToggleBorderOption> getActivateLiveBorderOnlyWhen() {
        return activateLiveBorderOnlyWhen;
    }

    public void addToggleOption(ToggleBorderOption options) {
        getActivateLiveBorderOnlyWhen().add(options);
        if (getActivateLiveBorderOnlyWhen().size() > 0) {
            setHiddenEnable();
        }
    }

    public void removeToggleOption(ToggleBorderOption options) {
        getActivateLiveBorderOnlyWhen().remove(options);
        if (getActivateLiveBorderOnlyWhen().size() < 1) {
            this.hiddenEnable = false;
        }
    }

    public boolean isHiddenEnable() {
        if (activateLiveBorderOnlyWhen.size() < 1) {
            this.hiddenEnable = false;
        }

        return hiddenEnable;
    }

    private void setHiddenEnable() {
        this.hiddenEnable = true;
    }

    public boolean isTimerEnabled() {
        return timerEnabled;
    }

    public void setTimerEnabled(boolean timerEnabled) {
        this.timerEnabled = timerEnabled;
    }

    public int getStartingHours() {
        return startingHours;
    }

    public void setStartingHours(int startingHours) {
        this.startingHours = startingHours;
    }

    public int getStartingMinutes() {
        return startingMinutes;
    }

    public void setStartingMinutes(int startingMinutes) {
        this.startingMinutes = startingMinutes;
    }

    public int getEndingHours() {
        return endingHours;
    }

    public void setEndingHours(int endingHours) {
        this.endingHours = endingHours;
    }

    public int getEndingMinutes() {
        return endingMinutes;
    }

    public void setEndingMinutes(int endingMinutes) {
        this.endingMinutes = endingMinutes;
    }

    public Set<Day> getTimerDays() {
        return timerDays;
    }

    public TimerQuickOptions getTimerQuickOption() {
        return timerQuickOption;
    }

    public void setTimerQuickOption(TimerQuickOptions timerQuickOption) {
        this.timerQuickOption = timerQuickOption;
    }

    public void schedulePendingIntent() {
        checkTime(false);
    }

    public void checkTime(boolean b) {
        if (!timerEnabled) {
            setTimerHidden(false);
            return;
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
            setTimerHidden(true);
            return;
        }

        if (now.after(nowAfter)) {
            if (getEndingHours() >= getStartingHours()) {
                if (now.before(endAfter)) {
                    setTimerHidden(false);
                } else {
                    setTimerHidden(true);
                }
            } else {
                setTimerHidden(false);
            }
        } else {
            setTimerHidden(true);
        }
    }

    public boolean isTimerHidden() {
        return timerHidden;
    }

    private void setTimerHidden(boolean timerHidden) {
        this.timerHidden = timerHidden;
    }


    public void setShowState(ShowState showState) {
        this.showState = showState;
    }

    public ShowState getShowState() {
        return showState;
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
    }

    public void setLastBorder(BorderStyle lastBorder) {
        this.lastBorder = lastBorder;
    }
}

