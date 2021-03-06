package com.example.zw.liveapp.encoder;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import com.example.zw.liveapp.egl.EglHelper;
import com.example.zw.liveapp.egl.MyEGLSurfaceView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;

public abstract class BaseMediaEncoder {
    private Surface surface;
    private EGLContext eglContext;
    private int width,height;
    private MediaCodec videoEncoder;
    private MediaFormat videoFormat;
    private MediaCodec.BufferInfo videoBufferInfo;

    private MediaCodec audioEncoder;
    private MediaFormat audioFormat;
    private MediaCodec.BufferInfo audioBufferInfo;
    private long audioPts=0;
    private int sampleRate;

    private MediaMuxer mediaMuxer;
    private MyEGLSurfaceView.MyGLRender mGLRender;
    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;
    private int mRenderMode = RENDERMODE_CONTINUOUSLY;
    private OnMediaInfoListener onMediaInfoListener;
    private EGLMediaThread eglMediaThread;
    private VideoEncodeThread videoEncodeThread;
    private AudioEncodeThread audioEncodeThread;

    private Boolean encodeStart,videoExit,audioExit;

    public BaseMediaEncoder(Context context) {
    }

    public void setRender(MyEGLSurfaceView.MyGLRender render){
        this.mGLRender=render;
    }

    public void setRenderMode(int renderMode){
        this.mRenderMode=renderMode;
    }

    public void initEncoder(EGLContext eglContext,String savePath,int width,int height,int sampleRate,int channelCount){
        this.width=width;
        this.height=height;
        this.eglContext=eglContext;
        initMediaEncoder(savePath,width,height,sampleRate,channelCount);
    }

