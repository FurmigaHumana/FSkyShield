#include "stdafx.h"
#include "shield.h"
#include "java-interface.h"
#include "auto_handle.h"

JNIEXPORT void JNICALL Java_me_FurH_SkyShield_win32_NativeShield_enableDebugPrivilege0(JNIEnv* env, jclass _) {
	auto_handle hToken;
	if(!OpenProcessToken( GetCurrentProcess(),
		TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken )) {
		reportError(env,"Failed to open the current process");
		return;
	}

	LUID sedebugnameValue;
	if(!LookupPrivilegeValue( NULL, SE_DEBUG_NAME, &sedebugnameValue )) {
		reportError(env,"Failed to look up SE_DEBUG_NAME");
		return;
	}

	TOKEN_PRIVILEGES tkp;
	tkp.PrivilegeCount = 1;
	tkp.Privileges[0].Luid = sedebugnameValue;
	tkp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;

	if(!AdjustTokenPrivileges( hToken, FALSE, &tkp, sizeof tkp, NULL, NULL )) {
		reportError(env,"Failed to adjust token privileges");
		return;
	}
}

