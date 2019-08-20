#include <jni.h>
#include <string>
#include "RtmpPush.h"
#include "CallbackUtil.h"

extern "C"{
    #include "librtmp/rtmp.h"
}

RtmpPush *rtmpPush=NULL;
CallbackUtil *callback=NULL;
JavaVM *javaVM=NULL;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_zw_liveapp_push_PushVideo_initPush(JNIEnv *env, jobject instance,
                                                    jstring pushUrl_) {
    const char *pushUrl = env->GetStringUTFChars(pushUrl_, 0);

    // TODO
    callback=new CallbackUtil(env,javaVM,&instance);
    rtmpPush=new RtmpPush(pushUrl,callback);
    rtmpPush->init();

    env->ReleaseStringUTFChars(pushUrl_, pushUrl);
}


extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
    javaVM = vm;
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_4) != JNI_OK)
    {
        if(LOG_SHOW)
        {
            ALOGD("GetEnv failed!");
        }
        return -1;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved){
    javaVM = NULL;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_zw_liveapp_push_PushVideo_pushSPSPPS(JNIEnv *env, jobject instance,
                                                      jbyteArray sps_, jint sps_len,
                                                      jbyteArray pps_, jint pps_len) {
    jbyte *sps = env->GetByteArrayElements(sps_, NULL);
    jbyte *pps = env->GetByteArrayElements(pps_, NULL);

    // TODO
    if(rtmpPush!=NULL){
        rtmpPush->pushSPSPPS(reinterpret_cast<char *>(sps), sps_len, reinterpret_cast<char *>(pps), pps_len);
    }
    env->ReleaseByteArrayElements(sps_, sps, 0);
    env->ReleaseByteArrayElements(pps_, pps, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_zw_liveapp_push_PushVideo_pushVideoData__Lbyte_3_093_2IZ(JNIEnv *env,
                                                                          jobject instance,
                                                                          jbyteArray data_,
                                                                          jint data_len,
                                                                          jboolean keyframe) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO
    if(rtmpPush!=NULL){
        rtmpPush->pushVideoData(reinterpret_cast<char *>(data), data_len, keyframe);
    }
    env->ReleaseByteArrayElements(data_, data, 0);
}