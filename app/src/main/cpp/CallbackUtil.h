//
// Created by zw on 2019/8/19.
//
#ifndef LIVEAPP_CALLBACKUTIL_H
#define LIVEAPP_CALLBACKUTIL_H

#include "jni.h"
#include "log.h"

#define MAIN_THREAD 0
#define CHILD_THREAD 1

class CallbackUtil{
    public:
        JNIEnv *jniEnv=NULL;
        JavaVM *javaVM=NULL;
        jobject jobj;

        jmethodID jmid_connecting;
        jmethodID jmid_connectSuccess;
        jmethodID jmid_connectFailed;

        CallbackUtil(JNIEnv *jniEnv, JavaVM *javaVM, jobject *jobj) ;

        ~CallbackUtil();

        void onConnecting(int type);
        void onConnectSuccess(int type);
        void onConnectFailed(int type,char *msg);
};
#endif //LIVEAPP_CALLBACKUTIL_H
