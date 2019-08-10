package com.example.zw.liveapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        System.loadLibrary("native-lib");
    }
    private Button btn_camera_preview,btn_video,btn_imgMixture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        btn_camera_preview=(Button)findViewById(R.id.btn_camera_preview);
        btn_camera_preview.setOnClickListener(this);
        btn_video=(Button)findViewById(R.id.btn_video);
        btn_video.setOnClickListener(this);
        btn_imgMixture=(Button)findViewById(R.id.btn_imgMixture);
        btn_imgMixture.setOnClickListener(this);
    }


    public native String stringFromJNI();

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
                Intent intent3=new Intent(MainActivity.this,img2VideoActivity.class);
                startActivity(intent3);
        }
    }
}
