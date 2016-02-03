package com.moneyforward.cameracompat.camera2;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

/**
 * Created by shoji.kuroda on 2015/12/17.
 */
public class Camera2Preview extends TextureView implements TextureView.SurfaceTextureListener {

    private int ratioWidth = 0;
    private int ratioHeight = 0;
    private Size previewSize;

    public Camera2Preview(Context context) {
        this(context, null);
    }

    public Camera2Preview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Camera2Preview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSurfaceTextureListener(this);
    }

    public void setPreviewSize(Size previewSize) {
        this.previewSize = previewSize;

        enterTheMatrix();
        requestLayout();
    }

    private void enterTheMatrix() {
    }

    public void setAspectRatio(int width, int height, int rotation) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        Matrix txform = new Matrix();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        RectF rectView = new RectF(0, 0, viewWidth, viewHeight);
        float viewCenterX = rectView.centerX();
        float viewCenterY = rectView.centerY();
        RectF rectPreview = new RectF(0, 0, height, width);
        float previewCenterX = rectPreview.centerX();
        float previewCenterY = rectPreview.centerY();

        if (Surface.ROTATION_90 == rotation ||
                Surface.ROTATION_270 == rotation) {
            rectPreview.offset(viewCenterX - previewCenterX,
                    viewCenterY - previewCenterY);

            txform.setRectToRect(rectView, rectPreview,
                    Matrix.ScaleToFit.FILL);

            float scale = Math.max((float) viewHeight / height,
                    (float) viewWidth / width);

            txform.postScale(scale, scale, viewCenterX, viewCenterY);
            txform.postRotate(90 * (rotation - 2), viewCenterX,
                    viewCenterY);
        } else {
            if (Surface.ROTATION_180 == rotation) {
                txform.postRotate(180, viewCenterX, viewCenterY);
            }
        }
        setTransform(txform);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == this.ratioWidth || 0 == this.ratioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * this.ratioWidth / this.ratioHeight) {
                setMeasuredDimension(width, width * this.ratioHeight / this.ratioWidth);
            } else {
                setMeasuredDimension(height * this.ratioWidth / this.ratioHeight, height);
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (previewSize != null) {
            setAspectRatio(previewSize.getWidth(),
                    previewSize.getHeight(),
                    ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
