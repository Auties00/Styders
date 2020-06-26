package it.auties.styders.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.cardview.widget.CardView;


public class InterceptCardView extends CardView {

    public InterceptCardView(Context context) {
        super(context);
    }

    public InterceptCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}