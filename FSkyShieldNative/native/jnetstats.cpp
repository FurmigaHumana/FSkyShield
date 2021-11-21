#include "stdafx.h"
#include "shield.h"
#include "java-interface.h"
#include "auto_handle.h"

#include "iphlpapi.h"
#pragma comment(lib, "IPHLPAPI.lib")

#define PTR32(T)	DWORD

boolean arrContains(int* pids, int size, int pid);

int fetchPidsOnPort(JNIEnv* pEnv, int port, int *pids, int maxsize) {

	int size = 0;
	int i;

	DWORD dwSize = 0;

	GetExtendedTcpTable(0, &dwSize, TRUE, AF_INET, TCP_TABLE_OWNER_PID_ALL, 0);

	PVOID pTcpTable = VirtualAlloc(NULL, dwSize, MEM_COMMIT, PAGE_READWRITE);

	if (GetExtendedTcpTable(pTcpTable, &dwSize, TRUE, AF_INET, TCP_TABLE_OWNER_PID_ALL, 0) != NO_ERROR)
	{

		VirtualFree(pTcpTable, NULL, MEM_RELEASE);
		reportError(pEnv, "Failed to fetch extended tcp table");

		return 0;
	}

	MIB_TCPROW_OWNER_PID TcpRow;

	for (DWORD i = 0; i < ((PMIB_TCPTABLE_OWNER_PID)pTcpTable)->dwNumEntries; i++)
	{

		TcpRow = ((PMIB_TCPTABLE_OWNER_PID)pTcpTable)->table[i];

		if (TcpRow.dwOwningPid > 4 && TcpRow.dwState == 5 && TcpRow.dwRemotePort == (int)port)
		{

			if (arrContains(pids, size, TcpRow.dwOwningPid)) {
				continue;
			}

			if (maxsize > size) {
				pids[size++] = TcpRow.dwOwningPid;
			}
			else {
				break;
			}
		}
	}

	VirtualFree(pTcpTable, NULL, MEM_RELEASE);

	return size;
}

boolean arrContains(int* pids, int size, int pid) {

	for (int j1 = 0; j1 < size; j1++) {
		if (pids[j1] == pid) {
			return true;
		}
	}

	return false;
}