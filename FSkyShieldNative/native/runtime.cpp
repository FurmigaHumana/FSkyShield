#include "stdafx.h"
#include "shield.h"


void error( JNIEnv* env, const char* file, int line, const char* msg ) {
	DWORD errorCode = GetLastError();

	jclass winpException = env->FindClass("me/FurH/SkyShield/win32/NativeException");
	if(winpException==0)
		env->FatalError("Failed to find NativeException");

	jmethodID winpExceptionConstructor = env->GetMethodID(
		winpException,"<init>","(Ljava/lang/String;ILjava/lang/String;I)V");
	if(winpExceptionConstructor==0)
		env->FatalError("Failed to find constructor");

	env->ExceptionClear();
	env->Throw(
		(jthrowable)env->NewObject( winpException, winpExceptionConstructor,
			env->NewStringUTF(msg), errorCode, env->NewStringUTF(file), line ));
}
