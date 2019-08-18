//
// Created by zw on 2019/8/18.
//

#ifndef LIVEAPP_RTMPPUSH_H
#define LIVEAPP_RTMPPUSH_H

#include <malloc.h>
#include <string.h>
#include "pthread.h"
#include "QueueUtil.h"


extern "C"{
    #include "librtmp/rtmp.h"
};

class RtmpPush{
    public:
        RTMP *rtmp=NULL;
        char *url=NULL;
        QueueUtil *rtmpQueue=NULL;

        pthread_t push_thread;

        RtmpPush(const char* url);
        ~RtmpPush();

        void init();
};
#endif //LIVEAPP_RTMPPUSH_H
