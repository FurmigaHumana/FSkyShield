#include <jni.h>

#ifndef _Included_me_FurH_SkyShield_win32_NativeShield
#define _Included_me_FurH_SkyShield_win32_NativeShield
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_me_FurH_SkyShield_win32_NativeShield_enableDebugPrivilege0
  (JNIEnv *, jclass);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action1
(JNIEnv*, jclass, jint, jstring);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action2
(JNIEnv*, jclass, jstring);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action3
(JNIEnv*, jclass, jstring, jbyteArray);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action4
(JNIEnv*, jclass, jstring, jbyteArray);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action5
(JNIEnv*, jclass, jstring);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action6
(JNIEnv*, jclass, jstring);

JNIEXPORT jboolean JNICALL Java_me_FurH_SkyShield_win32_NativeShield_is64
(JNIEnv*, jclass, jint);

JNIEXPORT jlong JNICALL Java_me_FurH_SkyShield_win32_NativeShield_lastInput
(JNIEnv*, jclass);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action7
(JNIEnv*, jclass, jstring);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action8
(JNIEnv*, jclass, jbyteArray, jstring);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action9
(JNIEnv*, jclass, jbyteArray, jbyteArray, jstring);

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action10
(JNIEnv*, jclass, jstring, jbyteArray);

JNIEXPORT jstring JNICALL Java_me_FurH_SkyShield_win32_NativeShield_hwnd
(JNIEnv*, jclass);

#ifdef __cplusplus
}
#endif
#endif
