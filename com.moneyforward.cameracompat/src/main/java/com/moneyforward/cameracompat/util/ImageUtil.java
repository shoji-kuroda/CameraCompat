package com.moneyforward.cameracompat.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Surface;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kuroda02 on 2016/01/29.
 */
public class ImageUtil {

    /**
     * ExifのOrientationValueと回転角度のmap
     */
    private static final SparseIntArray EXIF_ORIENTATION_VALUE_ANGLE_MAP = new SparseIntArray() {
        {
            put(1, 0);      // "Top, left side (Horizontal / normal)"
            put(2, 0);      // "Top, right side (Mirror horizontal)"
            put(3, 180);    // "Bottom, right side (Rotate 180)"
            put(4, 0);      // "Bottom, left side (Mirror vertical)"
            put(5, 270);    // "Left side, top (Mirror horizontal and rotate 270 CW)"
            put(6, 90);     // "Right side, top (Rotate 90 CW)"
            put(7, 90);     // "Right side, bottom (Mirror horizontal and rotate 90 CW)"
            put(8, 270);    // "Left side, bottom (Rotate 270 CW)"
        }
    };

    private static final Pair<Integer, Integer> PRE_SCALE_MIRROR_NONE = new Pair<>(1, 1);
    private static final Pair<Integer, Integer> PRE_SCALE_MIRROR_HORIZONTAL = new Pair<>(-1, 1);
    private static final Pair<Integer, Integer> PRE_SCALE_MIRROR_VERTICAL = new Pair<>(1, -1);

    /**
     * ExifのOrientationValueと反転情報のMap
     */
    private static final SparseArray<Pair<Integer, Integer>> EXIF_ORIENTATION_VALUE_PRE_SCALE_MAP
            = new SparseArray<Pair<Integer, Integer>>() {
        {
            put(1, PRE_SCALE_MIRROR_NONE);
            put(2, PRE_SCALE_MIRROR_HORIZONTAL);
            put(3, PRE_SCALE_MIRROR_NONE);
            put(4, PRE_SCALE_MIRROR_VERTICAL);
            put(5, PRE_SCALE_MIRROR_HORIZONTAL);
            put(6, PRE_SCALE_MIRROR_NONE);
            put(7, PRE_SCALE_MIRROR_HORIZONTAL);
            put(8, PRE_SCALE_MIRROR_NONE);
        }
    };

    /**
     * 画像をリサイズ及び回転して取り込み
     *
     * @param data
     * @param imageSizeMax
     * @return
     */
    public static synchronized Bitmap createBitmap(byte[] data, int imageSizeMax, int degrees, Bitmap.Config config) {

        System.gc();

        // 回転確度
        degrees = degrees + extractTransformMatrixFromExifInfo(data);

        // 画像のサイズ取得
        BitmapFactory.Options bitmapSizeOptions = new BitmapFactory.Options();
        bitmapSizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, bitmapSizeOptions);

        BitmapFactory.Options bitmapDecodeOptions = new BitmapFactory.Options();
        bitmapDecodeOptions.inSampleSize = computeInSampleSize(bitmapSizeOptions, imageSizeMax);

        Bitmap decodeBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapDecodeOptions);
        data = null;

        // 縮小
        int decodeWidth = decodeBitmap.getWidth();
        int decodeHeight = decodeBitmap.getHeight();

        int scaleHeight;
        int scaleWidth;
        if (decodeHeight > decodeWidth) {
            scaleHeight = imageSizeMax;
            scaleWidth = (int) Math.floor(imageSizeMax *
                    ((float) decodeWidth / decodeHeight));
        } else {
            scaleHeight = (int) Math.floor(imageSizeMax *
                    ((float) decodeHeight / decodeWidth));
            scaleWidth = imageSizeMax;
        }
        float ratioHeight = (float) scaleHeight / decodeHeight;
        float ratioWidth = (float) scaleWidth / decodeWidth;

        Matrix matrix = new Matrix();
        matrix.setScale(ratioWidth, ratioHeight);
        matrix.postRotate(degrees);
        Bitmap adjustBitmap = Bitmap.createBitmap(decodeBitmap, 0, 0, decodeWidth, decodeHeight, matrix, false);
        decodeBitmap.recycle();
        System.gc();

        return adjustBitmap;
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

    private static int computeInSampleSize(BitmapFactory.Options options, int maxSize) {
        int inSampleSize = 1;

        final int srcHeight = options.outHeight;
        final int srcWidth = options.outWidth;

        int tmpWidth = srcWidth, tmpHeight = srcHeight;
        while (true) {
            if (tmpWidth / 2 < maxSize || tmpHeight / 2 < maxSize)
                break;
            tmpWidth /= 2;
            tmpHeight /= 2;
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    /**
     * ExifからOrientation情報に基づいた回転補正を行うためのMatrixを生成する
     *
     * @param data
     * @return Exifから取得できれば回転情報を含むMatrix。取得できなければNull
     */
    @Nullable
    private static int extractTransformMatrixFromExifInfo(byte[] data) {
        final InputStream stream = new ByteArrayInputStream(data);

        Integer exifOrientationValue;
        try {
            final Metadata metadata = JpegMetadataReader.readMetadata(new ByteArrayInputStream(data));
            final ExifIFD0Directory exif = (ExifIFD0Directory) metadata.getDirectoriesOfType(ExifIFD0Directory.class).toArray()[0];

            exifOrientationValue = exif.getInteger(ExifDirectoryBase.TAG_ORIENTATION);
            if (exifOrientationValue == null) {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
        return EXIF_ORIENTATION_VALUE_ANGLE_MAP.get(exifOrientationValue, 0);
    }
}
