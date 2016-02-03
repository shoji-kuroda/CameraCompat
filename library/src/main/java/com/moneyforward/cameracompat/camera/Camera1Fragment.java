package com.moneyforward.cameracompat.camera;


import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moneyforward.cameracompat.CameraCompatCallback;
import com.moneyforward.cameracompat.CameraCompatFragment;
import com.moneyforward.cameracompat.R;

/**
 * Created by kuroda02 on 2015/12/21.
 */
public class Camera1Fragment extends Fragment implements CameraCompatFragment, View.OnClickListener {

    private final String TAG = Camera1Fragment.class.getSimpleName();
    private Camera camera;
    private Camera1Preview cameraPreview;
    private CameraCompatCallback cameraCompatCallback;
    private ViewGroup cameraFrame;
    private boolean allowRetry = false;

    public static Camera1Fragment newInstance() {
        Bundle args = new Bundle();
        Camera1Fragment fragment = new Camera1Fragment();
        fragment.setArguments(args);
        fragment.setRetainInstance(true);
        return fragment;
    }

    public Camera1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (safeCameraOpen()) {
            this.cameraPreview = new Camera1Preview(getContext(), this.camera);
            if (this.cameraFrame.getChildCount() > 0 && this.cameraFrame.getChildAt(0) instanceof Camera1Preview) {
                this.cameraFrame.removeViewAt(0);
            }
            this.cameraFrame.addView(this.cameraPreview);
            this.cameraFrame.setOnClickListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.camera != null) {
            releaseCameraAndPreview();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        this.cameraFrame = (ViewGroup) rootView.findViewById(R.id.camera_frame);
        return rootView;
    }

    private boolean safeCameraOpen() {
        boolean qOpened = false;
        try {
            releaseCameraAndPreview();
            this.camera = Camera.open();
            qOpened = (this.camera != null);
            if (qOpened) {
                this.camera.startPreview();
            }
        } catch (Exception e) {
            Log.d(TAG, "failed to open Camera");
        }
        return qOpened;
    }

    private void releaseCameraAndPreview() {
        if (this.camera != null) {
            this.camera.setPreviewCallback(null);
            this.cameraPreview.getHolder().removeCallback(this.cameraPreview);
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
        }
    }

    @Override
    public void takePicture() {
        if (this.camera == null) {
            return;
        }
        this.camera.takePicture(null, null, this.pictureCallback);
    }

    @Override
    public void setCallbackListener(CameraCompatCallback callback) {
        this.cameraCompatCallback = callback;
    }

    @Override
    public CameraCompatFragment setAllowRetry(boolean allowRetry) {
        this.allowRetry = allowRetry;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camera_frame) {
            if (this.camera == null) {
                return;
            }
            this.camera.cancelAutoFocus();
            this.camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        }
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 連続撮影モードの時は再度プレビュー開始
            if (allowRetry) {
                camera.startPreview();
            }
            cameraCompatCallback.takePicture(data);
        }
    };
}
