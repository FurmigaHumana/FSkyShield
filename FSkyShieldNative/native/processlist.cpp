#include "stdafx.h"
#include "shield.h"
#include "java-interface.h"
#include "auto_handle.h"
#include <psapi.h>
#include <vector>

#include <sstream>
#include <string.h>

#include <codecvt>

#pragma comment (lib, "Psapi.lib")
#pragma comment (lib, "version.lib")

std::wstring processPath(DWORD processId);

std::string readOriginalName(const wchar_t* fullname);

std::string fileMod(const wchar_t* fullname);

std::wstring s2ws(const std::string& str)
{
    using convert_typeX = std::codecvt_utf8<wchar_t>;
    std::wstring_convert<convert_typeX, wchar_t> converterX;

    return converterX.from_bytes(str);
}

std::string ws2s(const std::wstring& wstr)
{
    using convert_typeX = std::codecvt_utf8<wchar_t>;
    std::wstring_convert<convert_typeX, wchar_t> converterX;

    return converterX.to_bytes(wstr);
}

std::string listModules() {

    HMODULE hMods[1024];
    HANDLE hProcess;
    DWORD cbNeeded;
    unsigned int i;

    if (!EnumProcessModules(GetCurrentProcess(), hMods, sizeof(hMods), &cbNeeded)) {
        return "E1";
    }

    std::string result = "";

    for (i = 0; i < (cbNeeded / sizeof(HMODULE)); i++)
    {

        TCHAR szModName[MAX_PATH];

        if (!GetModuleFileName(hMods[i], szModName, sizeof(szModName) / sizeof(TCHAR)))
        {
            result += "ER";
        }
        else {

            std::string ret = ws2s(std::wstring(std::wstring(szModName).c_str()));
            result += ret;

        }

        result += "\n";
    }

    return result;
}

const std::string readProcessList(JNIEnv* env) {

    HANDLE hProcessSnap;
    HANDLE hProcess;
    PROCESSENTRY32 pe32;
    DWORD dwPriorityClass;

    hProcessSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);

    if (hProcessSnap == INVALID_HANDLE_VALUE)
    {
        reportError(env, "Failed to open process snapshot");
        return "E1";
    }

    pe32.dwSize = sizeof(PROCESSENTRY32);

    if (!Process32First(hProcessSnap, &pe32))
    {
        CloseHandle(hProcessSnap);
        reportError(env, "Failed to fetch first process");
        return "E2";
    }

    std::string result = "";

    do
    {

        std::wstring fullpath = processPath(pe32.th32ProcessID);
        const wchar_t* charpath = fullpath.c_str();

        result += std::to_string(pe32.th32ProcessID);
        result += "$^" + ws2s(pe32.szExeFile);
        result += "$^" + readOriginalName(charpath);
        result += "$^" + ws2s(fullpath);
        result += "$^" + fileMod(charpath);

        result += "\n";

    } while (Process32Next(hProcessSnap, &pe32));

    CloseHandle(hProcessSnap);

    return result;
}

LONGLONG FileTime_to_POSIX(FILETIME ft)
{
    LARGE_INTEGER date, adjust;

    date.HighPart = ft.dwHighDateTime;
    date.LowPart = ft.dwLowDateTime;
    adjust.QuadPart = 11644473600000 * 10000;
    date.QuadPart -= adjust.QuadPart;

    return date.QuadPart / 10000000;
}

std::string fileMod(const wchar_t* fullname) {

    HANDLE hFile1;
    hFile1 = CreateFile(fullname, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);

    if (hFile1 == INVALID_HANDLE_VALUE)
    {
        CloseHandle(hFile1);
        return "E1";
    }

    FILETIME ftCreate;
    FILETIME ftAccess;
    FILETIME ftModify;

    if (!GetFileTime(hFile1, &ftCreate, &ftAccess, &ftModify))
    {
        CloseHandle(hFile1);
        return "E2";
    }

    CloseHandle(hFile1);

    LONGLONG createStamp = FileTime_to_POSIX(ftCreate);
    LONGLONG accessStamp = FileTime_to_POSIX(ftAccess);
    LONGLONG modifyStamp = FileTime_to_POSIX(ftModify);

    return std::to_string(createStamp) + ":" + std::to_string(modifyStamp) + ":" + std::to_string(accessStamp);
}

std::wstring processPath(DWORD processId)
{
    std::wstring name = L"E";
    HANDLE Handle = OpenProcess(PROCESS_QUERY_LIMITED_INFORMATION, FALSE, processId);
   
    if (Handle) {

        TCHAR Buffer[MAX_PATH];
        DWORD dwSize = MAX_PATH;

        if (QueryFullProcessImageName(Handle, 0, Buffer, &dwSize)) {
            name = std::wstring(Buffer);
        }
    }

    CloseHandle(Handle);

    return name;
}

struct LANGANDCODEPAGE {
    WORD wLanguage;
    WORD wCodePage;
} *lpTranslate;

std::string readOriginalName(const wchar_t* fullname) {

    DWORD dwHandle;
    DWORD dwinfoSize = GetFileVersionInfoSize(fullname, &dwHandle);

    if (!dwinfoSize) {
        return "E1$^E1";
    }

    LPWSTR info = (LPWSTR) calloc(1, dwinfoSize);

    if (!info) {
        return "E2$^E2";
    }

    BOOL bRes = GetFileVersionInfo(fullname, 0, dwinfoSize, info);

    if (!bRes) {
        free(info);
        return "E3$^E3";
    }

    UINT uLen;

    if (!VerQueryValue(info, L"\\VarFileInfo\\Translation", (LPVOID*) &lpTranslate, &uLen)) {
        free(info);
        return "E4$^E4";
    }

    if (!uLen) {
        free(info);
        return "E5$^E5";
    }

    std::string result = "";

    UINT uBytes;
    LPBYTE lpBuffer = NULL;

    wchar_t buf[1024] = {0};
    swprintf(buf, L"\\StringFileInfo\\%04x%04x\\%s", lpTranslate[0].wLanguage, lpTranslate[0].wCodePage, L"OriginalFilename");

    VerQueryValue(info, buf, (LPVOID*) &lpBuffer, &uBytes);

    if (uBytes == 0) {
        result += "E6";
    }
    else {
        result += ws2s(std::wstring(std::wstring((wchar_t*)lpBuffer, uBytes).c_str()));
    }
    
    result += "$^";

    uBytes = 0;
    lpBuffer = NULL;

    wchar_t buf2[1024] = { 0 };
    swprintf(buf2, L"\\StringFileInfo\\%04x%04x\\%s", lpTranslate[0].wLanguage, lpTranslate[0].wCodePage, L"FileDescription");

    VerQueryValue(info, buf2, (LPVOID*)&lpBuffer, &uBytes);

    if (uBytes == 0) {
        result += "E7";
    }
    else {
        result += ws2s(std::wstring(std::wstring((wchar_t*)lpBuffer, uBytes).c_str()));
    }

    free(info);

    return result;
}