package com.moneyforward.cameracompat;

import android.graphics.Bitmap;

/**
 * Created by shoji.kuroda on 2015/12/21.
 */
public interface CameraCompatFragment {

    void takePicture(int maxSize, Bitmap.Config config);

    void setCallbackListener(CameraCompatCallback listener);

    void setFlash(boolean enable);

    CameraCompatFragment setAllowRetry(boolean allowRetry);
}
