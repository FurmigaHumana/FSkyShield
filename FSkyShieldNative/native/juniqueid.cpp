#include "stdafx.h"
#include "shield.h"
#include "java-interface.h"
#include "auto_handle.h"

#include <sstream>
#include <fstream>

#include <openssl/md5.h>
#include <iomanip>
#include "dirent.h"

const std::string volumeId() {

	DWORD VolumeSerialNumber = 0;
	GetVolumeInformation(L"c:\\", NULL, NULL, &VolumeSerialNumber, NULL, NULL, NULL, NULL);

	return std::to_string(VolumeSerialNumber);
}

const std::string productId() {

    std::wstring key = L"SOFTWARE\\Microsoft\\Cryptography";
    std::wstring name = L"MachineGuid";

    HKEY hKey;

    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, key.c_str(), 0, KEY_READ | KEY_WOW64_64KEY, &hKey) != ERROR_SUCCESS) {
        return "E1";
    }

    DWORD type;
    DWORD cbData;

    if (RegQueryValueEx(hKey, name.c_str(), NULL, &type, NULL, &cbData) != ERROR_SUCCESS)
    {
        RegCloseKey(hKey);
        return "R2";
    }

    if (type != REG_SZ)
    {
        RegCloseKey(hKey);
        return "R3";
    }

    std::wstring value(cbData / sizeof(wchar_t), L'\0');

    if (RegQueryValueEx(hKey, name.c_str(), NULL, NULL, reinterpret_cast<LPBYTE>(&value[0]), &cbData) != ERROR_SUCCESS)
    {
        RegCloseKey(hKey);
        return "R4";
    }

    RegCloseKey(hKey);

    return ws2s(value);
}

const std::string generateUniqueIds() {

	std::string result = "";

	result += volumeId();
    result += ":" + productId();

	return result;
}