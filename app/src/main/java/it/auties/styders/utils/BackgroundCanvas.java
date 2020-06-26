package it.auties.styders.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BackgroundCanvas extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private boolean hasActiveHolder;

    private int sHeight;
    private int sWidth;

    public BackgroundCanvas(Context context) {
        super(context);
        initHolder(getHolder());
    }

    public BackgroundCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHolder(getHolder());
    }

    public BackgroundCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHolder(getHolder());
    }

    public BackgroundCanvas(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initHolder(getHolder());
    }

    private void initHolder(SurfaceHolder holder) {
        this.holder = holder;
        holder.addCallback(this);
    }

    public void draw(Path path, Paint paint) {
        try {
            if (!hasActiveHolder) {
                return;
            }

            Canvas canvas = holder.lockCanvas();
            if (canvas == null) {
                return;
            }

            canvas.drawPath(path, paint);

            holder.unlockCanvasAndPost(canvas);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.hasActiveHolder = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.sWidth = i1;
        this.sHeight = i2;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        System.out.println("boom");
        this.hasActiveHolder = false;
    }

    public void setHasActiveHolder(boolean hasActiveHolder) {
        this.hasActiveHolder = hasActiveHolder;
    }

    public boolean hasActiveHolder() {
        return hasActiveHolder;
    }

    public int getCustomHeight() {
        return sHeight;
    }

    public int getCustomWidth() {
        return sWidth;
    }
}
