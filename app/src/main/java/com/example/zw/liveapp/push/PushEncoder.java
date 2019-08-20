package com.example.zw.liveapp.push;

import android.content.Context;

import com.example.zw.liveapp.push.BasePushEncoder;

public class PushEncoder extends BasePushEncoder {
    private PushEncoderRender mPushEncoderRender;
    public PushEncoder(Context context,int textureId) {
        super(context);
        mPushEncoderRender=new PushEncoderRender(context,textureId);
        setRender(mPushEncoderRender);
        setRenderMode(BasePushEncoder.RENDERMODE_CONTINUOUSLY);
    }
}
