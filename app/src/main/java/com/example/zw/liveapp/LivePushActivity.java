package com.example.zw.liveapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.zw.liveapp.push.PushConnectionListener;
import com.example.zw.liveapp.push.PushVideo;

public class LivePushActivity extends AppCompatActivity {
    private Button btnStartPush;
    private PushVideo mPushVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_push);
        btnStartPush=(Button)findViewById(R.id.btn_startPush);
        btnStartPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPushVideo.initLivePush("rtmp://10.135.104.71/");
            }
        });
        mPushVideo=new PushVideo();
        mPushVideo.setPushConnectionListener(new PushConnectionListener() {
            @Override
            public void onConnecting() {
                Log.d("zw_debug", "正在链接服务器");
            }

            @Override
            public void onConnectSuccess() {
                Log.d("zw_debug", "链接服务器成功");
            }

            @Override
            public void onConnectionFailed(String msg) {
                Log.d("zw_debug", "链接服务器失败");
            }
        });
    }
}
