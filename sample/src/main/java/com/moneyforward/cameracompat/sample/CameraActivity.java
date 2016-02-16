package com.moneyforward.cameracompat.sample;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.moneyforward.cameracompat.CameraCompatCallback;
import com.moneyforward.cameracompat.CameraCompatFragment;
import com.moneyforward.cameracompat.CameraCompatFragmentFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by kuroda02 on 2016/01/05.
 */
public class CameraActivity extends AppCompatActivity implements CameraCompatCallback {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private CameraCompatFragment cameraFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        this.cameraFragment = CameraCompatFragmentFactory.getInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, (Fragment) cameraFragment)
                .commitAllowingStateLoss();
        this.cameraFragment.setCallbackListener(this);
        findViewById(R.id.btnShutter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFragment.takePicture(800, Bitmap.Config.ARGB_8888);
            }
        });
    }

    @Override
    public void takePicture(Bitmap bitmap) {
        saveFile(bitmap);
    }

    public void saveFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(null), new Date().getTime() + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            bitmap.recycle();
            Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void requestCameraPermission() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.request_permission)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        } else {
                            finish();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                .create()
                .show();
    }

    @Override
    public void showPermissionError() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.request_permission))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create()
                .show();
    }

}
