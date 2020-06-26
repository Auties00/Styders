package it.auties.styders.wallpaper;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.animation.OvershootInterpolator;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import it.auties.styders.R;
import it.auties.styders.background.BorderStyle;
import it.auties.styders.background.ShowState;
import it.auties.styders.background.WallpaperSettings;
import it.auties.styders.utils.ColorUtils;

public class WallPaperService extends WallpaperService {
    private static boolean update;
    private static boolean showHelp;

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
        private Handler handler;
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
        private WallpaperSettings settings;
        private float interpolation;

        private float lerp(float f, float f2, float f3) {
            return f + ((f2 - f) * f3);
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            setTouchEventsEnabled(false);

            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
            this.index = 0;
            this.handler = new Handler();
            this.percentageForAlpha = 0;
            update = true;
            this.start = -1;
            this.end = -1;
            this.toAdd = 0;
            this.toRemove = 0;
            this.disappearancePhase = 0;
            this.last = System.currentTimeMillis();
            this.next = false;
            this.ms = 0;
            this.settings = WallpaperSettings.getInstance(getApplicationContext().getFilesDir());
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
            if (settings == null) {
                this.settings = WallpaperSettings.getInstance(getApplicationContext().getFilesDir());
            }

            this.borderPaint = new Paint(1);
            this.borderPaint.setStyle(Style.STROKE);
            this.borderPaint.setColor(-1);
            this.imagePaint = new Paint();
            this.imagePaint.setColor(-1);
            this.imagePaint.setFilterBitmap(true);
            makeBorderPaint();
            makePath(settings);

            this.helpFillPaint = new Paint(1);
            this.helpFillPaint.setStyle(Paint.Style.FILL);
            this.helpFillPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.helping_blue));

            this.blackFillPaint = new Paint(1);
            blackFillPaint.setStyle(Paint.Style.FILL);
            blackFillPaint.setColor(ColorUtils.getDominantColor(settings.getBackground()));

            handler.post(this);
        }

        private void makeBorderPaint(WallpaperSettings settings, boolean staticMode) {
            long time = 5000L - (settings.getBorderSpeed() * 5);
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
                int[] colorArray = settings.getColorSequences().getSequenceInUse().getColors();
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
            int[] colorArray = settings.getColorSequences().getSequenceInUse().getColors();
            int[] iArr = new int[(colorArray.length + 1)];

            for (int x = 0; x < 7; x++) {
                iArr[x] = colorArray[x != 6 ? x : 0];
            }

            this.grad = new SweepGradient(((float) this.surfaceWidth) / 2.0f, ((float) this.surfaceHeight) / 2.0f, iArr, null);
            this.borderPaint.setShader(this.grad);

            borderPaint.setAlpha(settings.getImageBorderBrightness() * 255 / 100);
        }


        private void notification(WallpaperSettings wallpaperSettings) {
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
                wallpaperSettings.setShowState(ShowState.HIDDEN);
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
            WallpaperSettings wallpaperSettings = settings;

            BorderStyle style = settings.getBorderStyle();

            Canvas lockHardwareCanvas = this.holder.getSurface().lockHardwareCanvas();
            if (this.rotated) {
                Matrix matrix = new Matrix();
                matrix.preRotate(-90.0f);
                matrix.postTranslate(0.0f, (float) this.surfaceWidth);
                lockHardwareCanvas.setMatrix(matrix);
            }

            wallpaperSettings.checkTime(true);
            wallpaperSettings.adjustState(getApplicationContext());

            long showTime = 0;
            int nanoTime = (int) ((System.nanoTime() - (showTime + 300000000)) / 1000000);

            if (wallpaperSettings.isBorderEnabled() && !wallpaperSettings.isHiddenEnable() && !wallpaperSettings.isTimerHidden()) {
                interpolation = new OvershootInterpolator().getInterpolation(((float) Math.max(Math.min(nanoTime, 1000), 0)) / 1000.0f) * (lerp((float) wallpaperSettings.getBorderSizeHomeScreen() < 10 ? 10 : wallpaperSettings.getBorderSizeHomeScreen() / 5F, (float) wallpaperSettings.getBorderSizeHomeScreen() < 10 ? 10 : wallpaperSettings.getBorderSizeHomeScreen() / 2F, unlockProgress()));
            } else {
                if (wallpaperSettings.getShowState() != null && wallpaperSettings.getShowState() != ShowState.HIDDEN) {
                    interpolation = new OvershootInterpolator().getInterpolation(((float) Math.max(Math.min(nanoTime, 1000), 0)) / 1000.0f) * (lerp((float) wallpaperSettings.getBorderSizeHomeScreen() < 10 ? 10 : wallpaperSettings.getBorderSizeHomeScreen() / 5F, (float) wallpaperSettings.getBorderSizeHomeScreen() < 10 ? 10 : wallpaperSettings.getBorderSizeHomeScreen() / 2F, unlockProgress()));
                } else {
                    interpolation = 0F;
                }
            }


            lockHardwareCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

            if (wallpaperSettings.isNewColor()) {
                if (style == BorderStyle.STATIC_LIGHTING) {
                    if (settings.getLastBorder() == BorderStyle.BREATH_LIGHTING) {
                        makeBorderPaint(wallpaperSettings, true);
                    } else {
                        makeBorderPaint();
                    }
                } else {
                    if (style == BorderStyle.BREATH_LIGHTING) {
                        makeBorderPaint(wallpaperSettings, false);
                    } else {
                        makeBorderPaint();
                    }
                }

                wallpaperSettings.setNewColor(false);
            }

            if (wallpaperSettings.getShowState() != null) {
                if (wallpaperSettings.getShowState() == ShowState.APPEAR) {
                    int[] colorArray = settings.getColorSequences().getSequenceInUse().getColors();
                    if (index > colorArray.length - 1) {
                        index = 0;
                    }

                    int[] iArr = new int[2];
                    iArr[0] = colorArray[index];
                    iArr[1] = colorArray[index];

                    this.grad = new SweepGradient(((float) this.surfaceWidth) / 2.0f, ((float) this.surfaceHeight) / 2.0f, iArr, null);

                    this.borderPaint.setShader(this.grad);
                }
            }

            if (wallpaperSettings.isNewImage()) {
                wallpaperSettings.setNewImage(false);
            }


            try {
                lockHardwareCanvas.drawBitmap(wallpaperSettings.getBackground(), null, lockHardwareCanvas.getClipBounds(), this.imagePaint);
            } catch (Exception e) {
                Log.d("[DRAW]", "Probably caught an error!");
                Log.d("[EX]", Objects.requireNonNull(e.getMessage()));
            }

            if (update) {
                makePath(settings);
                fakeDraw(wallpaperSettings, nanoTime, interpolation);
                update = false;
            }

            blackFillPaint.setColor(ColorUtils.getDominantColor(settings.getBackground()));
            if (wallpaperSettings.getShowState() != null && wallpaperSettings.getShowState() != ShowState.HIDDEN) {
                standardDraw(wallpaperSettings, lockHardwareCanvas, nanoTime, false);
                if (wallpaperSettings.getShowState() == ShowState.APPEAR) {
                    notification(wallpaperSettings);
                }

            } else {
                if (style == BorderStyle.STATIC_LIGHTING) {
                    BorderStyle borderStyle = settings.getLastBorder();
                    if (borderStyle == null) {
                        settings.setLastBorder(BorderStyle.CONTINUOUS_LIGHTING);
                    }

                    if (borderStyle == BorderStyle.CONTINUOUS_LIGHTING) {
                        standardDraw(wallpaperSettings, lockHardwareCanvas, nanoTime, true);
                    } else if (borderStyle == BorderStyle.BREATH_LIGHTING) {
                        breathLighting(wallpaperSettings, lockHardwareCanvas, true);
                    } else if (borderStyle == BorderStyle.DISAPPEARANCE_LIGHTING) {
                        disappearanceLighting(wallpaperSettings, lockHardwareCanvas, true);
                    } else if (borderStyle == BorderStyle.CIRCUIT_LIGHTING) {
                        circuitLighting(wallpaperSettings, lockHardwareCanvas, nanoTime, true);
                    }
                } else {
                    if (style == BorderStyle.CONTINUOUS_LIGHTING) {
                        standardDraw(wallpaperSettings, lockHardwareCanvas, nanoTime, false);
                    } else if (style == BorderStyle.BREATH_LIGHTING) {
                        breathLighting(wallpaperSettings, lockHardwareCanvas, false);
                    } else if (style == BorderStyle.DISAPPEARANCE_LIGHTING) {
                        disappearanceLighting(wallpaperSettings, lockHardwareCanvas, false);
                    } else if (style == BorderStyle.CIRCUIT_LIGHTING) {
                        circuitLighting(wallpaperSettings, lockHardwareCanvas, nanoTime, false);
                    }
                }
            }
        }

        private void fakeDraw(WallpaperSettings wallpaperSettings, int nanoTime, float interpolation) {
            float pow = (float) (((double) nanoTime) / Math.pow((double) (wallpaperSettings.getBorderSpeed() >= 90 ? 10 : 100F - ((float) wallpaperSettings.getBorderSpeed())), 1.3d));
            Matrix matrix2 = new Matrix();
            matrix2.preRotate(pow * 3, ((float) this.surfaceWidth) / 2.0f, ((float) this.surfaceHeight) / 2.0f);
            this.grad.setLocalMatrix(matrix2);
            this.borderPaint.setStrokeWidth(interpolation);
        }

        private void standardDraw(WallpaperSettings wallpaperSettings, Canvas lockHardwareCanvas, int nanoTime, boolean staticMode) {
            if (!staticMode) {
                float pow = (float) (((double) nanoTime) / Math.pow((double) (wallpaperSettings.getBorderSpeed() >= 90 ? 10 : 100F - ((float) wallpaperSettings.getBorderSpeed())), 1.3d));
                this.standardMatrix = new Matrix();
                standardMatrix.preRotate(pow * 3, ((float) this.surfaceWidth) / 2.0f, ((float) this.surfaceHeight) / 2.0f);
            }

            this.grad.setLocalMatrix(standardMatrix);
            this.borderPaint.setStrokeWidth(interpolation);
            if (interpolation > 0.001f) {
                lockHardwareCanvas.drawPath(this.path, this.borderPaint);
            }

            lockHardwareCanvas.drawPath(invPath, showHelp ? helpFillPaint : blackFillPaint);
            this.holder.getSurface().unlockCanvasAndPost(lockHardwareCanvas);
        }

        private void breathLighting(WallpaperSettings wallpaperSettings, Canvas lockHardwareCanvas, boolean staticMode) {
            makeBorderPaint(wallpaperSettings, staticMode);


            lockHardwareCanvas.drawPath(this.path, this.borderPaint);
            lockHardwareCanvas.drawPath(invPath, showHelp ? helpFillPaint : blackFillPaint);
            this.holder.getSurface().unlockCanvasAndPost(lockHardwareCanvas);
        }

        private void disappearanceLighting(WallpaperSettings wallpaperSettings, Canvas canvas, boolean z) {
            boolean z2;
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
                canvas.drawPath(this.invPath, WallPaperService.showHelp ? this.helpFillPaint : this.blackFillPaint);

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
                    canvas.drawPath(this.invPath, WallPaperService.showHelp ? this.helpFillPaint : this.blackFillPaint);

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
                    canvas.drawPath(this.invPath, WallPaperService.showHelp ? this.helpFillPaint : this.blackFillPaint);

                    if (!z) {
                        this.toAdd += (pathMeasure.getLength() * 0.1f) / 100.0f;
                    }
                } else if (i == 3) {
                    float f9 = centerX + radiusBottom + f2;
                    if (this.toAdd + f9 < pathMeasure.getLength()) {
                        path2 = new Path();
                        z2 = true;
                        pathMeasure.getSegment(0.0f, f2, path2, true);
                        pathMeasure.getSegment(this.toAdd + f9, pathMeasure.getLength(), path6, true);
                    } else {
                        z2 = true;
                        pathMeasure.getSegment(this.toRemove, f2, path6, true);
                        path2 = null;
                    }
                    pathMeasure.getSegment(f2, f9 - this.toAdd, path7, z2);
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
                    canvas.drawPath(this.invPath, WallPaperService.showHelp ? this.helpFillPaint : this.blackFillPaint);
                    if (!z) {
                        this.toAdd += (pathMeasure.getLength() * 0.1f) / 100.0f;
                    }
                }
            }

            this.holder.getSurface().unlockCanvasAndPost(canvas);
        }


        private void circuitLighting(WallpaperSettings settings, Canvas lockHardwareCanvas, int nanoTime, boolean staticMode) {
            float pow = (float) (((double) nanoTime) / Math.pow((double) (settings.getBorderSpeed() >= 90 ? 10 : 100F - ((float) settings.getBorderSpeed())), 1.3d));

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
                start -= length * (0.1F * (settings.getBorderSpeed() / 25F)) / 100F;
                end -= length * (0.1F * (settings.getBorderSpeed() / 25F)) / 100F;
            }

            this.holder.getSurface().unlockCanvasAndPost(lockHardwareCanvas);
        }

        private void makePath(WallpaperSettings wallpaperSettings) {
            this.path = generatePath(wallpaperSettings);
            this.invPath = new Path(path);
            invPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        }

        private Path generatePath(WallpaperSettings wallpaperSettings) {
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
                Log.d("[F13]", "F: " + f13);
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
                handler.post(this);
            } else {
                handler.removeCallbacks(this);
            }
        }


        public void onDestroy() {
            super.onDestroy();
        }

        public WallpaperSettings getSettings() {
            return settings;
        }

        @Override
        public void run() {
            if (canPost()) {
                handler.post(this);
            }
        }
    }

    public Engine onCreateEngine() {
        return new CustomEngine();
    }

    public static void setUpdate(boolean update) {
        WallPaperService.update = update;
    }

    public static void setShowHelp(boolean showHelp) {
        WallPaperService.showHelp = showHelp;
    }
}