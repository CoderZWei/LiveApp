package com.example.zw.liveapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.zw.liveapp.camera.CameraView;
import com.example.zw.liveapp.utils.PermissionUtils;

public class CameraPreviewActivity extends AppCompatActivity {
    private CameraView mCameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        PermissionUtils.requestPermissionsIfNeed(this);
        mCameraView=(CameraView)findViewById(R.id.cameraView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraView.onDestory();
    }
}
