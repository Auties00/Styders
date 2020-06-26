package it.auties.styders.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.ColorInt;
import androidx.viewpager.widget.ViewPager;

import it.auties.styders.main.MainActivity;

public class BlockedViewPager extends ViewPager {
    private boolean enabled;

    public BlockedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (MainActivity.getMainActivity().isTutorial()) {
            int gray = adjustAlpha(Color.parseColor("#242424"), 0.98F);

            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            Paint paint = new Paint();

            ColorFilter filter = new PorterDuffColorFilter(gray, PorterDuff.Mode.SRC_ATOP);
            paint.setColorFilter(filter);

            canvas.saveLayer(null, paint, Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            canvas.restore();
        } else {
            super.dispatchDraw(canvas);
        }
    }

    @ColorInt
    private int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
