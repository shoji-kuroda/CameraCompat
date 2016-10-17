package com.moneyforward.cameracompat.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by shoji.kuroda on 2016/10/17.
 */

public class FeatureUtil {
    public static boolean hasFeatureAutoFocus(Context context) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        boolean hasAutoFocus = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
        return hasAutoFocus;
    }
}
