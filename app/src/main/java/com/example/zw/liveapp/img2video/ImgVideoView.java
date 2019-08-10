package com.example.zw.liveapp.img2video;

import android.content.Context;
import android.util.AttributeSet;

import com.example.zw.liveapp.egl.MyEGLSurfaceView;

public class ImgVideoView extends MyEGLSurfaceView {
    private ImgVideoRender mImgVideoRender;
    private int fboTextureId;

    public ImgVideoView(Context context) {
        this(context,null);
    }

    public ImgVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ImgVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mImgVideoRender=new ImgVideoRender(context);
        setRender(mImgVideoRender);
        setRenderMode(MyEGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mImgVideoRender.setOnRenderCreateListener(new ImgVideoRender.OnRenderCreateListener() {
            @Override
            public void onCreate(int textId) {
                fboTextureId=textId;
            }
        });
    }

    public void setCurrentImg(int imgSr){
        if(mImgVideoRender!=null){
            mImgVideoRender.setCurrentImgSrc(imgSr);
            requestRender();
        }
    }

    public int getFboTextureId(){
        return fboTextureId;
    }
}
