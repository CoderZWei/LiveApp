package com.example.zw.liveapp.push;


import android.text.TextUtils;

public class PushVideo {
    static {
        System.loadLibrary("native-lib");
    }

    private PushConnectionListener mPushConnectionListener;
    public void setPushConnectionListener(PushConnectionListener pushConnectionListener) {
        this.mPushConnectionListener = pushConnectionListener;
    }

    public void initLivePush(String url){
        if(!TextUtils.isEmpty(url)){
            initPush(url);
        }
    }

    private void onConnecting(){
        if(mPushConnectionListener!=null){
            mPushConnectionListener.onConnecting();
        }
    }

    private void onConnectSuccess(){
        if(mPushConnectionListener!=null){
            mPushConnectionListener.onConnectSuccess();
        }
    }

    private void onConnectionFailed(String msg){
        if(mPushConnectionListener!=null){
            mPushConnectionListener.onConnectionFailed(msg);
        }
    }

    public native void initPush(String pushUrl);
}
