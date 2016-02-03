package com.moneyforward.cameracompat.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.Surface;

/**
 * Created by kuroda02 on 2016/01/29.
 */
public class ImageUtil {

    /**
     * 画像をリサイズして取り込み
     *
     * @param data
     * @param imageSizeMax
     * @return
     */
    public static Bitmap createBitmap(byte[] data, int imageSizeMax, int degrees, Bitmap.Config config) {

        Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        int scaleHeight;
        int scaleWidth;
        if (originalHeight > originalWidth) {
            scaleHeight = imageSizeMax;
            scaleWidth = (int) Math.floor(imageSizeMax *
                    ((float) originalWidth / originalHeight));
        } else {
            scaleHeight = (int) Math.floor(imageSizeMax *
                    ((float) originalHeight / originalWidth));
            scaleWidth = imageSizeMax;
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, scaleWidth, scaleHeight, true);
        original.recycle();

        int rotatedWidth, rotatedHeight;
        if (degrees % 180 == 0) {
            rotatedWidth = (int) Math.floor(scaleWidth);
            rotatedHeight = (int) Math.floor(scaleHeight);
        } else {
            rotatedWidth = (int) Math.floor(scaleHeight);
            rotatedHeight = (int) Math.floor(scaleWidth);
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(rotatedWidth, rotatedHeight, config);
        Canvas canvas = new Canvas(rotatedBitmap);
        canvas.save();
        canvas.rotate(degrees, rotatedWidth / 2, rotatedHeight / 2);
        int offset = (rotatedHeight - rotatedWidth) / 2 * ((degrees - 180) % 180) / 90;
        canvas.translate(offset, -offset);
        canvas.drawBitmap(scaledBitmap, 0, 0, null);
        canvas.restore();
        scaledBitmap.recycle();
        return rotatedBitmap;

    }

    /**
     * 端末の向きを取得する
     *
     * @param activity
     * @return
     */
    public static int getCameraDisplayOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        return (90 + 360 - degrees) % 360;
    }
}
