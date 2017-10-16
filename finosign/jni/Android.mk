LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -llog

LOCAL_MODULE    := AndroidLibFinoSign
LOCAL_SRC_FILES := d2r_checksign_lib_FinoSign.c

include $(BUILD_SHARED_LIBRARY)