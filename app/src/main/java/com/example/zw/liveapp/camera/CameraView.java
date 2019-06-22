package com.example.zw.liveapp.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;

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
        mCamera=new MyCamera();
        setRender(mCameraRender);
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
}
