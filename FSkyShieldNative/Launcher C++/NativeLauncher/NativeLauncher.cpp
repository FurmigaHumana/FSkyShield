// Win32Project1.cpp : Defines the entry point for the application.
//

#include "stdafx.h"

#include <winbase.h>
#include <sstream>
#include <fstream>
#include <time.h>

#include "NativeLauncher.h"

#include <openssl/md5.h>
#include <iomanip>

#include <jni.h>
#pragma comment(lib, "jvm.lib")

using namespace std;

int executeJavaVM();

int APIENTRY wWinMain(_In_ HINSTANCE hInstance,
                     _In_opt_ HINSTANCE hPrevInstance,
                     _In_ LPWSTR    lpCmdLine,
                     _In_ int       nCmdShow)
{

	char* exec = new char[ MAX_PATH ];
	int bytes = GetModuleFileName(NULL, exec, MAX_PATH);

	std::string basepath = exec;
	std::string base = basepath.substr(0, basepath.find_last_of("\\"));

	std::string path1 = base + "\\bin\\server";
	std::string path2 = base + "\\bin";

	std::string builtpath = path1 + ";" + path2;
	
	char* path = getenv("PATH");
	
	std::string pathbit = path;
	pathbit += ";";
	pathbit += builtpath;

	printf(pathbit.c_str());
	printf("\n");

	int ret = _putenv_s("PATH", pathbit.c_str());

	executeJavaVM();

    return 0;
}

const std::string md5file(const std::string& filename) {

    unsigned char c[MD5_DIGEST_LENGTH];
    int i;

    FILE* inFile = fopen(filename.c_str(), "rb");
    MD5_CTX mdContext;
    int bytes;

    unsigned char data[1024];

    if (inFile == NULL) {
        return filename;
    }

    MD5_Init(&mdContext);
    
    while ((bytes = fread(data, 1, 1024, inFile)) != 0) {
        MD5_Update(&mdContext, data, bytes);
    }

    MD5_Final(c, &mdContext);
    
    std::string result = "";
    char hex[2];

    for (i = 0; i < MD5_DIGEST_LENGTH; i++) {
        sprintf(hex, "%02x", c[i]);
        result += hex[0];
        result += hex[1];
    }

    fclose(inFile);

    return result;
}

const std::string md5str(const std::string& str) {
		
	unsigned char result[MD5_DIGEST_LENGTH];
	MD5((unsigned char*)str.c_str(), str.size(), result);

	std::ostringstream sout;
	sout << std::hex << std::setfill('0');
	
	for (long long c : result)
	{
		sout << std::setw(2) << (long long)c;
	}

	return sout.str();
}

int executeJavaVM() {

	time_t seconds;
	seconds = time(NULL);

	std::ostringstream sin;
	sin << "client_" << seconds << ".jar";

	std::string filename(sin.str());

	char* filechars = new char[filename.length() + 1];
	strcpy(filechars, filename.c_str());

	std::ifstream  src("client.jar", std::ios::binary);
	std::ofstream  dst(filechars, std::ios::binary);

	dst << src.rdbuf();

	src.close();
	dst.close();

	std::string str = "-Djava.class.path=";
	str += filename;
	str += ";lib/tools.jar;lib/jna.jar";

	char *classpath = new char[str.length() + 1];
	strcpy(classpath, str.c_str());

	JNIEnv *env;
	JavaVM *jvm;

	JavaVMInitArgs args;
	JavaVMOption options[7];
	args.nOptions = 7;

	options[0].optionString = "-XX:+UseSerialGC";
	options[1].optionString = "-Xms8m";
	options[2].optionString = "-Xmx256m";
	options[3].optionString = "-XX:MinHeapFreeRatio=5";
	options[4].optionString = "-XX:MaxHeapFreeRatio=10";
	options[5].optionString = "-XX:+DisableAttachMechanism";
	options[6].optionString = classpath;

	args.options = options;
	args.version = JNI_VERSION_1_8;
	args.ignoreUnrecognized = 0;

	std::string filehash = md5file(filename);

	JNI_CreateJavaVM(&jvm, (void**)&env, &args);

	if (env == NULL)
	{
		return 1;
	}

	jclass cls;
	jmethodID main;

	std::string clsname = "me/FurH/JavaPacker/loader/AClassLoader";

	cls = env->FindClass(clsname.c_str());
	main = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V");

	std::string namehash = md5str(filename);
	std::string signature = md5str(filename + filehash + clsname);
	std::string hash = "";

	int pos1 = 0;
	int fi1 = 0;
	int fi2 = 0;
	int fi3 = 0;

	for (int j1 = 0; j1 < 96; j1++) {

		if (pos1 == 0) {
			pos1++;
			hash += filehash.at(fi1++);
		}
		else if (pos1 == 1) {
			pos1++;
			hash += namehash.at(fi2++);
		}
		else if (pos1 == 2) {
			pos1 = 0;
			hash += signature.at(fi3++);
		}
	}

	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray inputArgs = env->NewObjectArray(1, stringClass, env->NewStringUTF(hash.c_str()));

	env->CallStaticVoidMethod(cls, main, inputArgs);
	jvm->DestroyJavaVM();

	remove(filename.c_str());

	return 0;
}