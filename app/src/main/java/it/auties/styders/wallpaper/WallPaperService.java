package it.auties.styders.wallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.animation.OvershootInterpolator;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import it.auties.styders.R;
import it.auties.styders.background.BorderStyle;
import it.auties.styders.background.ColorSequence;
import it.auties.styders.background.Day;
import it.auties.styders.background.ToggleBorderOption;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.service.NotificationService;
import it.auties.styders.utils.AudioUtils;
import it.auties.styders.utils.ColorUtils;
import it.auties.styders.utils.PowerUtils;

import static it.auties.styders.background.WallpaperSetting.ACTIVATE_CONDITIONS;
import static it.auties.styders.background.WallpaperSetting.BACKGROUND_STYLE;
import static it.auties.styders.background.WallpaperSetting.BORDER_ENABLED;
import static it.auties.styders.background.WallpaperSetting.BORDER_HOME;
import static it.auties.styders.background.WallpaperSetting.BORDER_SPEED;
import static it.auties.styders.background.WallpaperSetting.BORDER_STYLE;
import static it.auties.styders.background.WallpaperSetting.CHANGE;
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

public class WallPaperService extends WallpaperService {
    private static CustomEngine engine;
    public class CustomEngine extends Engine implements Runnable {
        private Paint borderPaint;
        private SweepGradient grad;
        private SurfaceHolder holder;
        private Paint imagePaint;
        private Path path;
        private int surfaceHeight;
        private int surfaceWidth;
        private boolean visible;
        private long last;
        private long lastAlpha;
        private int index;
        private int percentageForAlpha;
        private boolean increaseAlpha;
        private long wait;
        private float start;
        private float end;
        private float toAdd;
        private float toRemove;
        private int disappearancePhase;
        private boolean next;
        private boolean rotated;
        private Path invPath;
        private Paint helpFillPaint;
        private Paint blackFillPaint;
        private long ms;
        private Matrix standardMatrix;
        private WallpaperSettings wallpaperSettings;
        private float interpolation;
        private volatile String update;
        private volatile boolean showHelp;
        private boolean shouldRun;
        private Executor executor;

        private float lerp(float f, float f2, float f3) {
            return f + ((f2 - f) * f3);
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            setTouchEventsEnabled(false);

            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
            this.index = 0;
            this.percentageForAlpha = 0;
            this.start = -1;
            this.end = -1;
            this.toAdd = 0;
            this.toRemove = 0;
            this.disappearancePhase = 0;
            this.last = System.currentTimeMillis();
            this.next = false;
            this.ms = 0;
            this.wallpaperSettings = WallpaperSettings.getInstance(getBaseContext());
            this.shouldRun = true;
            this.executor = Executors.newScheduledThreadPool(1);
            this.update = BORDER_ENABLED;
            setup();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            super.onSurfaceChanged(surfaceHolder, i, i2, i3);

            if (getResources().getConfiguration().orientation == 2) {
                this.surfaceWidth = i3;
                this.surfaceHeight = i2;
                this.rotated = true;
            } else {
                this.surfaceWidth = i2;
                this.surfaceHeight = i3;
                this.rotated = false;
            }

            setup();
        }

        private void setup() {
            if (wallpaperSettings == null) {
                this.wallpaperSettings = WallpaperSettings.getInstance(getBaseContext());
            }

            this.borderPaint = new Paint(1);
            this.borderPaint.setStyle(Style.STROKE);
            this.borderPaint.setColor(-1);
            this.imagePaint = new Paint();
            this.imagePaint.setColor(-1);
            this.imagePaint.setFilterBitmap(true);
            makeBorderPaint();
            makePath();

            this.helpFillPaint = new Paint(1);
            this.helpFillPaint.setStyle(Paint.Style.FILL);
            this.helpFillPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.helping_blue));

            this.blackFillPaint = new Paint(1);
            blackFillPaint.setStyle(Paint.Style.FILL);
            blackFillPaint.setColor(ColorUtils.getDominantColor(wallpaperSettings.getBackground()));

