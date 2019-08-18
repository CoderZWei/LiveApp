package com.example.zw.liveapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnCameraPreview,btnVideo,btnImgMixture,btnYuvVideo,btnLivePush;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        btnCameraPreview=(Button)findViewById(R.id.btn_camera_preview);
        btnCameraPreview.setOnClickListener(this);
        btnVideo=(Button)findViewById(R.id.btn_video);
        btnVideo.setOnClickListener(this);
        btnImgMixture=(Button)findViewById(R.id.btn_imgMixture);
        btnImgMixture.setOnClickListener(this);
        btnYuvVideo=(Button)findViewById(R.id.btn_yuvVideo);
        btnYuvVideo.setOnClickListener(this);
        btnLivePush=(Button)findViewById(R.id.btn_livePush);
        btnLivePush.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera_preview:
                Intent intent1=new Intent(MainActivity.this,CameraPreviewActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_video:
                Intent intent2=new Intent(MainActivity.this,VideoActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_imgMixture:
                Intent intent3=new Intent(MainActivity.this,Img2VideoActivity.class);
                startActivity(intent3);
                break;
            case R.id.btn_yuvVideo:
                Intent intent4=new Intent(MainActivity.this,YuvVideoActivity.class);
                startActivity(intent4);
                break;
            case R.id.btn_livePush:
                Intent intent5=new Intent(MainActivity.this,LivePushActivity.class);
                startActivity(intent5);
                break;
        }
    }
}
