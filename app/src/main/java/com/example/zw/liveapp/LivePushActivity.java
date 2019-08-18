package com.example.zw.liveapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                mPushVideo.initLivePush("rtmp://X.X.X.X/live/mystream");
            }
        });
        mPushVideo=new PushVideo();
    }
}
