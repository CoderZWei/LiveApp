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
    private Button btn_camera_preview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        btn_camera_preview=(Button)findViewById(R.id.btn_camera_preview);
        btn_camera_preview.setOnClickListener(this);
    }


    public native String stringFromJNI();

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera_preview:
                Intent intent=new Intent(MainActivity.this,CameraPreviewActivity.class);
                startActivity(intent);
                break;
        }
    }
}
