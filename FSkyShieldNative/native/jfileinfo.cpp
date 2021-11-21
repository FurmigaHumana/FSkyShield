#include "stdafx.h"
#include "shield.h"
#include "java-interface.h"
#include "auto_handle.h"

#include <sstream>
#include <fstream>

#include <openssl/md5.h>
#include <iomanip>
#include "dirent.h"

const std::string listFiles(const std::string path) {
    
    WDIR* dir;
    struct wdirent* ent;

    if ((dir = wopendir(s2ws(path).c_str())) != NULL) {

        std::string result = "";
        int count = 0;

        while ((ent = wreaddir(dir)) != NULL) {

            count++;
           
            result += ws2s(std::wstring(ent->d_name, ent->d_namlen));
           
            if (ent->d_type == DT_DIR) {
                result += ":1";
            }
            else {
                result += ":0";
            }

            result += "\n";

            if (count > 100) {
                break;
            }
        }

        wclosedir(dir);

        return result;
    }
    else {
        return "ERR";
    }
}

const std::string md5file(const std::string& filename) {

    unsigned char c[MD5_DIGEST_LENGTH];
    int i;

    FILE* inFile = _wfopen(s2ws(filename).c_str(), L"rb");
    MD5_CTX mdContext;
    int bytes;

    unsigned char data[1024];

    if (inFile == NULL) {
        return "ERR";
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