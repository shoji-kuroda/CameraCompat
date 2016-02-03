package com.moneyforward.cameracompat.camera2;


import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by shoji.kuroda on 2015/12/17.
 */
public class Camera2Preview extends TextureView {

    private int ratioWidth = 0;
    private int ratioHeight = 0;

    public Camera2Preview(Context context) {
        this(context, null);
    }

    public Camera2Preview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Camera2Preview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        this.ratioWidth = width;
        this.ratioHeight = height;
        requestLayout();
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
}
