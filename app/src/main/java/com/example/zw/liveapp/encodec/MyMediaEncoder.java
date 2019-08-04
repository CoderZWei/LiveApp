package com.example.zw.liveapp.encodec;

import android.content.Context;

public class MyMediaEncoder extends BaseMediaEncoder{
    private EncoderRender mEncoderRender;

    public MyMediaEncoder(Context context,int textureId) {
        super(context);
        mEncoderRender=new EncoderRender(context,textureId);
        setRender(mEncoderRender);
        setRenderMode(BaseMediaEncoder.RENDERMODE_CONTINUOUSLY);
    }
}
