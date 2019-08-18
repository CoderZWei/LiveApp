package com.example.zw.liveapp.push;


import android.text.TextUtils;

public class PushVideo {
    static {
        System.loadLibrary("native-lib");
    }

    public void initLivePush(String url){
        if(!TextUtils.isEmpty(url)){
            initPush(url);
        }
    }

    public native void initPush(String pushUrl);
}
