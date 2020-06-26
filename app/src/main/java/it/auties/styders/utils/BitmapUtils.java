package it.auties.styders.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.ByteArrayOutputStream;

public class BitmapUtils {
    static String bitmapToString(Bitmap in) {
        if (in == null) {
            return "";
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        in.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        return Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
    }

    static Bitmap stringToBitmap(String in) {
        byte[] bytes = Base64.decode(in, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static void applyBlur(final Context context, final View image, final View layout) {
        image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                image.getViewTreeObserver().removeOnPreDrawListener(this);
                image.buildDrawingCache();

                Bitmap bmp = image.getDrawingCache();

                Bitmap overlay = Bitmap.createBitmap((layout.getMeasuredWidth()), (layout.getMeasuredHeight()), Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(overlay);

                canvas.translate(-layout.getLeft(), -layout.getTop());
                canvas.drawBitmap(bmp, 0, 0, null);

                RenderScript rs = RenderScript.create(context);

                Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);

                ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                        rs, overlayAlloc.getElement());

                blur.setInput(overlayAlloc);

                blur.setRadius(20);

                blur.forEach(overlayAlloc);

                overlayAlloc.copyTo(overlay);

                layout.setBackground(new BitmapDrawable(context.getResources(), overlay));
                rs.destroy();
                return true;
            }
        });
    }
}
