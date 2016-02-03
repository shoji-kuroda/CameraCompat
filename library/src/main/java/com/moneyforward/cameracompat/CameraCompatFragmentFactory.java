package com.moneyforward.cameracompat;


import android.os.Build;

import com.moneyforward.cameracompat.camera.Camera1Fragment;
import com.moneyforward.cameracompat.camera2.Camera2Fragment;

/**
 * Created by kuroda02 on 2015/12/21.
 */
public class CameraCompatFragmentFactory {

    public static CameraCompatFragment getInstance() {
        CameraCompatFragment fragment;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fragment = Camera1Fragment.newInstance();
        } else {
            fragment = Camera2Fragment.newInstance();
        }
        fragment.setAllowRetry(true);

        return fragment;
    }
}
