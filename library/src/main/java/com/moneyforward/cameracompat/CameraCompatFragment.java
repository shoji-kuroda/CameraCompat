package com.moneyforward.cameracompat;

/**
 * Created by kuroda02 on 2015/12/21.
 */
public interface CameraCompatFragment {

    void takePicture();

    void setCallbackListener(CameraCompatCallback listener);

    CameraCompatFragment setAllowRetry(boolean allowRetry);
}
