package it.auties.styders.utils;

import android.graphics.Bitmap;

public class ColorUtils {
    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        return newBitmap.getPixel(0, 0);
    }
}
