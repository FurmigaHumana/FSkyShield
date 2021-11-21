#include "stdafx.h"
#include "shield.h"
#include "java-interface.h"
#include "auto_handle.h"
#include <sstream>

#include <openssl/conf.h>
#include <openssl/evp.h>
#include <openssl/err.h>
#include <openssl/rand.h>
#include <openssl/md5.h>
#include <string.h>
#include <iomanip>

#include "zlib.h"

std::string jstring2string(JNIEnv* env, jstring jStr);

void handleErrors(void);

int encrypt(unsigned char* plaintext, int plaintext_len, unsigned char* key,
	unsigned char* iv, unsigned char* ciphertext);

int decrypt(unsigned char* ciphertext, int ciphertext_len, unsigned char* key,
	unsigned char* iv, unsigned char* plaintext);

std::string compress_string(const std::string& str,
    int compressionlevel);

std::string decompress_string(const std::string& str);

jbyteArray handleResponse(JNIEnv* env, std::string str, jstring inputkey);

std::string getSignatureInfo(std::string szFileName);

JNIEXPORT jlong JNICALL Java_me_FurH_SkyShield_win32_NativeShield_lastInput(JNIEnv* env, jclass _) {

    LASTINPUTINFO info;
    info.cbSize = sizeof(LASTINPUTINFO);

    if (GetLastInputInfo(&info)) {
        return info.dwTime;
    }

    return -1;
}

JNIEXPORT jboolean JNICALL Java_me_FurH_SkyShield_win32_NativeShield_is64(JNIEnv* env, jclass _, jint pid) {

    BOOL bIsWow64 = FALSE;
    HANDLE Handle = OpenProcess(PROCESS_QUERY_LIMITED_INFORMATION, FALSE, (DWORD) pid);

    if (!Handle) {
        reportError(env, "Failed to open process");
    } else {
        if (!IsWow64Process(Handle, &bIsWow64)) {
            reportError(env, "Failed to read process");
        }
    }

    CloseHandle(Handle);

    return bIsWow64;
}

/*const std::string md5str(const std::string& str) {

    unsigned char result[MD5_DIGEST_LENGTH];
    MD5((unsigned char*)str.c_str(), str.size(), result);

    std::ostringstream sout;
    sout << std::hex << std::setfill('0');

    for (long long c : result)
    {
        sout << std::setw(2) << (long long)c;
    }

    return sout.str();
}*/

const std::string decryptInput(JNIEnv* env, jstring inputkey, jbyteArray encryptedin) {
    
    int bytesize = env->GetArrayLength(encryptedin);

    boolean isCopy;
    jbyte* b = env->GetByteArrayElements(encryptedin, &isCopy);

    if (isCopy) {
        env->ReleaseByteArrayElements(encryptedin, b, 0);
    }

    std::string keystr = jstring2string(env, inputkey);

    char iv[16];
    for (int j1 = 0; j1 < 16; j1++) {
        iv[j1] = b[ (bytesize - 16) + j1 ];
    }

    char* plaintext = new char[ bytesize ];
    int decryptbytes = decrypt((unsigned char*) b, (bytesize - 16), (unsigned char*) keystr.c_str(), (unsigned char*) iv, (unsigned char*) plaintext);

    std::string compressed = std::string(plaintext, decryptbytes);

    return decompress_string(compressed);
}

