LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE:=STATIC

include /home/garrigues/projects/OpenCV-2.4.5-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := native_tracking
LOCAL_SRC_FILES := tracking.cpp common.cpp background_substraction.cpp

#LOCAL_C_INCLUDES += ${NDKROOT}/sources/cxx-stl/stlport/stlport
#LOCAL_C_INCLUDES += /home/garrigues/projects/cuimg -/usr/include

#LOCAL_CXXFLAGS += -DNO_OPENMP -std=c++0x  -Ofast -mfloat-abi=softfp
#LOCAL_SHARED_LIBRARIES :=  android log pthread
#TARGET_ARCH_ABI := armeabi-v7a
#TARGET_ARCH := arm
LOCAL_LDLIBS += -landroid -llog -lm -mfloat-abi=softfp
LOCAL_CFLAGS += -DNO_OPENMP -std=c++0x -Ofast  -mfpu=neon -funroll-loops -DNO_CUDA -DWITH_OPENCV -I/home/garrigues/projects/cuimg -I/usr/include -DNDEBUG -mfloat-abi=softfp
#LOCAL_CFLAGS += -DBOOST_EXCEPTION_DISABLE -D_STLP_NO_EXCEPTIONS -DOS_ANDROID -D_STLP_USE_SIMPLE_NODE_ALLOC -mfloat-abi=softfp 

include $(BUILD_SHARED_LIBRARY)
