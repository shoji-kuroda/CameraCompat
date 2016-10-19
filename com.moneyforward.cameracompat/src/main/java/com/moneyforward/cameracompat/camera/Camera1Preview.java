package com.moneyforward.cameracompat.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * Camera Preview
 * <p/>
 * Created by shoji.kuroda on 2015/12/18.
 */
public class Camera1Preview extends ViewGroup implements SurfaceHolder.Callback {

    private final String TAG = Camera1Preview.class.getSimpleName();
    private final Activity activity;
    private Camera.Size previewSize;
    private List<Camera.Size> supportedPreviewSizes;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private List<String> supportedFlashModes;
    private Camera camera;
    private Camera.Size pictureSize;

    public Camera1Preview(Activity activity, Camera camera) {
        super(activity);
        this.activity = activity;
        setCamera(camera);
        this.surfaceView = new SurfaceView(activity);
        addView(surfaceView, 0);
        this.holder = this.surfaceView.getHolder();
        this.holder.addCallback(this);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.holder.setKeepScreenOn(true);
    }

    public SurfaceHolder getHolder() {
        return holder;
    }

    /**
     * カメラセット
     *
     * @param camera
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
        if (this.camera != null) {
            this.supportedPreviewSizes = this.camera.getParameters().getSupportedPreviewSizes();
            this.supportedFlashModes = this.camera.getParameters().getSupportedFlashModes();
            // Set the camera to Auto Flash mode.
            if (this.supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = this.camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                this.camera.setParameters(parameters);
            }
        }
        requestLayout();
    }

    /**
     * Viewが生成されたときのイベント
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (this.camera != null) {
                this.camera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    /**
     * Viewの状態が変わったときのイベント
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (this.holder.getSurface() == null)
            return;
        try {
            this.camera.stopPreview();
        } catch (Exception ignored) {
        }

        try {
            Camera.Parameters params = this.camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            if (this.previewSize != null) {
                params.setPreviewSize(this.previewSize.width, this.previewSize.height);
                List<Camera.Size> supportedPictureSizes
                        = camera.getParameters().getSupportedPictureSizes();
                List<Camera.Size> supportedPreviewSizes
                        = camera.getParameters().getSupportedPreviewSizes();
                if (supportedPictureSizes != null &&
                        supportedPreviewSizes != null &&
                        supportedPictureSizes.size() > 0 &&
                        supportedPreviewSizes.size() > 0) {

                    WindowManager windowManager = activity.getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    display.getMetrics(displayMetrics);

                    //撮影サイズを設定する
                    this.pictureSize = supportedPictureSizes.get(0);
                    params.setPictureSize(pictureSize.width, pictureSize.height);
                    params.setPreviewSize(previewSize.width, previewSize.height);
                }
                this.camera.setParameters(params);
                this.camera.setPreviewDisplay(holder);
                this.camera.startPreview();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * Viewが破棄されたときのイベント
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.camera != null) {
            this.camera.stopPreview();
        }
    }

    /**
     * プレビューサイズの決定
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        if (width == 0 || height == 0) {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);

        if (this.supportedPreviewSizes != null) {
            this.previewSize = getOptimalPreviewSize(this.supportedPreviewSizes, width, height);
        }
    }

    /**
     * プレビュー比率の決定
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }
        final View cameraView = getChildAt(0);

        final int width = right - left;
        final int height = bottom - top;

        int previewWidth = width;
        int previewHeight = height;
        if (this.previewSize != null) {
            Display display = ((WindowManager) this.activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            switch (display.getRotation()) {
                case Surface.ROTATION_0:
                    previewWidth = this.previewSize.height;
                    previewHeight = this.previewSize.width;
                    this.camera.setDisplayOrientation(90);
                    break;
                case Surface.ROTATION_90:
                    previewWidth = this.previewSize.width;
                    previewHeight = this.previewSize.height;
                    break;
                case Surface.ROTATION_180:
                    previewWidth = this.previewSize.height;
                    previewHeight = this.previewSize.width;
                    break;
                case Surface.ROTATION_270:
                    previewWidth = this.previewSize.width;
                    previewHeight = this.previewSize.height;
                    this.camera.setDisplayOrientation(180);
                    break;
            }
        }

        if (width * previewHeight > height * previewWidth) {
            final int scaledChildWidth = previewWidth * height / previewHeight;
            cameraView.layout((width - scaledChildWidth) / 2, 0,
                    (width + scaledChildWidth) / 2, height);
        } else {
            final int scaledChildHeight = previewHeight * width / previewWidth;
            cameraView.layout(0, (height - scaledChildHeight) / 2,
                    width, (height + scaledChildHeight) / 2);
        }
//        final int scaledChildHeight = previewHeight * width / previewWidth;
//        int padding = (height - scaledChildHeight) / 2;
//        cameraView.layout(0, padding, width, height - padding);
    }

    /**
     * 最適な画面サイズを取得
     *
     * @param sizes
     * @param width
     * @param height
     * @return
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
