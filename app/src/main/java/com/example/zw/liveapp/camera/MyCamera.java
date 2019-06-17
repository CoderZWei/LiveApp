package com.example.zw.liveapp.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;

public class MyCamera {
    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;
    public void initCamera(SurfaceTexture surfaceTexture,int cameraType){
        this.mSurfaceTexture=surfaceTexture;
        mCamera=Camera.open(cameraType);
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            Camera.Parameters parameters=mCamera.getParameters();
            parameters.setFlashMode("off");
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setPictureSize(parameters.getSupportedPictureSizes().get(0).width,
                    parameters.getSupportedPictureSizes().get(0).height);
            parameters.setPreviewSize(parameters.getSupportedPreviewSizes().get(0).width,
                    parameters.getSupportedPreviewSizes().get(0).height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopCamera(){
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
    }
}

