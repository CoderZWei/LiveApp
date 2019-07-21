package com.example.zw.liveapp.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.WindowManager;

import com.example.zw.liveapp.CameraPreviewActivity;
import com.example.zw.liveapp.egl.MyEGLSurfaceView;

public class CameraView extends MyEGLSurfaceView {
    private CameraRender mCameraRender;
    private MyCamera mCamera;
    private int cameraType=Camera.CameraInfo.CAMERA_FACING_BACK;

    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCameraRender=new CameraRender(context);
        mCamera=new MyCamera(context);
        setRender(mCameraRender);
        previewAngle(context);
        mCameraRender.setOnSurfaceCreateListener(new CameraRender.OnSurfaceCreateListener() {
            @Override
            public void OnSurfaceCreate(SurfaceTexture surfaceTexture) {
                mCamera.initCamera(surfaceTexture,cameraType);
            }
        });
    }
    public void onDestory(){
        if(mCamera!=null){
            mCamera.stopCamera();
        }
    }

    public void previewAngle(Context context) {
        int angle=((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        mCameraRender.resetMatrix();
        switch (angle){
            case Surface.ROTATION_0:
                if(cameraType==Camera.CameraInfo.CAMERA_FACING_BACK){
                    mCameraRender.setAngle(90, 0, 0, 1);
                    mCameraRender.setAngle(180, 1, 0, 0);
                }else {
                    mCameraRender.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_90:
                if(cameraType==Camera.CameraInfo.CAMERA_FACING_BACK){
                    mCameraRender.setAngle(180, 0, 0, 1);
                    mCameraRender.setAngle(180, 0, 1, 0);
                }else {
                    mCameraRender.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_180:
                if(cameraType==Camera.CameraInfo.CAMERA_FACING_BACK){
                    mCameraRender.setAngle(90f, 0.0f, 0f, 1f);
                    mCameraRender.setAngle(180f, 0.0f, 1f, 0f);
                }else {
                    mCameraRender.setAngle(-90, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_270:
                if(cameraType==Camera.CameraInfo.CAMERA_FACING_BACK){
                    mCameraRender.setAngle(180f, 0.0f, 1f, 0f);
                }else {
                    mCameraRender.setAngle(0f, 0f, 0f, 1f);
                }
                break;
        }
    }
}
