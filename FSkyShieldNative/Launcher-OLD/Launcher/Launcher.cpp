
#include "stdafx.h"
#include <stdio.h>
#include <windows.h>
#include <string>

using namespace std;

int main()
{

	STARTUPINFO si;
	PROCESS_INFORMATION pi;
	int spi = sizeof(pi);
	int ssi = sizeof(si);
	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(STARTUPINFO);
	ZeroMemory(&pi, sizeof(pi));

	WCHAR working_directory[MAX_PATH + 1];
	GetModuleFileNameW(NULL, working_directory, sizeof(working_directory));

	wstring temp = wstring(working_directory);
	wstring::size_type pos = temp.find_last_of(L"\\");

	wstring work = wstring(temp.substr(0, pos));
	SetCurrentDirectoryW(work.c_str());

	wstring temp0 = wstring(work);
	temp0 += L"\\bin\\server";

	LPCWSTR path = temp0.c_str();

	SetDllDirectoryW(path);

	LPWSTR args = GetCommandLineW();
	LPWSTR exline = NULL;

	std::wstring cmdline = std::wstring(args);

	if (cmdline.find(L"delayed") != std::string::npos)
	{
		exline = L"SkyShield.exe -delayed";
	}

	CreateProcessW(
		L"SkyShield.exe",   // No module name (use command line)
		exline,        // Command line
		NULL,           // Process handle not inheritable
		NULL,           // Thread handle not inheritable
		FALSE,          // Set handle inheritance to FALSE
		CREATE_NEW_PROCESS_GROUP | CREATE_NO_WINDOW,              // No creation flags
		NULL,           // Use parent's environment block
		NULL,           // Use parent's starting directory 
		&si,            // Pointer to STARTUPINFO structure
		&pi           // Pointer to PROCESS_INFORMATION structure
		);

    return 0;
}