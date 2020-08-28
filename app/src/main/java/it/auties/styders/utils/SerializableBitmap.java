package it.auties.styders.utils;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;

public class SerializableBitmap {
    @Expose(serialize = false, deserialize = false)
    private Bitmap bitmap;
    @Expose
    private final String asString;

    public SerializableBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.asString = BitmapUtils.bitmapToString(bitmap);
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            this.bitmap = BitmapUtils.stringToBitmap(asString);
        }

        return bitmap;
    }
}
