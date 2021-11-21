// ConsolePEApplication1.cpp : 2064Defines3021 1815  the entry point for the console application.
//

#include "stdafx.h"
#include "jni.h"
#include "windows.h"
#include "string"

#pragma comment(lib, "jvm.lib")

int main()
{

	LPWSTR cmargs = GetCommandLineW();

	std::wstring cmdline = std::wstring(cmargs);

	if (cmdline.find(L"delayed") != std::string::npos)
	{
		Sleep(10000);
	}

	JNIEnv *env;
	JavaVM *jvm;

	JavaVMInitArgs args;
	JavaVMOption options[6];
	args.nOptions = 6;

	options[0].optionString = (char*)"-XX:+UseSerialGC";
	options[1].optionString = (char*)"-Xms16m";
	options[2].optionString = (char*)"-Xmx256m";
	options[3].optionString = (char*)"-XX:MinHeapFreeRatio=5";
	options[4].optionString = (char*)"-XX:MaxHeapFreeRatio=10";
	options[5].optionString = "-Djava.class.path=client.jar;lib/tools.jar";

	args.options = options;
	args.version = JNI_VERSION_1_8;
	args.ignoreUnrecognized = 0;

	JNI_CreateJavaVM(&jvm, (void**) &env, &args);

	if (env == NULL)
	{
		return 1;
	}

	jclass cls = NULL;
	jmethodID main = NULL;

	cls = env->FindClass("me/FurH/JavaPacker/loader/AClassLoader");
	main = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V");

	env->CallStaticVoidMethod(cls, main, NULL);
	jvm->DestroyJavaVM();

	return 0;
}