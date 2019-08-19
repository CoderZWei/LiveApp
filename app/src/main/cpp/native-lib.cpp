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
    rtmpPush=new RtmpPush(pushUrl);
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