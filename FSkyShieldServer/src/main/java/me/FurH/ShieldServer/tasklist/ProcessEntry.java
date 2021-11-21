package me.FurH.ShieldServer.tasklist;

import java.util.Objects;
import java.util.StringTokenizer;
import org.json.JSONObject;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ProcessEntry {

    final int pid;
    final String exe;
    private final String original;
    private final String desc;
    private final String path;
    private final String stamps;
    
    private int hashCode;

    ProcessEntry(String entry) {

        StringTokenizer token = new StringTokenizer(entry, "$^");

        this.pid = Integer.parseInt(token.nextToken());
        this.exe = token.nextToken();
        this.original = token.nextToken();
        this.desc = token.nextToken();
        this.path = token.nextToken();
        this.stamps = token.nextToken();
    }

    ProcessEntry(JSONObject next) {
        this.pid = 0;
        this.exe = next.getString("exe");
        this.original = next.getString("original");
        this.desc = next.getString("desc");
        this.path = next.getString("path");
        this.stamps = next.getString("stamps");
    }

    JSONObject toJson() {
        
        JSONObject json = new JSONObject();
        
        json.put("exe", exe);
        json.put("original", original);
        json.put("desc", desc);
        json.put("path", path);
        json.put("stamps", stamps);
        
        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessEntry other = (ProcessEntry) obj;
        if (!Objects.equals(this.exe, other.exe)) {
            return false;
        }
        if (!Objects.equals(this.original, other.original)) {
            return false;
        }
        if (!Objects.equals(this.desc, other.desc)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
//        if (!Objects.equals(this.stamps, other.stamps)) {
//            return false;
//        }
        return true;
    }

    @Override
    public int hashCode() {
        
        if (hashCode == -1) {
           
            int hash = 7;
            hash = 13 * hash + Objects.hashCode(this.exe);
            hash = 13 * hash + Objects.hashCode(this.original);
            hash = 13 * hash + Objects.hashCode(this.desc);
            hash = 13 * hash + Objects.hashCode(this.path);
//            hash = 13 * hash + Objects.hashCode(this.stamps);
            
            hashCode = hash;
        }
        
        return hashCode;
    }
}