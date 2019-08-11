package com.example.zw.liveapp.yuvVideo;

import android.content.Context;
import android.util.AttributeSet;

import com.example.zw.liveapp.egl.MyEGLSurfaceView;

public class YuvView extends MyEGLSurfaceView {
    private YuvRender mYuvRender;

    public YuvView(Context context) {
        this(context,null);
    }

    public YuvView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public YuvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mYuvRender=new YuvRender(context);
        setRender(mYuvRender);
        setRenderMode(MyEGLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setFrameData(int w, int h, byte[] by, byte[] bu, byte[] bv)
    {
        if(mYuvRender != null)
        {
            mYuvRender.setFrameData(w, h, by, bu, bv);
            requestRender();
        }
    }

}
