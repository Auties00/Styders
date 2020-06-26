package it.auties.styders.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class RoundedCornersImageView extends AppCompatImageView {
    private static final int PADDING = 8;
    private static final float STROKE_WIDTH = 5.0f;
    private final float[] corners = new float[]{
            80, 80,        // Top left radius in px
            80, 80,        // Top right radius in px
            0, 0,          // Bottom right radius in px
            0, 0           // Bottom left radius in px
    };
    private Paint mBorderPaint;

    public RoundedCornersImageView(Context context) {
        this(context, null);
    }

    public RoundedCornersImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setPadding(PADDING, PADDING, PADDING, PADDING);
    }

    public RoundedCornersImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBorderPaint();
    }

    private void initBorderPaint() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(Color.DKGRAY);
        mBorderPaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final Path path = new Path();
        RectF rect = new RectF(PADDING, PADDING, getWidth() - PADDING, getHeight() - PADDING);
        path.addRoundRect(rect, corners, Path.Direction.CW);
        canvas.drawPath(path, mBorderPaint);
        canvas.drawRect(PADDING, PADDING, getWidth() - PADDING, getHeight() - PADDING, mBorderPaint);
    }
}
