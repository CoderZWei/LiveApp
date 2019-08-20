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

    public void initLivePush(String url) {
        if (!TextUtils.isEmpty(url)) {
            initPush(url);
        }
    }

    public void pushSPSPPS(byte[] sps, byte[] pps) {
        if (sps != null && pps != null) {
            pushSPSPPS(sps, sps.length, pps, pps.length);
        }
    }

    public void pushVideoData(byte[] data, boolean keyFrame) {
        if (data != null) {
            pushVideoData(data, data.length, keyFrame);
        }
    }

    private void onConnecting() {
        if (mPushConnectionListener != null) {
            mPushConnectionListener.onConnecting();
        }
    }

    private void onConnectSuccess() {
        if (mPushConnectionListener != null) {
            mPushConnectionListener.onConnectSuccess();
        }
    }

    private void onConnectionFailed(String msg) {
        if (mPushConnectionListener != null) {
            mPushConnectionListener.onConnectionFailed(msg);
        }
    }

    public native void initPush(String pushUrl);

    public native void pushSPSPPS(byte[] sps, int sps_len, byte[] pps, int pps_len);

    public native void pushVideoData(byte[] data, int data_len, boolean keyframe);
}
