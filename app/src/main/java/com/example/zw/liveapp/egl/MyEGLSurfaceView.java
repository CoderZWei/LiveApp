package com.example.zw.liveapp.egl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;

public class MyEGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private Surface mSurface;
    private EGLContext mEglContext;
    private MyGLRender myGLRender;
    private MyEGLThread myEGLThread;
    public final static int RENDERMODE_WHEN_DIRTY=0;
    public final static int RENDERMODE_CONTINUOUSLY=1;
    private int mRenderMode=RENDERMODE_CONTINUOUSLY;
    public interface MyGLRender{
        void onSurfaceCreated();
        void onSurfaceChanged(int width,int height);
        void onDrawFrame();
    }

    public MyEGLSurfaceView(Context context) {
        this(context,null);
    }

    public MyEGLSurfaceView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public MyEGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }
    public void setRender(MyGLRender render){
        this.myGLRender=render;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mSurface==null){
            mSurface=holder.getSurface();
        }
        myEGLThread=new MyEGLThread(new WeakReference<MyEGLSurfaceView>(this));
        myEGLThread.isCreate=true;
        myEGLThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        myEGLThread.width=width;
        myEGLThread.height=height;
        myEGLThread.isChange=true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myEGLThread.onDestory();
        myEGLThread=null;
        mSurface=null;
        mEglContext=null;
    }

    public void setRenderMode(int mRenderMode) {
        if(myGLRender==null){
            throw new RuntimeException("must set render before");
        }
        this.mRenderMode = mRenderMode;
    }
    public void setSurfaceAndEglContext(Surface surface,EGLContext eglContext){
        this.mSurface=surface;
        this.mEglContext=eglContext;
    }
    public EGLContext getEglContext(){
        if(myEGLThread!=null){
            return myEGLThread.getEglContext();
        }
        return null;
    }
    public void requestRender(){
        if(myEGLThread!=null){
            myEGLThread.requestRender();
        }
    }


    static class MyEGLThread extends Thread{
        private WeakReference<MyEGLSurfaceView>myEGLSurfaceViewWeakReference;
        private EglHelper mEglHelper=null;
        private Object object=null;
        private boolean isExit=false;
        private boolean isCreate=false;
        private boolean isChange=false;
        private boolean isStart=false;
        private int width,height;
        private MyEGLThread(WeakReference<MyEGLSurfaceView>myEGLSurfaceViewWeakReference){
            this.myEGLSurfaceViewWeakReference=myEGLSurfaceViewWeakReference;
        }

        @Override
        public void run() {
            super.run();
            isExit=false;
            isStart=false;
            object=new Object();
            mEglHelper=new EglHelper();
            mEglHelper.initEgl(myEGLSurfaceViewWeakReference.get().mSurface,myEGLSurfaceViewWeakReference.get().mEglContext);
            while (true){
                if(isExit){
                    //释放资源
                    release();
                    break;
                }
                if(isStart) {
                    if(myEGLSurfaceViewWeakReference.get().mRenderMode==RENDERMODE_WHEN_DIRTY){
                        synchronized (object){
                            try {
                                object.wait();
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }else if(myEGLSurfaceViewWeakReference.get().mRenderMode==RENDERMODE_CONTINUOUSLY){
                        try {
                            Thread.sleep(1000/60);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }else {
                        throw new RuntimeException("RenderMode is wrong value");
                    }
                }
                onCreate();
                onChange(width,height);
                onDraw();
                isStart=true;
            }
        }

        private void onCreate() {
            if(isCreate && myEGLSurfaceViewWeakReference.get().myGLRender!=null){
                isCreate=false;
                myEGLSurfaceViewWeakReference.get().myGLRender.onSurfaceCreated();
            }
        }
        private void onChange(int width,int height){
            if(isChange&&myEGLSurfaceViewWeakReference.get().myGLRender!=null){
                isChange=false;
                myEGLSurfaceViewWeakReference.get().myGLRender.onSurfaceChanged(width,height);
            }
        }
        private void onDraw(){
            if(myEGLSurfaceViewWeakReference.get().myGLRender!=null && mEglHelper!=null){
                myEGLSurfaceViewWeakReference.get().myGLRender.onDrawFrame();
                if(!isStart){
                    myEGLSurfaceViewWeakReference.get().myGLRender.onDrawFrame();
                }
                mEglHelper.swapBuffers();
            }
        }
        public void requestRender(){
            if(object!=null){
                synchronized (object){
                    object.notifyAll();
                }
            }
        }
        public void onDestory(){
            isExit=true;
            requestRender();
        }
        public void release() {
            if(mEglHelper!=null){
                mEglHelper.destoryEgl();
                mEglHelper=null;
                object=null;
                myEGLSurfaceViewWeakReference=null;
            }
        }
        public EGLContext getEglContext(){
            if(mEglHelper!=null){
                return mEglHelper.getEglContext();
            }
            return null;
        }
    }
}