const std::string hex(JNIEnv* env, jbyteArray data) {

    size_t length = (size_t)env->GetArrayLength(data);
    jbyte* pBytes = env->GetByteArrayElements(data, NULL);

    unsigned char* c = (unsigned char*) pBytes;
    int i;

    std::string result = "";
    char hex[2];

    for (i = 0; i < MD5_DIGEST_LENGTH; i++) {
        sprintf(hex, "%02x", c[i]);
        result += hex[0];
        result += hex[1];
    }

    env->ReleaseByteArrayElements(data, pBytes, 0);

    return result;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action10(JNIEnv* env, jclass _, jstring inputkey, jbyteArray encryptedin) {

    std::string path = decryptInput(env, inputkey, encryptedin);
    std::string signature = std::to_string(GetCurrentProcessId()) + ":" + getSignatureInfo(path);

    jbyteArray jdata = handleResponse(env, signature, inputkey);

    return jdata;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action9(JNIEnv* env, jclass _, jbyteArray encryptedin, jbyteArray md5, jstring inputkey) {

    std::string path = decryptInput(env, inputkey, encryptedin);
    std::string md5hash = std::to_string(GetCurrentProcessId()) + ":" + hex(env, md5) + ":" + path;

    jbyteArray jdata = handleResponse(env, md5hash, inputkey);

    return jdata;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action8(JNIEnv* env, jclass _, jbyteArray data, jstring inputkey) {
    
    boolean isCopy;
    size_t length = (size_t)env->GetArrayLength(data);
    jbyte* pBytes = env->GetByteArrayElements(data, &isCopy);

    unsigned char result[MD5_DIGEST_LENGTH];

    std::string keystr = jstring2string(env, inputkey);
    MD5_CTX mdContext;

    MD5_Init(&mdContext);
    
    MD5_Update(&mdContext, (unsigned char*)pBytes, length);
    MD5_Update(&mdContext, keystr.c_str(), 5);

    MD5_Final(result, &mdContext);

    env->ReleaseByteArrayElements(data, pBytes, 0);

    jbyteArray arr = env->NewByteArray(MD5_DIGEST_LENGTH);
    env->SetByteArrayRegion(arr, 0, MD5_DIGEST_LENGTH, (jbyte*) result);

    return arr;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action7(JNIEnv* env, jclass _, jstring inputkey) {

    std::string modules = std::to_string(GetCurrentProcessId()) + ":" + listModules();
    jbyteArray jdata = handleResponse(env, modules, inputkey);

    return jdata;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action6(JNIEnv* env, jclass _, jstring inputkey) {

    std::string result = std::to_string(GetCurrentProcessId()) + ":" + generateUniqueIds();
    jbyteArray jdata = handleResponse(env, result, inputkey);

    return jdata;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action5(JNIEnv* env, jclass _, jstring inputkey) {

    std::string currentPid = std::to_string(GetCurrentProcessId());
    jbyteArray jdata = handleResponse(env, currentPid, inputkey);

    return jdata;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action4(JNIEnv* env, jclass _, jstring inputkey, jbyteArray encryptedin) {

    std::string result = decryptInput(env, inputkey, encryptedin);
    std::string filelist = std::to_string(GetCurrentProcessId()) + ":" + listFiles(result);

    jbyteArray jdata = handleResponse(env, filelist, inputkey);

    return jdata;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action3(JNIEnv* env, jclass _, jstring inputkey, jbyteArray encryptedin) {

    std::string path = decryptInput(env, inputkey, encryptedin);
    std::string md5hash = std::to_string(GetCurrentProcessId()) + ":" + md5file(path) + ":" + path;

    jbyteArray jdata = handleResponse(env, md5hash, inputkey);

    return jdata;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action2(JNIEnv* env, jclass _, jstring inputkey) {

    std::string processlist = std::to_string(GetCurrentProcessId()) + ":" + readProcessList(env);
    jbyteArray jdata = handleResponse(env, processlist, inputkey);

    return jdata;
}

JNIEXPORT jbyteArray JNICALL Java_me_FurH_SkyShield_win32_NativeShield_action1(JNIEnv* env, jclass _, jint port, jstring inputkey) {

    const int maxpids = 2;
	int pids[maxpids];
	int pidsize = fetchPidsOnPort(env, (int) port, pids, maxpids);

	std::string str = "";

	if (pidsize <= 0) {

		str = "NOPID";

	}
	else if (pidsize != 1) {

		str = "MULTIPID";

	} else {

        jstring cmdline = getCmdLineAndEnvVars(env, pids[0], 0);
		std::string parsed = jstring2string(env, cmdline);

        str = std::to_string(pids[0]) + ":";
        str += parsed;
	}

    jbyteArray jdata = handleResponse(env, str, inputkey);

    return jdata;
}

jbyteArray handleResponse(JNIEnv* env, std::string str, jstring inputkey) {

    std::string keystr = jstring2string(env, inputkey);
    std::string compressed = compress_string(str, 1);

    unsigned char* key = (unsigned char*)keystr.c_str();
    unsigned char iv[16];

    RAND_bytes(iv, sizeof iv);

    unsigned char* ciphertext = (unsigned char*) new char[compressed.size() + 16];
    int ciphertext_len;

    ciphertext_len = encrypt((unsigned char*)compressed.c_str(), compressed.size(), key, iv, ciphertext);

    jbyteArray jdata = env->NewByteArray(ciphertext_len + 16);

    env->SetByteArrayRegion(jdata, 0, ciphertext_len, (jbyte*)ciphertext);
    env->SetByteArrayRegion(jdata, ciphertext_len, 16, (jbyte*)iv);

    return jdata;
}

std::string jstring2string(JNIEnv* env, jstring jStr) {
	
	if (!jStr)
		return "";

	const jclass stringClass = env->GetObjectClass(jStr);
	const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
	const jbyteArray stringJbytes = (jbyteArray)env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    boolean isCopy;
	size_t length = (size_t)env->GetArrayLength(stringJbytes);
	jbyte* pBytes = env->GetByteArrayElements(stringJbytes, &isCopy);
    
    if (isCopy) {
        env->ReleaseByteArrayElements(stringJbytes, pBytes, 0);
    }

    std::string ret = std::string((char*)pBytes, length);

	env->DeleteLocalRef(stringJbytes);
	env->DeleteLocalRef(stringClass);

	return ret;
}

void handleErrors(void)
{
    ERR_print_errors_fp(stderr);
    abort();
}

int encrypt(unsigned char* plaintext, int plaintext_len, unsigned char* key,
    unsigned char* iv, unsigned char* ciphertext)
{

}


int decrypt(unsigned char* ciphertext, int ciphertext_len, unsigned char* key,
    unsigned char* iv, unsigned char* plaintext)
{

}

std::string compress_string(const std::string& str,
    int compressionlevel)
{

}

std::string decompress_string(const std::string& str)
{

}