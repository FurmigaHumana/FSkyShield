#include <jni.h>

#ifndef _Included_me_FurH_Setup_Native_SetupNative
#define _Included_me_FurH_Setup_Native_SetupNative
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_me_FurH_Setup_Native_SetupNative_getFolder
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
