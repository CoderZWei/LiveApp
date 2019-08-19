package com.example.zw.liveapp.push;

public interface PushConnectionListener {
    void onConnecting();

    void onConnectSuccess();

    void onConnectionFailed(String msg);
}
