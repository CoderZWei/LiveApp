//
// Created by zw on 2019/8/18.
//

#ifndef LIVEAPP_LOG_H
#define LIVEAPP_LOG_H


#include <android/log.h>

static const char *TAG="zw_debug";

#define LOG_SHOW true

#ifndef ALOGD
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#endif

#ifndef ALOGE
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
#endif //AUDIOCOURSE_LOG_H

#endif //LIVEAPP_LOG_H
