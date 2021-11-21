#include "setupnative.h"
#include "jni.h"
#include  "stdio.h"
#include "Shlobj.h"
 
JNIEXPORT jstring JNICALL Java_me_FurH_Setup_Native_SetupNative_getFolder(JNIEnv * pEnv, jobject, jint pathid)
{

	int castid = (int) pathid;

	TCHAR path[MAX_PATH];
	HRESULT result = SHGetFolderPath(NULL, castid | CSIDL_FLAG_DONT_VERIFY, NULL, SHGFP_TYPE_CURRENT, path);
	
	jstring packedStr;
	packedStr = pEnv->NewStringUTF(path);
	
	return packedStr;
}