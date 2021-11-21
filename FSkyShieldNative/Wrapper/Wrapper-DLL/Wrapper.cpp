
#include "stdafx.h"
#include "Wrapper.h"
#include <fstream>
#include <sstream>
#include <time.h>

#include <jni.h>
#pragma comment(lib, "jvm.lib")

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
	str += ";lib/tools.jar";

	char *classpath = new char[str.length() + 1];
	strcpy(classpath, str.c_str());

	JNIEnv *env;
	JavaVM *jvm;

	JavaVMInitArgs args;
	JavaVMOption options[6];
	args.nOptions = 6;

	options[0].optionString = "-XX:+UseSerialGC";
	options[1].optionString = "-Xms16m";
	options[2].optionString = "-Xmx256m";
	options[3].optionString = "-XX:MinHeapFreeRatio=5";
	options[4].optionString = "-XX:MaxHeapFreeRatio=10";
	options[5].optionString = classpath;

	args.options = options;
	args.version = JNI_VERSION_1_8;
	args.ignoreUnrecognized = 0;

	JNI_CreateJavaVM(&jvm, (void**)&env, &args);

	if (env == NULL)
	{
		return 1;
	}

	jclass cls;
	jmethodID main;

	cls = env->FindClass("me/FurH/JavaPacker/loader/AClassLoader");
	main = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V");

	env->CallStaticVoidMethod(cls, main, NULL);
	jvm->DestroyJavaVM();

	return 0;
}