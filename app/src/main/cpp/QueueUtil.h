//
// Created by zw on 2019/8/18.
//

#ifndef LIVEAPP_QUEUEUTIL_H
#define LIVEAPP_QUEUEUTIL_H

#include "queue"
#include "pthread.h"
#include "log.h"
extern "C"{
    #include "librtmp/rtmp.h"
};
using namespace std;
class QueueUtil{
    public:
        queue<RTMPPacket *>queuePacket;
        pthread_mutex_t mutexPacket;
        pthread_cond_t condPacket;

    public:
        QueueUtil();
        ~QueueUtil();

        int putRtmpPacket(RTMPPacket *packet);
        RTMPPacket* getRtmpPacket();
        void clearRtmpQueue();
        void notifyRtmpQueue();

};

#endif //LIVEAPP_QUEUEUTIL_H
