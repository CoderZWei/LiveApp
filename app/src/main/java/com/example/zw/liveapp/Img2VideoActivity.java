package com.example.zw.liveapp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.zw.liveapp.encoder.MyMediaEncoder;
import com.example.zw.liveapp.img2video.ImgVideoView;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

public class Img2VideoActivity extends AppCompatActivity {
    private ImgVideoView mImgVideoView;
    private Button mBtn;
    private MyMediaEncoder mMediaEncoder;
    private WlMusic mWlMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img2video);
        mImgVideoView=(ImgVideoView)findViewById(R.id.imgVideoView);
        mImgVideoView.setCurrentImg(R.drawable.img_1);

        mBtn=(Button)findViewById(R.id.btnStartMixture);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWlMusic.setSource(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/the girl.m4a");
                mWlMusic.prePared();
            }
        });
        mWlMusic=WlMusic.getInstance();
        mWlMusic.setCallBackPcmData(true);
        mWlMusic.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                mWlMusic.playCutAudio(0,60);
            }
        });
        mWlMusic.setOnShowPcmDataListener(new OnShowPcmDataListener() {
            @Override
            public void onPcmInfo(int samplerate, int bit, int channels) {
                mMediaEncoder=new MyMediaEncoder(Img2VideoActivity.this,mImgVideoView.getFboTextureId());
                mMediaEncoder.initEncoder(mImgVideoView.getEglContext(),
                        Environment.getExternalStorageDirectory().getAbsolutePath()+ "/img2video.mp4",
                        720,500,samplerate,channels);
                mMediaEncoder.startRecord();
                startImgs();
            }

            @Override
            public void onPcmData(byte[] pcmdata, int size, long clock) {
                if(mMediaEncoder!=null){
                    mMediaEncoder.putPCMData(pcmdata,size);
                }
            }
        });
    }

    private void startImgs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=1;i<257;i++){
                    int imgSrc=getResources().getIdentifier("img_" + i, "drawable","com.example.zw.liveapp");
                    mImgVideoView.setCurrentImg(imgSrc);
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(mMediaEncoder!=null){
                    mWlMusic.stop();
                    mMediaEncoder.stopRecord();
                    mMediaEncoder=null;
                }
            }
        }).start();
    }
}
