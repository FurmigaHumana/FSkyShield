package me.FurH.ShieldServer.newlistener;

import org.json.JSONObject;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class FileInfo {

    FileType type = FileType.UNTESTED;
    String hash;

    boolean signed;
    String sigdata;
    boolean ziphash;

    JSONObject toJson() {
        
        JSONObject info = new JSONObject();

        info.put("hash", hash);
        
        if (type.needCert()) {
            info.put("signed", signed);
            info.put("signdata", sigdata);
        }
        
        if (ziphash) {
            info.put("ziphash", ziphash);
        }
        
        return info;
    }

}