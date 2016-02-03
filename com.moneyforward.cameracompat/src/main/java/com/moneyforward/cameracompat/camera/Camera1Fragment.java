package com.moneyforward.cameracompat.camera;


import android.graphics.Bitmap;
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
import com.moneyforward.cameracompat.util.ImageUtil;

/**
 * CameraFragment for below Marshmallow
 * <p/>
 * Created by shoji.kuroda on 2015/12/21.
 */
public class Camera1Fragment extends Fragment implements CameraCompatFragment, View.OnClickListener, Camera.PictureCallback {

    private final String TAG = Camera1Fragment.class.getSimpleName();
    private Camera camera;
    private Camera1Preview cameraPreview;
    private CameraCompatCallback cameraCompatCallback;
    private ViewGroup cameraFrame;
    private boolean allowRetry = false;
    private int imageSizeMax;
    private Bitmap.Config config;
    private boolean isCameraActive = true;

    public static Camera1Fragment newInstance() {
        return new Camera1Fragment();
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

    /**
     * カメラプレビューを開く
     *
     * @return
     */
    private boolean safeCameraOpen() {
        boolean qOpened = false;
        try {
            releaseCameraAndPreview();
            this.camera = Camera.open();
            qOpened = (this.camera != null);
            if (qOpened) {
                this.camera.startPreview();
                isCameraActive = true;
            }
        } catch (Exception e) {
            Log.d(TAG, "failed to open Camera");
        }
        return qOpened;
    }

    /**
     * カメラプレビューを開放
     */
    private void releaseCameraAndPreview() {
        if (this.camera != null) {
            this.camera.setPreviewCallback(null);
            this.cameraPreview.getHolder().removeCallback(this.cameraPreview);
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
        }
    }

    /**
     * コールバック設定
     *
     * @param callback
     */
    @Override
    public void setCallbackListener(CameraCompatCallback callback) {
        this.cameraCompatCallback = callback;
    }

    /**
     * 連続撮影モードの設定
     *
     * @param allowRetry
     * @return
     */
    @Override
    public CameraCompatFragment setAllowRetry(boolean allowRetry) {
        this.allowRetry = allowRetry;
        return this;
    }

    /**
     * 画面クリック
     *
     * @param v
     */
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
                    Log.d(TAG, "onAutoFocus = " + success);
                }
            });
        }
    }

    /**
     * 撮影処理
     *
     * @param imageSizeMax 一編の最大pixel
     * @param config       画質
     */
    @Override
    public void takePicture(int imageSizeMax, Bitmap.Config config) {
        this.imageSizeMax = imageSizeMax;
        this.config = config;
        if (this.camera == null) {
            return;
        }
        if (isCameraActive) {
            this.camera.takePicture(null, null, this);
            isCameraActive = false;
        }
    }

    /**
     * 撮影完了コールバック
     *
     * @param bytes
     * @param camera
     */
    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        if (allowRetry) {
            camera.startPreview();
        }
        int degrees = ImageUtil.getCameraDisplayOrientation(getActivity());
        final Bitmap bitmap = ImageUtil.createBitmap(bytes, imageSizeMax, degrees, config);
        cameraCompatCallback.takePicture(bitmap);
        isCameraActive = true;
    }
}
