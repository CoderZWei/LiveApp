package com.example.zw.liveapp;

import android.media.MediaFormat;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.zw.liveapp.camera.CameraView;
import com.example.zw.liveapp.encodec.BaseMediaEncoder;
import com.example.zw.liveapp.encodec.MyMediaEncoder;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
    private CameraView mCameraView;
    private Button mBtn;
    private MyMediaEncoder mMediaEncoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mCameraView=(CameraView)findViewById(R.id.cameraViewRecord);
        mBtn=(Button)findViewById(R.id.btnStartRecord);
        mBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mMediaEncoder==null){
            mMediaEncoder=new MyMediaEncoder(this,mCameraView.getTextureId());
            mMediaEncoder.initEncoder(mCameraView.getEglContext(),Environment.getExternalStorageDirectory().getAbsolutePath()+"/demo.mp4",MediaFormat.MIMETYPE_VIDEO_AVC,720,1080);
            mMediaEncoder.setOnMediaInfoListener(new BaseMediaEncoder.OnMediaInfoListener() {
                @Override
                public void onMediaTime(int times) {
                    Log.d("zw_debug",String.valueOf(times));
                }
            });
            mMediaEncoder.startRecord();
            mBtn.setText("正在录制");
        }else {
            mMediaEncoder.stopRecord();
            mBtn.setText("开始录制");
            mMediaEncoder=null;
        }
    }
}
