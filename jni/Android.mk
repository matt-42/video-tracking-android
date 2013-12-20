LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE:=STATIC

include /home/garrigues/projects/OpenCV-2.4.5-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := native_tracking
LOCAL_SRC_FILES := tracking.cpp common.cpp background_substraction.cpp
LOCAL_LDLIBS += -landroid -llog -lm -mfloat-abi=softfp
LOCAL_CFLAGS += -DNO_OPENMP -std=c++0x -Ofast  -mfpu=neon -funroll-loops -DNO_CUDA -DWITH_OPENCV -I/usr/include -DNDEBUG -mfloat-abi=softfp -I !!!!FIXME_PATH_TO_CUIMG!!!!!

include $(BUILD_SHARED_LIBRARY)
