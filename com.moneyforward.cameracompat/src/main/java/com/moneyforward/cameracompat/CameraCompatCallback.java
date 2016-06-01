package com.moneyforward.cameracompat;

import android.graphics.Bitmap;

/**
 * Created by shoji.kuroda on 2015/12/24.
 */
public interface CameraCompatCallback {

    enum FocusState {
        STARTED,
        FINISHED,
        CANCELED
    }

    void takePicture(Bitmap bitmap);

    void requestCameraPermission();

    void onFocusStateChanged(FocusState state);

    void showPermissionError();
}
