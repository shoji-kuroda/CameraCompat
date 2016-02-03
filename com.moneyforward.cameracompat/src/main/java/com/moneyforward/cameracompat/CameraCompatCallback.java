package com.moneyforward.cameracompat;

import android.graphics.Bitmap;

/**
 * Created by shoji.kuroda on 2015/12/24.
 */
public interface CameraCompatCallback {
    void takePicture(Bitmap bitmap);

    void requestCameraPermission();

    void showPermissionError();
}