            executor.execute(this);
        }

        private void makeBorderPaint(boolean staticMode) {
            long time = 5000L - (wallpaperSettings.getBorderSpeed() * 5);
            if (System.currentTimeMillis() - last < time) {
                if (System.currentTimeMillis() - lastAlpha < (time / 255 * 4)) {
                    return;
                }

                if (!staticMode) {
                    if (!increaseAlpha) {
                        this.borderPaint.setAlpha((percentageForAlpha * 255 / 100) * 2);
                        this.lastAlpha = System.currentTimeMillis();
                        if (percentageForAlpha > 0) {
                            percentageForAlpha -= 2;
                        }
                    } else {
                        this.borderPaint.setAlpha((percentageForAlpha * 255 / 100) * 2);
                        this.lastAlpha = System.currentTimeMillis();
                        percentageForAlpha += 2;
                        if (percentageForAlpha == 50) {
                            this.increaseAlpha = false;
                        }
                    }
                }

                return;
            }

            if (!staticMode) {
                this.increaseAlpha = true;
                this.percentageForAlpha = 0;
                int[] colorArray = wallpaperSettings.getColorSequences().getSequenceInUse().getColors();
                if (index > colorArray.length - 1) {
                    index = 0;
                }

                int[] iArr = new int[2];
                iArr[0] = colorArray[index];
                iArr[1] = colorArray[index];

                this.grad = new SweepGradient(((float) this.surfaceWidth) / 2.0f, ((float) this.surfaceHeight) / 2.0f, iArr, null);

                this.borderPaint.setShader(this.grad);
                borderPaint.setAlpha(0);

                last = System.currentTimeMillis();
                lastAlpha = System.currentTimeMillis();
                index++;
            }
        }

        private void makeBorderPaint() {
            int[] colorArray = wallpaperSettings.getColorSequences().getSequenceInUse().getColors();
            int[] iArr = new int[(colorArray.length + 1)];

            for (int x = 0; x < 7; x++) {
                iArr[x] = colorArray[x != 6 ? x : 0];
            }

            this.grad = new SweepGradient(((float) this.surfaceWidth) / 2.0f, ((float) this.surfaceHeight) / 2.0f, iArr, null);
            this.borderPaint.setShader(this.grad);

            borderPaint.setAlpha(wallpaperSettings.getImageBorderBrightness() * 255 / 100);
        }


        private void notification() {
            int[] colorArray = wallpaperSettings.getColorSequences().getSequenceInUse().getColors();
            if (index > colorArray.length - 1) {
                index = 0;
            }

            int[] iArr = new int[2];
            iArr[0] = colorArray[index];
            iArr[1] = colorArray[index];

            this.grad = new SweepGradient(((float) this.surfaceWidth) / 2.0f, ((float) this.surfaceHeight) / 2.0f, iArr, null);

            this.borderPaint.setShader(this.grad);

            if (ms < 500) {
                if (ms < 250) {
                    borderPaint.setAlpha((int) ms * 255 / 500);
                } else {
                    borderPaint.setAlpha((int) (255 - (ms * 255 / 500)));
                }

                ms++;
            } else {
                ms = 0;
                index++;
            }
        }


        private boolean canPost() {
            if (!visible) {
                return false;
            }

            draw();
            return true;
        }


        private void draw() {
            BorderStyle style = wallpaperSettings.getBorderStyle();

            Canvas lockHardwareCanvas = this.holder.getSurface().lockHardwareCanvas();
            if (this.rotated) {
                Matrix matrix = new Matrix();
                matrix.preRotate(-90.0f);
                matrix.postTranslate(0.0f, (float) this.surfaceWidth);
                lockHardwareCanvas.setMatrix(matrix);
            }

            long showTime = 0;
            int nanoTime = (int) ((System.nanoTime() - (showTime + 300000000)) / 1000000);
            if (wallpaperSettings.isBorderEnabled() && wallpaperSettings.isVisibleBecauseOfTimer(true) && wallpaperSettings.getActivateLiveBorderOnlyWhen().size() == 0) {
                interpolation = new OvershootInterpolator().getInterpolation(((float) Math.max(Math.min(nanoTime, 1000), 0)) / 1000.0f) * (lerp((float) wallpaperSettings.getBorderSizeHomeScreen() < 10 ? 10 : wallpaperSettings.getBorderSizeHomeScreen() / 5F, (float) wallpaperSettings.getBorderSizeHomeScreen() < 10 ? 10 : wallpaperSettings.getBorderSizeHomeScreen() / 2F, unlockProgress()));
            } else {
                if ((wallpaperSettings.getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.HEADPHONES) && AudioUtils.isWiredToHeadphones(getBaseContext()) || (wallpaperSettings.getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.CHARGER) && PowerUtils.isPlugged(getBaseContext())) || (wallpaperSettings.getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.NOTIFICATION) && NotificationService.IsNotificationActive())) && wallpaperSettings.isVisibleBecauseOfTimer(true)) {
                    interpolation = new OvershootInterpolator().getInterpolation(((float) Math.max(Math.min(nanoTime, 1000), 0)) / 1000.0f) * (lerp((float) wallpaperSettings.getBorderSizeHomeScreen() < 10 ? 10 : wallpaperSettings.getBorderSizeHomeScreen() / 5F, (float) wallpaperSettings.getBorderSizeHomeScreen() < 10 ? 10 : wallpaperSettings.getBorderSizeHomeScreen() / 2F, unlockProgress()));
                }else {
                    interpolation = 0;
                }
            }

            lockHardwareCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            if (wallpaperSettings.isNewColor()) {
                if (style == BorderStyle.STATIC_LIGHTING) {
                    if (wallpaperSettings.getLastBorder() == BorderStyle.BREATH_LIGHTING) {
                        makeBorderPaint(true);
                    } else {
                        makeBorderPaint();
                    }
                } else {
                    if (style == BorderStyle.BREATH_LIGHTING) {
                        makeBorderPaint(false);
                    } else {
                        makeBorderPaint();
                    }
                }

                wallpaperSettings.setNewColor(false);
            }

            try {
                lockHardwareCanvas.drawBitmap(wallpaperSettings.getBackground(), null, lockHardwareCanvas.getClipBounds(), this.imagePaint);
            } catch (Exception e) {
                Log.d("[DRAW]", "Probably caught an error!");
                Log.d("[EX]", Objects.requireNonNull(e.getMessage()));
            }

            blackFillPaint.setColor(ColorUtils.getDominantColor(wallpaperSettings.getBackground()));
            if(interpolation > 0.001F) {
                if (wallpaperSettings.getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.NOTIFICATION) && NotificationService.IsNotificationActive()) {
                    notification();
                } else if (wallpaperSettings.getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.CHARGER) || wallpaperSettings.getActivateLiveBorderOnlyWhen().contains(ToggleBorderOption.HEADPHONES)) {
                    switch (style) {
                        case CONTINUOUS_LIGHTING:
                            standardDraw(lockHardwareCanvas, nanoTime, false);
                            break;
                        case BREATH_LIGHTING:
                            breathLighting(lockHardwareCanvas, false);
                            break;
                        case DISAPPEARANCE_LIGHTING:
                            disappearanceLighting(lockHardwareCanvas, false);
                            break;
                        case CIRCUIT_LIGHTING:
                            circuitLighting(lockHardwareCanvas, nanoTime, false);
                            break;
                    }
                } else {
                    if (style == BorderStyle.STATIC_LIGHTING) {
                        BorderStyle borderStyle = wallpaperSettings.getLastBorder();
                        if (borderStyle == null) {
                            wallpaperSettings.setLastBorder(BorderStyle.CONTINUOUS_LIGHTING);
                        }

                        switch (Objects.requireNonNull(borderStyle)) {
                            case CONTINUOUS_LIGHTING:
                                standardDraw(lockHardwareCanvas, nanoTime, true);
                                break;
                            case BREATH_LIGHTING:
                                breathLighting(lockHardwareCanvas, true);
                                break;
                            case DISAPPEARANCE_LIGHTING:
                                disappearanceLighting(lockHardwareCanvas, true);
                                break;
                            case CIRCUIT_LIGHTING:
                                circuitLighting(lockHardwareCanvas, nanoTime, true);
                                break;
                        }
                    } else {
                        switch (style) {
                            case CONTINUOUS_LIGHTING:
                                standardDraw(lockHardwareCanvas, nanoTime, false);
                                break;
                            case BREATH_LIGHTING:
                                breathLighting(lockHardwareCanvas, false);
                                break;
                            case DISAPPEARANCE_LIGHTING:
                                disappearanceLighting(lockHardwareCanvas, false);
                                break;
                            case CIRCUIT_LIGHTING:
                                circuitLighting(lockHardwareCanvas, nanoTime, false);
                                break;
                        }
                    }
                }
            }

            this.holder.getSurface().unlockCanvasAndPost(lockHardwareCanvas);
        }

        private void standardDraw(Canvas lockHardwareCanvas, int nanoTime, boolean staticMode) {
            if (!staticMode) {
                float pow = (float) (((double) nanoTime) / Math.pow(wallpaperSettings.getBorderSpeed() >= 90 ? 10 : 100F - ((float) wallpaperSettings.getBorderSpeed()), 1.3d));
                this.standardMatrix = new Matrix();
                standardMatrix.preRotate(pow * 3, ((float) this.surfaceWidth) / 2.0f, ((float) this.surfaceHeight) / 2.0f);
            }

            this.grad.setLocalMatrix(standardMatrix);
            this.borderPaint.setStrokeWidth(interpolation);
            lockHardwareCanvas.drawPath(this.path, this.borderPaint);

            lockHardwareCanvas.drawPath(invPath, showHelp ? helpFillPaint : blackFillPaint);
        }

        private void breathLighting(Canvas lockHardwareCanvas, boolean staticMode) {
            makeBorderPaint(staticMode);
            lockHardwareCanvas.drawPath(this.path, this.borderPaint);
            lockHardwareCanvas.drawPath(invPath, showHelp ? helpFillPaint : blackFillPaint);
        }

        //IMPORTANT
        //This code was lost because Windows corrupted by ssd before I pushed my code to git
        //Because of this it's decompiled
        //TODO: Rewrite the code
        private void disappearanceLighting(Canvas canvas, boolean z) {
            Path path2;
            Path path3;
            Path path4;
            Path path5;
            float f;
            PathMeasure pathMeasure = new PathMeasure(this.path, true);
            RectF rectF = new RectF();
            this.path.computeBounds(rectF, true);
            float centerX = (rectF.centerX() + ((float) wallpaperSettings.getRadiusTop())) * 2.0f;
            float radiusBottom = (((float) this.surfaceHeight) + 2.0f) - ((float) wallpaperSettings.getRadiusBottom());
            float f2 = centerX / 2.0f;
            Path path6 = new Path();
            Path path7 = new Path();
            int i = this.disappearancePhase;
            if (i == 0) {
                if (this.next) {
                    if (this.wait >= 750) {
                        this.wait = 0;
                        this.next = false;
                    } else {
                        this.wait++;
                    }
                }
                float f4 = f2 - this.toAdd;
                f = f2;
                if (f4 >= 0.0f) {
                    pathMeasure.getSegment(f4, f, path6, true);
                    path5 = null;
                } else {
                    path5 = new Path();
                    pathMeasure.getSegment(0.0f, f, path5, true);
                    pathMeasure.getSegment(pathMeasure.getLength() - this.toRemove, pathMeasure.getLength(), path6, true);
                }
                if (this.toAdd + f >= centerX + ((float) this.surfaceHeight) + f) {
                    this.toAdd = 0.0f;
                    this.toRemove = 0.0f;
                    this.disappearancePhase++;
                }
                pathMeasure.getSegment(f, this.toAdd + f, path7, true);
                if (path5 != null) {
                    canvas.drawPath(path5, this.borderPaint);
                    if (!z) {
                        this.toRemove += (pathMeasure.getLength() * 0.1f) / 100.0f;
                    }
                }
                canvas.drawPath(path6, this.borderPaint);
                canvas.drawPath(path7, this.borderPaint);
                canvas.drawPath(this.invPath, showHelp ? this.helpFillPaint : this.blackFillPaint);

                if (!z) {
                    this.toAdd += (pathMeasure.getLength() * 0.1f) / 100.0f;
                }
            } else {
                if (i == 1) {
                    float f6 = f2 - this.toAdd;
                    if (f6 >= 0.0f) {
                        pathMeasure.getSegment(0.0f, f6, path6, true);
                        path4 = new Path();
                        pathMeasure.getSegment(centerX + radiusBottom + f2, pathMeasure.getLength(), path4, true);
                    } else {
                        pathMeasure.getSegment(centerX + radiusBottom + f2, pathMeasure.getLength() - this.toRemove, path6, true);
                        path4 = null;
                    }
                    float f7 = centerX + radiusBottom + f2;
                    pathMeasure.getSegment(this.toAdd + f2, f7, path7, true);
                    if (f2 + this.toAdd >= f7) {
                        this.toAdd = 0.0f;
                        this.toRemove = 0.0f;
                        this.next = true;
                        this.disappearancePhase++;
                    }
                    if (path4 != null) {
                        canvas.drawPath(path4, this.borderPaint);
                    } else if (!z) {
                        this.toRemove += (pathMeasure.getLength() * 0.1f) / 100.0f;
                    }
                    canvas.drawPath(path7, this.borderPaint);
                    canvas.drawPath(path6, this.borderPaint);
                    canvas.drawPath(this.invPath, showHelp ? this.helpFillPaint : this.blackFillPaint);

                    if (!z) {
                        this.toAdd += (pathMeasure.getLength() * 0.1f) / 100.0f;
                    }
                } else if (i == 2) {
                    if (this.next) {
                        if (this.wait >= 750) {
                            this.wait = 0;
                            this.next = false;
                        } else {
                            this.wait++;
                        }
                    }
                    float f8 = centerX + radiusBottom + f2;
                    if (this.toAdd + f8 < pathMeasure.getLength()) {
                        pathMeasure.getSegment(f8, this.toAdd + f8, path6, true);
                        path3 = null;
                    } else {
                        path3 = new Path();
                        pathMeasure.getSegment(f8, pathMeasure.getLength(), path3, true);
                        pathMeasure.getSegment(0.0f, this.toRemove, path6, true);
                    }
                    if (f8 - this.toAdd <= f2) {
                        this.toAdd = 0.0f;
                        this.toRemove = 0.0f;
                        this.disappearancePhase += 1;
                    }
                    pathMeasure.getSegment(f8 - this.toAdd, f8, path7, true);
                    if (path3 != null) {
                        canvas.drawPath(path3, this.borderPaint);
                        if (!z) {
                            this.toRemove += (pathMeasure.getLength() * 0.1f) / 100.0f;
                        }
                    }
                    canvas.drawPath(path6, this.borderPaint);
                    canvas.drawPath(path7, this.borderPaint);
                    canvas.drawPath(this.invPath, showHelp ? this.helpFillPaint : this.blackFillPaint);

                    if (!z) {
                        this.toAdd += (pathMeasure.getLength() * 0.1f) / 100.0f;
                    }
                } else if (i == 3) {
                    float f9 = centerX + radiusBottom + f2;
                    if (this.toAdd + f9 < pathMeasure.getLength()) {
                        path2 = new Path();
                        pathMeasure.getSegment(0.0f, f2, path2, true);
                        pathMeasure.getSegment(this.toAdd + f9, pathMeasure.getLength(), path6, true);
                    } else {
                        pathMeasure.getSegment(this.toRemove, f2, path6, true);
                        path2 = null;
                    }
                    pathMeasure.getSegment(f2, f9 - this.toAdd, path7, true);
                    if (f9 - this.toAdd <= f2) {
                        this.toAdd = 0.0f;
                        this.toRemove = 0.0f;
                        this.next = true;
                        this.disappearancePhase = 0;
                    }
                    if (path2 != null) {
                        canvas.drawPath(path2, this.borderPaint);
                    } else if (!z) {
                        this.toRemove += (pathMeasure.getLength() * 0.1f) / 100.0f;
                    }
                    canvas.drawPath(path7, this.borderPaint);
                    canvas.drawPath(path6, this.borderPaint);
                    canvas.drawPath(this.invPath, showHelp ? this.helpFillPaint : this.blackFillPaint);
                    if (!z) {
                        this.toAdd += (pathMeasure.getLength() * 0.1f) / 100.0f;
                    }
                }
            }
        }


        private void circuitLighting(Canvas lockHardwareCanvas, int nanoTime, boolean staticMode) {
            float pow = (float) (((double) nanoTime) / Math.pow(wallpaperSettings.getBorderSpeed() >= 90 ? 10 : 100F - ((float) wallpaperSettings.getBorderSpeed()), 1.3d));

            Matrix matrix2 = new Matrix();
            matrix2.preRotate(pow * 3, ((float) this.surfaceWidth) / 2.0f, ((float) this.surfaceHeight) / 2.0f);
            this.grad.setLocalMatrix(matrix2);
            this.borderPaint.setStrokeWidth(interpolation);

            PathMeasure measure = new PathMeasure(path, false);
            float length = measure.getLength();
            Path partialPath = new Path();

            if (start == -1 || end == -1) {
                this.start = length;
                this.end = length - (length * 4F / 100F);
            }

            if (end <= 0) {
                if (start > 0) {
                    Path endPath = new Path();
                    measure.getSegment(length + end, length, endPath, true);

                    Path startPath = new Path();
                    measure.getSegment(0, start, startPath, true);

                    lockHardwareCanvas.drawPath(startPath, this.borderPaint);
                    lockHardwareCanvas.drawPath(endPath, this.borderPaint);
                    lockHardwareCanvas.drawPath(invPath, showHelp ? helpFillPaint : blackFillPaint);
                } else {
                    this.start = length;
                    this.end = length - (length * 4F / 100F);
                }
            } else {
                measure.getSegment(end, start, partialPath, true);
                lockHardwareCanvas.drawPath(partialPath, this.borderPaint);
                lockHardwareCanvas.drawPath(invPath, showHelp ? helpFillPaint : blackFillPaint);
            }

            if (!staticMode) {
                start -= length * (0.1F * (wallpaperSettings.getBorderSpeed() / 25F)) / 100F;
                end -= length * (0.1F * (wallpaperSettings.getBorderSpeed() / 25F)) / 100F;
            }
        }

        private void makePath() {
            this.path = generatePath();
            this.invPath = new Path(path);
            invPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        }

        //IMPORTANT
        //This code was lost because Windows corrupted by ssd before I pushed my code to git
        //Because of this it's decompiled
        //TODO: Refactor the code
        private Path generatePath() {
            Path path = new Path();
            float f;
            float f2;
            float f3 = (float) wallpaperSettings.getRadiusTop();
            float f4 = (float) wallpaperSettings.getRadiusBottom();
            float f5 = (float) (wallpaperSettings.getNotchBottomFull() * 2);
            float f6 = (float) (this.surfaceWidth + 2);
            float f7 = (float) (this.surfaceHeight + 2);
            float f8 = f6 - (f3 + f3);
            path.moveTo(f6 - 1.0f, -1.0f + f3);
            float f9 = -f3;
            path.rQuadTo(0.0f, f9, f9, f9);
            if (this.rotated || !wallpaperSettings.isNotch()) {
                f = f6;
                f2 = 0.0f;
                path.rLineTo(-f8, 0.0f);
            } else {
                float f10 = (float) wallpaperSettings.getNotchHeight();
                float f11 = (float) wallpaperSettings.getNotchWidth();
                float f12 = (((float) wallpaperSettings.getNotchTop() / 100.0f) * f11);
                float f13 = f10 * 0.0f;
                float f14 = (((float) wallpaperSettings.getNotchBottom() / 100.0f) * f11);
                float f15 = -((f8 - ((f11 * 2.0f) + f5)) / 2.0f);
                path.rLineTo(f15, 0.0f);
                float f16 = -f13;
                f = f6;
                float f17 = -f11;
                path.rCubicTo(-f12, f13, f14 - f11, f16 + f10, f17, f10);
                path.rLineTo(-f5, 0.0f);
                path.rCubicTo(-f14, f16, f12 - f11, f13 - f10, f17, -f10);
                f2 = 0.0f;
                path.rLineTo(f15, 0.0f);
            }
            path.rQuadTo(f9, f2, f9, f3);
            float f18 = f7 - (f3 + f4);
            path.rLineTo(f2, f18);
            path.rQuadTo(f2, f4, f4, f4);
            path.rLineTo(f - (f4 + f4), f2);
            path.rQuadTo(f4, f2, f4, -f4);
            path.rLineTo(f2, -f18);
            path.close();
            return path;
        }

        private float unlockProgress() {
            return 1.0f;
        }

        @Override
        public void onVisibilityChanged(boolean z) {
            this.visible = z;
            if (z) {
                shouldRun = true;
                executor.execute(this);
            } else {
               shouldRun = false;
            }
        }


        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void run() {
                if (canPost()) {
                    if (getUpdate() != null) {
                        SharedPreferences.Editor preferences = getBaseContext().getSharedPreferences("Styders", Context.MODE_PRIVATE).edit();
                        switch (update) {
                            case BORDER_ENABLED:
                                preferences.putBoolean(BORDER_ENABLED, wallpaperSettings.isBorderEnabled());
                                break;
                            case NOTCH_ENABLED:
                                preferences.putBoolean(NOTCH_ENABLED, wallpaperSettings.isNotch());
                                break;
                            case TIMER_ENABLED:
                                preferences.putBoolean(TIMER_ENABLED, wallpaperSettings.isTimerEnabled());
                                break;
                            case NOTCH_WIDTH:
                                preferences.putInt(NOTCH_WIDTH, wallpaperSettings.getNotchWidth());
                                break;
                            case NOTCH_BOTTOM_FULL:
                                preferences.putInt(NOTCH_BOTTOM_FULL, wallpaperSettings.getNotchBottomFull());
                                break;
                            case NOTCH_TOP:
                                preferences.putInt(NOTCH_TOP, wallpaperSettings.getNotchTop());
                                break;
                            case NOTCH_BOTTOM:
                                preferences.putInt(NOTCH_BOTTOM, wallpaperSettings.getNotchBottom());
                                break;
                            case NOTCH_HEIGHT:
                                preferences.putInt(NOTCH_HEIGHT, wallpaperSettings.getNotchHeight());
                                break;
                            case STARTING_HOURS:
                                preferences.putInt(STARTING_HOURS, wallpaperSettings.getStartingHours());
                                break;
                            case STARTING_MINUTES:
                                preferences.putInt(STARTING_MINUTES, wallpaperSettings.getStartingMinutes());
                                break;
                            case ENDING_HOURS:
                                preferences.putInt(ENDING_HOURS, wallpaperSettings.getEndingHours());
                                break;
                            case ENDING_MINUTES:
                                preferences.putInt(ENDING_MINUTES, wallpaperSettings.getEndingMinutes());
                                break;
                            case TIMER_DAYS:
                                Set<String> set = new HashSet<>();
                                for (Day day : wallpaperSettings.getTimerDays()) {
                                    String name = day.name();
                                    set.add(name);
                                }

                                preferences.putStringSet(TIMER_DAYS, set);
                                break;
                            case TIMER_QUICK_OPTION:
                                preferences.putInt(TIMER_QUICK_OPTION, wallpaperSettings.getTimerQuickOption().ordinal());
                                break;
                            case ACTIVATE_CONDITIONS:
                                Set<String> result = new HashSet<>();
                                for (ToggleBorderOption toggleBorderOption : wallpaperSettings.getActivateLiveBorderOnlyWhen()) {
                                    result.add(String.valueOf(toggleBorderOption.ordinal()));
                                }

                                preferences.putStringSet(ACTIVATE_CONDITIONS, result);
                                break;
                            case STYLE:
                                preferences.putInt(STYLE, wallpaperSettings.getStydersStyle().ordinal());
                                break;
                            case RESTART:
                                preferences.putInt(RESTART, wallpaperSettings.getRestartOption().ordinal());
                                break;
                            case BACKGROUND_STYLE:
                                preferences.putInt(BACKGROUND_STYLE, wallpaperSettings.getBackgroundStyle().ordinal());
                                break;
                            case BORDER_STYLE:
                                preferences.putInt(BORDER_STYLE, wallpaperSettings.getBorderStyle().ordinal());
                                break;
                            case SEQUENCES:
                                preferences.putString(SEQUENCES, new Gson().toJson(wallpaperSettings.getColorSequences(), ColorSequence.class));
                                break;
                            case IMAGE_BRIGHTNESS:
                                preferences.putInt(IMAGE_BRIGHTNESS, wallpaperSettings.getImageBorderBrightness());
                                break;
                            case RADIUS_TOP:
                                preferences.putInt(RADIUS_TOP, wallpaperSettings.getRadiusTop());
                                break;
                            case RADIUS_BOTTOM:
                                preferences.putInt(RADIUS_BOTTOM, wallpaperSettings.getRadiusBottom());
                                break;
                            case BORDER_HOME:
                                preferences.putInt(BORDER_HOME, wallpaperSettings.getBorderSizeHomeScreen());
                                break;
                            case BORDER_SPEED:
                                preferences.putInt(BORDER_SPEED, wallpaperSettings.getBorderSpeed());
                                break;
                            case CHANGE:
                                this.borderPaint.setAlpha(0);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + update);
                        }

                        preferences.apply();
                        makeBorderPaint();
                        makePath();
                        update = null;
                    }

                    if(shouldRun) executor.execute(this);
                }
        }

        public synchronized void setShowHelp(boolean showHelp) {
            this.showHelp = showHelp;
        }

        public synchronized void setUpdate(String update) {
            this.update = update;
        }

        public synchronized String getUpdate() {
            return update;
        }
    }

    public Engine onCreateEngine() {
        engine = new CustomEngine();
        return engine;
    }

    public synchronized static void setUpdate(String update) {
        engine.setUpdate(update);
    }

    public static void setShowHelp(boolean showHelp) {
       engine.setShowHelp(showHelp);
    }
}