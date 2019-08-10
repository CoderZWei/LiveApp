package com.example.zw.liveapp;

import android.media.MediaFormat;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.zw.liveapp.camera.CameraView;
import com.example.zw.liveapp.encoder.BaseMediaEncoder;
import com.example.zw.liveapp.encoder.MyMediaEncoder;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnCompleteListener;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
    private CameraView mCameraView;
    private Button mBtn;
    private MyMediaEncoder mMediaEncoder;
    private WlMusic mWlMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mCameraView=(CameraView)findViewById(R.id.cameraViewRecord);
        mBtn=(Button)findViewById(R.id.btnStartRecord);
        mBtn.setOnClickListener(this);

        mWlMusic=WlMusic.getInstance();
        mWlMusic.setCallBackPcmData(true);
        mWlMusic.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                mWlMusic.playCutAudio(10,50);
            }
        });
        mWlMusic.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
               if(mMediaEncoder!=null){
                   mMediaEncoder.stopRecord();
                   mMediaEncoder=null;
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           mBtn.setText("开始录制");
                       }
                   });
               }
            }
        });
        mWlMusic.setOnShowPcmDataListener(new OnShowPcmDataListener() {
            @Override
            public void onPcmInfo(int samplerate, int bit, int channels) {
                mMediaEncoder=new MyMediaEncoder(VideoActivity.this,mCameraView.getTextureId());
                mMediaEncoder.initEncoder(mCameraView.getEglContext(),
                        Environment.getExternalStorageDirectory().getAbsolutePath()+"/demo.mp4",
                        720,1280,samplerate,channels);
                mMediaEncoder.setOnMediaInfoListener(new BaseMediaEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {
                        Log.d("zw_debug",String.valueOf(times));
                    }
                });
                mMediaEncoder.startRecord();
            }

            @Override
            public void onPcmData(byte[] pcmdata, int size, long clock) {
                if(mMediaEncoder!=null){
                    mMediaEncoder.putPCMData(pcmdata,size);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(mMediaEncoder==null){
            mWlMusic.setSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio.ogg");
            mWlMusic.prePared();
           /* mMediaEncoder.setOnMediaInfoListener(new BaseMediaEncoder.OnMediaInfoListener() {
                @Override
                public void onMediaTime(int times) {
                    Log.d("zw_debug",String.valueOf(times));
                }
            });*/
            //mMediaEncoder.startRecord();
            mBtn.setText("正在录制");
        }else {
            mMediaEncoder.stopRecord();
            mBtn.setText("开始录制");
            mMediaEncoder=null;
            mWlMusic.stop();
        }
    }
}
