package com.example.zw.liveapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.zw.liveapp.camera.CameraView;
import com.example.zw.liveapp.push.BasePushEncoder;
import com.example.zw.liveapp.push.PushConnectionListener;
import com.example.zw.liveapp.push.PushEncoder;
import com.example.zw.liveapp.push.PushVideo;

public class LivePushActivity extends AppCompatActivity {
    private Button btnStartPush;
    private CameraView cameraViewPush;
    private PushEncoder mPushEncoder;
    private PushVideo mPushVideo;
    private Boolean isPushing=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_push);
        btnStartPush=(Button)findViewById(R.id.btn_startPush);
        btnStartPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPushing=!isPushing;
                if(isPushing){
                    mPushVideo.initLivePush("rtmp://10.135.104.71/");
                }else {
                    if(mPushEncoder!=null){
                        mPushEncoder.stopRecord();
                        mPushEncoder=null;
                    }
                }


            }
        });
        cameraViewPush=(CameraView)findViewById(R.id.cameraView_push);
        mPushVideo=new PushVideo();
        mPushVideo.setPushConnectionListener(new PushConnectionListener() {
            @Override
            public void onConnecting() {
                Log.d("zw_debug", "正在链接服务器");
            }

            @Override
            public void onConnectSuccess() {
                Log.d("zw_debug", "链接服务器成功");
                mPushEncoder=new PushEncoder(LivePushActivity.this,cameraViewPush.getTextureId());
                mPushEncoder.initEncoder(cameraViewPush.getEglContext(),720, 1280, 44100, 2);
                mPushEncoder.startRecord();
                mPushEncoder.setOnMediaInfoListener(new BasePushEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {

                    }

                    @Override
                    public void onSPSPPSInfo(byte[] sps, byte[] pps) {
                        mPushVideo.pushSPSPPS(sps,pps);
                    }

                    @Override
                    public void onVideoInfo(byte[] data, boolean keyFame) {
                        mPushVideo.pushVideoData(data,keyFame);
                    }
                });
            }

            @Override
            public void onConnectionFailed(String msg) {
                Log.d("zw_debug", "链接服务器失败");
            }
        });
    }
}
