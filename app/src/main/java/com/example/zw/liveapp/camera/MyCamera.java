package com.example.zw.liveapp.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.util.List;

public class MyCamera {
    private Camera mCamera;
    private Context mContext;
    private int width,height;
    private SurfaceTexture mSurfaceTexture;
    public MyCamera(Context context){
        this.mContext=context;
    }
    public void initCamera(SurfaceTexture surfaceTexture,int cameraType){
        this.mSurfaceTexture=surfaceTexture;
        DisplayMetrics displayMetrics=mContext.getResources().getDisplayMetrics();
        this.width=displayMetrics.widthPixels;
        this.height=displayMetrics.heightPixels;

        mCamera=Camera.open(cameraType);
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            Camera.Parameters parameters=mCamera.getParameters();
            parameters.setFlashMode("off");
            parameters.setPreviewFormat(ImageFormat.NV21);
            Camera.Size size=getFitSize(parameters.getSupportedPictureSizes());
            parameters.setPictureSize(size.width,size.height);

            size=getFitSize(parameters.getSupportedPreviewSizes());
            parameters.setPreviewSize(size.width,size.height);
//            parameters.setPictureSize(parameters.getSupportedPictureSizes().get(0).width,
//                    parameters.getSupportedPictureSizes().get(0).height);
//            parameters.setPreviewSize(parameters.getSupportedPreviewSizes().get(0).width,
//                    parameters.getSupportedPreviewSizes().get(0).height);
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
    private Camera.Size getFitSize(List<Camera.Size> sizes)
    {
        if(width < height)
        {
            int t = height;
            height = width;
            width = t;
        }

        for(Camera.Size size : sizes)
        {
            if(1.0f * size.width / size.height == 1.0f * width / height)
            {
                return size;
            }
        }
        return sizes.get(0);
    }

}

