#pragma once
#include "stdafx.h"
#include <sstream>

// Sets bit 29 in order to keep the codes in the user space
#define reportErrorWithCode(env,code,msg)	SetLastError(code + 0x10000000); error(env,__FILE__,__LINE__,msg);
#define reportError(env,msg)	error(env,__FILE__,__LINE__,msg);

void error(JNIEnv* env, const char* file, int line, const char* msg);

int fetchPidsOnPort(JNIEnv* pEnv, int port, int* pids, int maxsize);

jstring getCmdLineAndEnvVars(JNIEnv* pEnv, jint pid, jint retrieveEnvVars);

std::wstring s2ws(const std::string& str);

std::string ws2s(const std::wstring& wstr);

const std::string readProcessList(JNIEnv* env);

const std::string listFiles(const std::string path);

const std::string md5file(const std::string& filename);

const std::string generateUniqueIds();

std::string listModules();

//
// NTDLL functions
//

// see http://msdn2.microsoft.com/en-us/library/aa489609.aspx
#define NT_SUCCESS(Status) ((NTSTATUS)(Status) >= 0)

enum PROCESSINFOCLASS {
	// see http://msdn2.microsoft.com/en-us/library/ms687420(VS.85).aspx
	ProcessBasicInformation = 0,
	ProcessWow64Information = 26,
	ProcessBreakOnTermination = 29,
};

enum MBI_REGION_STATE : DWORD {
	/// For MEMORY_BASIC_IONFORMATION#State
	// https://msdn.microsoft.com/en-us/library/windows/desktop/aa366775(v=vs.85).aspx
	Allocated = MEM_COMMIT,
	Free = MEM_FREE,
	Reserved = MEM_RESERVE
};

enum MBI_REGION_PROTECT : DWORD {
	/// For MEMORY_BASIC_IONFORMATION#Protect
	// https://msdn.microsoft.com/en-us/library/windows/desktop/aa366786(v=vs.85).aspx
	NoAccessToCheck = 0,
	NoAccess = PAGE_NOACCESS,
	// Documentation does not really say it is not readable, but it seems so according to the samples in the internet and existense of PAGE_EXECUTE_READ
	ExecuteOnly = PAGE_EXECUTE
	//TODO: Add other flags on-demand
};

enum MBI_REGION_TYPE : DWORD {
	/// For MEMORY_BASIC_IONFORMATION#Type
	// https://msdn.microsoft.com/en-us/library/windows/desktop/aa366775(v=vs.85).aspx
	Image = MEM_IMAGE,
	Mapped = MEM_MAPPED,
	Private = MEM_PRIVATE
};

extern "C" NTSTATUS NTAPI ZwQueryInformationProcess(HANDLE hProcess, PROCESSINFOCLASS infoType, /*out*/ PVOID pBuf, /*sizeof pBuf*/ ULONG lenBuf, SIZE_T* /*PULONG*/ returnLength); 

#define	SystemProcessesAndThreadsInformation 5

#define STATUS_INFO_LENGTH_MISMATCH      ((NTSTATUS)0xC0000004L)

extern "C" NTSTATUS NTAPI ZwQuerySystemInformation(UINT, PVOID, ULONG, PULONG);