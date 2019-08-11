package com.example.zw.liveapp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.zw.liveapp.yuvVideo.YuvView;

import java.io.File;
import java.io.FileInputStream;

public class YuvVideoActivity extends AppCompatActivity {
    private Button mBtn;
    private YuvView mYuvView;
    private FileInputStream fis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv_video);
        mBtn=(Button)findViewById(R.id.btn_play);
        mYuvView=(YuvView)findViewById(R.id.yuvView);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int w = 640;
                            int h = 360;
                            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/sintel_640_360.yuv"));
                            byte []y = new byte[w * h];
                            byte []u = new byte[w * h / 4];
                            byte []v = new byte[w * h / 4];

                            while (true)
                            {
                                int ry = fis.read(y);
                                int ru = fis.read(u);
                                int rv = fis.read(v);
                                if(ry > 0 && ru > 0 && rv > 0)
                                {
                                    //Log.d("zw_debug","send data");
                                    mYuvView.setFrameData(w, h, y, u, v);
                                    Thread.sleep(40);
                                }
                                else
                                {
                                    Log.d("zw_debug", "读取结束");
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            Log.d("zw_debug",e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