    private void initMediaEncoder(String savePath, int width, int height,int sampleRate,int channelCount) {
        try {
            mediaMuxer=new MediaMuxer(savePath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            initVideoEncoder(MediaFormat.MIMETYPE_VIDEO_AVC,width,height);
            initAudioEncoder(MediaFormat.MIMETYPE_AUDIO_AAC,sampleRate,channelCount);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initVideoEncoder(String mimeType, int width, int height) {
        videoBufferInfo=new MediaCodec.BufferInfo();
        videoFormat=MediaFormat.createVideoFormat(mimeType,width,height);
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4);
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

        try {
            videoEncoder=MediaCodec.createEncoderByType(mimeType);
            videoEncoder.configure(videoFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
            surface=videoEncoder.createInputSurface();
        } catch (IOException e) {
            Log.d("zw_debug",e.getMessage());
            e.printStackTrace();
            videoEncoder=null;
            videoFormat=null;
            videoBufferInfo=null;
        }
    }

    private void initAudioEncoder(String mimeType, int sampleRate, int channelCount) {
        this.sampleRate=sampleRate;
        audioBufferInfo=new MediaCodec.BufferInfo();
        audioFormat=MediaFormat.createAudioFormat(mimeType,sampleRate,channelCount);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE,96000);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE,4096);
        try {
            audioEncoder=MediaCodec.createEncoderByType(mimeType);
            audioEncoder.configure(audioFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
            audioBufferInfo=null;
            audioFormat=null;
            audioEncoder=null;
        }

    }

    public void putPCMData(byte[] buffer,int size){
        if(audioEncodeThread!=null && !audioEncodeThread.isExit && buffer!=null && size>0){
            int inputBufferIndex=audioEncoder.dequeueInputBuffer(0);
            if(inputBufferIndex>=0){
                ByteBuffer byteBuffer=audioEncoder.getInputBuffers()[inputBufferIndex];
                byteBuffer.clear();
                byteBuffer.put(buffer);
                long pts=getAudioPts(size,sampleRate);
                audioEncoder.queueInputBuffer(inputBufferIndex,0,size,pts,0);
            }
        }
    }

    private long getAudioPts(int size,int sampleRate){
        audioPts += (long)(1.0 * size / (sampleRate * 2 * 2) * 1000000.0);
        return audioPts;
    }

    public interface OnMediaInfoListener{
        void onMediaTime(int times);
    }

    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener) {
        this.onMediaInfoListener = onMediaInfoListener;
    }

    public void startRecord(){
        if(surface!=null && eglContext!=null){
            audioPts=0;
            audioExit=false;
            videoExit=false;
            encodeStart=false;

            eglMediaThread=new EGLMediaThread(new WeakReference<BaseMediaEncoder>(this));
            eglMediaThread.isCreate=true;
            eglMediaThread.isChange=true;
            eglMediaThread.start();

            videoEncodeThread=new VideoEncodeThread(new WeakReference<BaseMediaEncoder>(this));
            videoEncodeThread.start();
            audioEncodeThread=new AudioEncodeThread(new WeakReference<BaseMediaEncoder>(this));
            audioEncodeThread.start();
        }
    }

    public void stopRecord(){
        if(eglMediaThread!=null && videoEncodeThread!=null){
            videoEncodeThread.exit();
            videoEncodeThread=null;
            audioEncodeThread.exit();
            audioEncodeThread=null;
            eglMediaThread.onDestory();
            eglMediaThread=null;
        }
    }

    static class EGLMediaThread extends Thread{
        private WeakReference<BaseMediaEncoder>encoder;
        private EglHelper eglHelper;
        private Object object;
        private boolean isExit=false;
        private boolean isCreate=false;
        private boolean isChange=false;
        private boolean isStart=false;
        public EGLMediaThread(WeakReference<BaseMediaEncoder>encoder) {
            this.encoder=encoder;
        }

        @Override
        public void run() {
            super.run();
            isExit=false;
            isStart=false;
            object=new Object();
            eglHelper=new EglHelper();
            eglHelper.initEgl(encoder.get().surface,encoder.get().eglContext);
            while (true){
                if(isExit){
                    release();
                    break;
                }
                if(isStart){
                    if(encoder.get().mRenderMode==RENDERMODE_WHEN_DIRTY){
                        synchronized (object){
                            try {
                                object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else if(encoder.get().mRenderMode==RENDERMODE_CONTINUOUSLY){
                        try {
                            Thread.sleep(1000/60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        throw new RuntimeException("RenderMode wrong value");
                    }
                }
                onCreate();
                onChange(encoder.get().width,encoder.get().height);
                onDraw();
                isStart=true;
            }
        }

        private void onCreate(){
            if(isCreate && encoder.get().mGLRender!=null){
                isCreate=false;
                encoder.get().mGLRender.onSurfaceCreated();
            }
        }

        private void onChange(int width,int height){
            if(isChange&&encoder.get().mGLRender!=null){
                isChange=false;
                encoder.get().mGLRender.onSurfaceChanged(width,height);
            }
        }

        private void onDraw(){
            if(encoder.get().mGLRender!=null && eglHelper!=null){
                encoder.get().mGLRender.onDrawFrame();
                if(!isStart){
                    encoder.get().mGLRender.onDrawFrame();
                }
                eglHelper.swapBuffers();
            }
        }

        private void requestRender(){
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

        public void release(){
            if(eglHelper!=null){
                eglHelper.destoryEgl();
                eglHelper=null;
                object=null;
                encoder=null;
            }
        }
    }

    static class VideoEncodeThread extends Thread{
        private WeakReference<BaseMediaEncoder>encoder;
        private boolean isExit;
        private MediaCodec videoEncoder;
        private MediaFormat videoFormat;
        private MediaCodec.BufferInfo videoBufferInfo;
        private MediaMuxer mediaMuxer;
        private int videoTrackIndex;
        private long pts;
        public VideoEncodeThread(WeakReference<BaseMediaEncoder> encoder) {
            this.encoder = encoder;
            videoEncoder=encoder.get().videoEncoder;
            videoFormat=encoder.get().videoFormat;
            videoBufferInfo=encoder.get().videoBufferInfo;
            mediaMuxer=encoder.get().mediaMuxer;
            videoTrackIndex=-1;
        }
        @Override
        public void run() {
            super.run();
            pts=0;
            videoTrackIndex=-1;
            isExit=false;
            videoEncoder.start();
            while (true){
                //停止编码
                if(isExit){
                    videoEncoder.stop();
                    videoEncoder.release();
                    videoEncoder=null;
                    encoder.get().videoExit=true;
                    if(encoder.get().audioExit){
                        mediaMuxer.stop();
                        mediaMuxer.release();
                        mediaMuxer=null;
                    }
                    break;
                }
                int outputBufferIndex=videoEncoder.dequeueOutputBuffer(videoBufferInfo,0);
                if(outputBufferIndex==MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                    videoTrackIndex=mediaMuxer.addTrack(videoEncoder.getOutputFormat());
                    if(encoder.get().audioEncodeThread.audioTrackIndex!=-1){
                        mediaMuxer.start();
                        encoder.get().encodeStart=true;
                    }
                }else {
                    while (outputBufferIndex>=0){
                        if(encoder.get().encodeStart){
                            ByteBuffer outputBuffer=videoEncoder.getOutputBuffers()[outputBufferIndex];
                            outputBuffer.position(videoBufferInfo.offset);
                            outputBuffer.limit(videoBufferInfo.offset+videoBufferInfo.size);
                            if(pts==0){
                                pts=videoBufferInfo.presentationTimeUs;
                            }
                            videoBufferInfo.presentationTimeUs=videoBufferInfo.presentationTimeUs-pts;
                            mediaMuxer.writeSampleData(videoTrackIndex,outputBuffer,videoBufferInfo);

                            if(encoder.get().onMediaInfoListener!=null){
                                encoder.get().onMediaInfoListener.onMediaTime((int)(videoBufferInfo.presentationTimeUs/1000000));
                                }
                        }
                        videoEncoder.releaseOutputBuffer(outputBufferIndex,false);
                        outputBufferIndex=videoEncoder.dequeueOutputBuffer(videoBufferInfo,0);
                    }
                }
            }
        }
        private void exit(){
            this.isExit=true;
        }
    }

    static class AudioEncodeThread extends Thread{
        private WeakReference<BaseMediaEncoder>encoder;
        private boolean isExit;
        private MediaCodec audioEncoder;
        private MediaCodec.BufferInfo audioBufferInfo;
        private MediaMuxer mediaMuxer;
        private int audioTrackIndex;
        private long pts;

        public AudioEncodeThread(WeakReference<BaseMediaEncoder> encoder) {
            this.encoder = encoder;
            audioEncoder=encoder.get().audioEncoder;
            audioBufferInfo=encoder.get().audioBufferInfo;
            mediaMuxer=encoder.get().mediaMuxer;
            audioTrackIndex=-1;
        }

        @Override
        public void run() {
            super.run();
            pts=0;
            isExit=false;
            audioEncoder.start();
            while (true){
                if (isExit){
                    audioEncoder.stop();
                    audioEncoder.release();
                    audioEncoder=null;
                    encoder.get().audioExit=true;
                    if(encoder.get().videoExit){
                        mediaMuxer.stop();
                        mediaMuxer.release();
                        mediaMuxer=null;
                    }
                    break;
                }

                int outputBufferIndex=audioEncoder.dequeueOutputBuffer(audioBufferInfo,0);
                if(outputBufferIndex==MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                    if(mediaMuxer!=null){
                        audioTrackIndex=mediaMuxer.addTrack(audioEncoder.getOutputFormat());
                        if(encoder.get().videoEncodeThread.videoTrackIndex!=-1){
                            mediaMuxer.start();
                            encoder.get().encodeStart=true;
                        }
                    }
                }else {
                    while (outputBufferIndex>=0){
                        if(encoder.get().encodeStart){
                            ByteBuffer outputBuffer=audioEncoder.getOutputBuffers()[outputBufferIndex];
                            outputBuffer.position(audioBufferInfo.offset);
                            outputBuffer.limit(audioBufferInfo.offset+audioBufferInfo.size);
                            if(pts==0){
                                pts=audioBufferInfo.presentationTimeUs;
                            }
                            audioBufferInfo.presentationTimeUs=audioBufferInfo.presentationTimeUs-pts;
                            mediaMuxer.writeSampleData(audioTrackIndex,outputBuffer,audioBufferInfo);
                        }
                        audioEncoder.releaseOutputBuffer(outputBufferIndex,false);
                        outputBufferIndex=audioEncoder.dequeueOutputBuffer(audioBufferInfo,0);
                    }
                }
            }
        }

        public void exit(){
            this.isExit=true;
        }
    }
}
