package it.auties.styders.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

public class SquareButtonView extends AppCompatImageButton {
    public SquareButtonView(Context context) {
        super(context);
    }

    public SquareButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }
}
