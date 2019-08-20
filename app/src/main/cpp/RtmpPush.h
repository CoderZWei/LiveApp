//
// Created by zw on 2019/8/18.
//

#ifndef LIVEAPP_RTMPPUSH_H
#define LIVEAPP_RTMPPUSH_H

#include <malloc.h>
#include <string.h>
#include "pthread.h"
#include "QueueUtil.h"
#include "CallbackUtil.h"


extern "C" {
#include "librtmp/rtmp.h"
};

class RtmpPush {
public:
    RTMP *rtmp = NULL;
    char *url = NULL;
    QueueUtil *rtmpQueue = NULL;
    CallbackUtil *callback = NULL;
    pthread_t push_thread;
    bool startPushing= false;
    long startTime=0;

    RtmpPush(const char *url, CallbackUtil *callback);

    ~RtmpPush();

    void init();

    void pushSPSPPS(char *sps, int sps_len, char *pps, int pps_len);

    void pushVideoData(char *data, int data_len, bool keyframe);


};

#endif //LIVEAPP_RTMPPUSH_H
