//
// Created by zw on 2019/8/18.
//
#include "RtmpPush.h"

RtmpPush:: RtmpPush(const char* url,CallbackUtil *callback) {
    this->url= static_cast<char *>(malloc(512));
    strcpy(this->url,url);
    this->rtmpQueue=new QueueUtil();
    this->callback=callback;
}

RtmpPush::~RtmpPush() {
    rtmpQueue->notifyRtmpQueue();
    rtmpQueue->clearRtmpQueue();
    free(url);
}

void *callBackPush(void *data){
    RtmpPush *rtmpPush= static_cast<RtmpPush*>(data);
    rtmpPush->rtmp=RTMP_Alloc();
    RTMP_Init(rtmpPush->rtmp);
    rtmpPush->rtmp->Link.timeout = 10;
    rtmpPush->rtmp->Link.lFlags |= RTMP_LF_LIVE;
    RTMP_SetupURL(rtmpPush->rtmp,rtmpPush->url);
    RTMP_EnableWrite(rtmpPush->rtmp);

    if(!RTMP_Connect(rtmpPush->rtmp, NULL))
    {
        ALOGD("can not connect the url");
        rtmpPush->callback->onConnectFailed(CHILD_THREAD,"can not connect the url");
        goto end;
    }
    if(!RTMP_ConnectStream(rtmpPush->rtmp, 0))
    {
        ALOGD("can not connect the stream of service");
        rtmpPush->callback->onConnectFailed(CHILD_THREAD,"can not connect the stream of service");
        goto end;
    }

    ALOGD("链接成功， 开始推流");
    rtmpPush->callback->onConnectSuccess(CHILD_THREAD);
    end:
        RTMP_Close(rtmpPush->rtmp);
        RTMP_Free(rtmpPush->rtmp);
        rtmpPush->rtmp=NULL;

        pthread_exit(&rtmpPush->push_thread);
}

void RtmpPush::init() {
    rtmpPush->callback->onConnecting(MAIN_THREAD);
    pthread_create(&push_thread,NULL,callBackPush,this);
}
