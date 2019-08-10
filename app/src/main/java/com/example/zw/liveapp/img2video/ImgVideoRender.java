package com.example.zw.liveapp.img2video;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.zw.liveapp.R;
import com.example.zw.liveapp.egl.MyEGLSurfaceView;
import com.example.zw.liveapp.egl.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ImgVideoRender implements MyEGLSurfaceView.MyGLRender {
    private Context mContext;
    private OnRenderCreateListener onRenderCreateListener;
    private ImgFboRender mImgFboRender;
    private int srcImg=0;

    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };
    private FloatBuffer vertexBuffer;
    private float[] fragmentData = {
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
    };
    private FloatBuffer fragmentBuffer;
    private int program;
    private int vPosition;
    private int fPosition;
    private int textureid;

    private int vboId;
    private int fboId;

    private int imgTextureId;



    public ImgVideoRender(Context context) {
        this.mContext=context;
        mImgFboRender=new ImgFboRender(context);
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(fragmentData);
        fragmentBuffer.position(0);
    }


    @Override
    public void onSurfaceCreated() {
        mImgFboRender.onCreate();
        String vertexSource=ShaderUtil.getRawResource(mContext, R.raw.vertex_shader_screen);
        String fragmentSource=ShaderUtil.getRawResource(mContext,R.raw.fragment_shader_screen);
        program=ShaderUtil.createProgram(vertexSource,fragmentSource);
        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        int[] vbos=new int[1];
        GLES20.glGenBuffers(1,vbos,0);
        vboId=vbos[0];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + fragmentData.length * 4, null, GLES20. GL_STATIC_DRAW);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, fragmentData.length * 4, fragmentBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        int[] fbos=new int[1];
        GLES20.glGenBuffers(1,fbos,0);
        fboId=fbos[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        int[] textureIds=new int[1];
        GLES20.glGenTextures(1,textureIds,0);
        textureid=textureIds[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureid, 0);
        if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE)
        {
            Log.d("zw_debug", "fbo wrong");
        }
        else
        {
            Log.d("zw_debug", "fbo success");
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        if(onRenderCreateListener!=null){
            onRenderCreateListener.onCreate(textureid);
        }
        GLES20.glViewport(0,0,width,height);
        mImgFboRender.onChange(width,height);
    }

    @Override
    public void onDrawFrame() {
        imgTextureId=ShaderUtil.loadTexture(srcImg,mContext);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fboId);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f,0f, 0f, 1f);

        GLES20.glUseProgram(program);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,imgTextureId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vboId);

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,8,0);
        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        int []ids = new int[]{imgTextureId};
        GLES20.glDeleteTextures(1, ids, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        mImgFboRender.onDraw(textureid);
    }

    public void setOnRenderCreateListener(OnRenderCreateListener onRenderCreateListener) {
        this.onRenderCreateListener = onRenderCreateListener;
    }

    public interface OnRenderCreateListener{
        void onCreate(int textId);
    }

    public void setCurrentImgSrc(int src){
        srcImg=src;
    }

}
