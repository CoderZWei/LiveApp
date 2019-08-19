//
// Created by zw on 2019/8/19.
//
#include "CallbackUtil.h"


CallbackUtil::CallbackUtil(JNIEnv *jniEnv, JavaVM *javaVM, jobject *jobj) {
    this->jniEnv=jniEnv;
    this->javaVM=javaVM;
    this->jobj=jniEnv->NewGlobalRef(*jobj);

    jclass jlz=jniEnv->GetObjectClass(this->jobj);
    jmid_connecting=jniEnv->GetMethodID(jlz,"onConnecting","()V");
    jmid_connectSuccess=jniEnv->GetMethodID(jlz,"onConnectSuccess","()V");
    jmid_connectFailed=jniEnv->GetMethodID(jlz,"onConnectionFailed","(Ljava/lang/String;)V");
}

CallbackUtil::~CallbackUtil() {
    jniEnv->DeleteGlobalRef(jobj);
    javaVM = NULL;
    jniEnv = NULL;
}

void CallbackUtil::onConnecting(int type) {
    if(type==CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_connecting);
        javaVM->DetachCurrentThread();
    } else{
        jniEnv->CallVoidMethod(jobj, jmid_connecting);
    }
}

void CallbackUtil::onConnectSuccess(int type) {
    if(type==CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_connectSuccess);
        javaVM->DetachCurrentThread();
    } else{
        jniEnv->CallVoidMethod(jobj, jmid_connectSuccess);
    }
}

void CallbackUtil::onConnectFailed(int type,char *msg) {
    if(type==CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            return;
        }
        jstring jmsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jobj, jmid_connectFailed,jmsg);
        jniEnv->DeleteLocalRef(jmsg);
        javaVM->DetachCurrentThread();
    } else{
        jstring jmsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jobj, jmid_connectFailed,jmsg);
        jniEnv->DeleteLocalRef(jmsg);
    }
}


