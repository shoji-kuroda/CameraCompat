package com.moneyforward.cameracompat;

/**
 * Created by kuroda02 on 2015/12/24.
 */
public interface CameraCompatCallback {
    void takePicture(byte[] data);

    void requestCameraPermission();

    void showPermissionError();
}
