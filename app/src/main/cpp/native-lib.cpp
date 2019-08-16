#include <jni.h>
#include <string>

extern "C"{
    #include "librtmp/rtmp.h"
}
extern "C" JNIEXPORT jstring JNICALL


Java_com_example_zw_liveapp_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
