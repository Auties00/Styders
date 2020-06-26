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

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import it.auties.styders.main.MainActivity;

public class ClosableTabLayout extends TabLayout {
    public ClosableTabLayout(@NonNull Context context) {
        super(context);
    }

    public ClosableTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClosableTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
}
