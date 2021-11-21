#include "stdafx.h"
#include "shield.h"
#include "java-interface.h"
#include <sstream>

HWND FindTopWindow(DWORD pid, BOOL ignoreSmall, BOOL ignoreHidden);

JNIEXPORT jstring JNICALL Java_me_FurH_SkyShield_win32_NativeShield_hwnd(JNIEnv* env, jclass _) {

    HWND hwnd = FindTopWindow(GetCurrentProcessId(), true, true); // only visible
    std::string ret;

    if (hwnd == 0) {
        hwnd = FindTopWindow(GetCurrentProcessId(), true, false); // allow hidden
    }

    if (hwnd == 0) {
        hwnd = FindTopWindow(GetCurrentProcessId(), false, false); // allow small
    }

    if (hwnd == 0) {

        ret = "0:0:0:0";

    }
    else {

        RECT rect;

        GetClientRect(hwnd, &rect);

        int width = rect.right;
        int height = rect.bottom;

        ClientToScreen(hwnd, reinterpret_cast<POINT*>(&rect.left));
        ClientToScreen(hwnd, reinterpret_cast<POINT*>(&rect.right));

        ret = std::to_string(width) + ":" + std::to_string(height) + ":" + std::to_string(rect.left) + ":" + std::to_string(rect.top);
    }

    return env->NewStringUTF(ret.c_str());
}

BOOL is_main_window(HWND handle, BOOL ignoreSmall, BOOL ignoreHidden)
{

    if (ignoreHidden && !IsWindowVisible(handle)) {
        return false;
    }

    if (ignoreSmall) {

        RECT rect;
        GetClientRect(handle, &rect);

        if (rect.right < 100 || rect.bottom < 100) {
            return false;
        }
    }

    return GetWindow(handle, GW_OWNER) == (HWND) 0;
}

HWND FindTopWindow(DWORD pid, BOOL ignoreSmall, BOOL ignoreHidden)
{

    std::pair<std::pair<HWND, DWORD>, std::pair<BOOL, BOOL>> params = { 
        { 0, pid },
        { ignoreSmall, ignoreHidden }
    };

    BOOL bResult = EnumWindows([](HWND hwnd, LPARAM lParam) -> BOOL
        {
            auto pParams = (std::pair<std::pair<HWND, DWORD>, std::pair<BOOL, BOOL>>*)(lParam);

            DWORD processId;
            if (is_main_window(hwnd, pParams->second.first, pParams->second.second) && GetWindowThreadProcessId(hwnd, &processId) && processId == pParams->first.second)
            {
                SetLastError(-1);
                pParams->first.first = hwnd;
                return FALSE;
            }

            return TRUE;
        }, (LPARAM)&params);

    if (!bResult && GetLastError() == -1 && params.first.first)
    {
        return params.first.first;
    }

    return 0;
}