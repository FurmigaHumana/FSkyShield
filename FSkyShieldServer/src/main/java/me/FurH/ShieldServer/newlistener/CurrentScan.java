package me.FurH.ShieldServer.newlistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import me.FurH.Core.encript.Encrypto;
import me.FurH.ShieldServer.filelist.FileCrawler;
import me.FurH.ShieldServer.listener.HandlerListener;
import me.FurH.ShieldServer.newdb.RevisionList;
import me.FurH.ShieldServer.newdb.VersionEntry;
import static me.FurH.ShieldServer.newlistener.CmdLine.path;
import me.FurH.ShieldServer.protocol.MapEntry;
import me.FurH.ShieldServer.tasklist.ProcessList;
import me.FurH.SkyShield.packets.scan.Packet3ScanUser;
import org.json.JSONObject;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CurrentScan {
    
    long start = System.currentTimeMillis();
    public HandlerListener handler;
   
    public int pid;

    ServerStage scanstage;
    private String currentKey;

    private Packet3ScanUser request;
    private int protocol;

    private CmdLine cmdline;
    int protocolindex;

    HashSet<String> askcerts;
    HashSet<String> filelist;
    Iterator<String> askiterator;
    
    RevisionList revision;
    private HashMap<VersionEntry, Integer> checks;
    
    private String javahome;
    
    private final JSONObject json = new JSONObject();
    TreeMap<String, FileInfo> fileinfos;
    FileInfo nexthash;
   
    int totalchecks;
    int checkfiles = 0;
    int currentporcent = 0;

    int error_code;
    int userid;
    
    private double result = 0;
    int scanid;
    boolean finished;
    
    private UUID randomid;
    private UUID sessionId;
    ProcessList tasklist;
    
    ArrayList<String> modules;

    HashMap<String, FileCrawler> crawler;
    FileCrawler curcrawler;
    
    boolean unique;
            
    UUID getSessionId() {
        return this.sessionId;
    }
    
    void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
        json.put("session", sessionId);
    }
    
    UUID getRandomId() {
        
        if (randomid == null) {
            randomid = UUID.randomUUID();
        }
        
        return randomid;
    }
    
    double getResult() {
        return this.result;
    }
    
    void setResult(double result) {
        this.result = result;
        json.put("result", result);
    }

    void setJavaVer(String javaver) {
        json.put("javaver", javaver);
    }
    
    String getJavaHome() {
        return this.javahome;
    }
    
    void setJavaHome(String javahome) {
        
        javahome = path(javahome);
        
        this.javahome = javahome;
        json.put("javahome", javahome);

        String javaw = cmdline.javaw;

        int j1 = javaw.lastIndexOf("bin");
        String javawhome = javaw;

        if (j1 >= 0) {
            javawhome = javaw.substring(0, j1);
        }
        
        json.put("javawdir", javawhome);
    }

    String getRequestName() {
        return request.username;
    }
    
    UUID getRequestUUID() {
        return request.uniqueId;
    }
    
    CmdLine getCmdLine() {
        return this.cmdline;
    }
    
    void setCmdLine(CmdLine cmdline) {

        this.cmdline = cmdline;
        this.pid = cmdline.pid;

        json.put("pid", cmdline.pid);
        json.put("cmdline", cmdline.toJson()); // @TODO: COMPRESSED
    }

    void setMapEntry(MapEntry mapentry) {
        json.put("protocol_label", mapentry.label);
    }

    void setProtocol(int protocol) {
        this.protocol = protocol;
        json.put("protocol", protocol);
    }
    
    int getProtocol() {
        return this.protocol;
    }
    
    void errorCode(int error_code) {
        
        this.setResult(0d);
        this.error_code = error_code;
        
        json.put("errorcode", error_code);
    }
    
    void setRequest(Packet3ScanUser request) {
        
        this.request = request;
        
        JSONObject data = new JSONObject();

        data.put("uuid", request.uniqueId);
        data.put("username", request.username);
        data.put("port", request.remortport[0]);

        json.put("request", data);
    }

    boolean namesMatch() {
        return request.username.equals(cmdline.username);
    }

    String serialize() {

        if (checks != null) {
            json.put("pathchecks", checks);
        }

        json.put("took", System.currentTimeMillis() - start);
        
        if (fileinfos != null) {

            TreeMap<FileType, TreeMap<String, JSONObject>> sorted = new TreeMap<>();

            for (Entry<String, FileInfo> infos : fileinfos.entrySet()) {
                
                TreeMap<String, JSONObject> tree = sorted.get(infos.getValue().type);
                
                if (tree == null) {
                    tree = new TreeMap<>();
                    sorted.put(infos.getValue().type, tree);
                }

                tree.put(infos.getKey(), infos.getValue().toJson());
            }

            json.put("files", sorted);
        }

        return json.toString();
    }

    String newKey() {
        this.currentKey = Encrypto.genRndPass(16);
        return currentKey;
    }

    String currentKey() {
        
        String key = this.currentKey;
        this.currentKey = null;
        
        return key;
    }

    void logFileList(List<String> filelist) {
        json.put("filelist", filelist);
    }

    void setPathChecks(HashMap<VersionEntry, Integer> checks) {
        this.checks = checks;
    }
    
    HashMap<VersionEntry, Integer> getCheckList() {
        return this.checks;
    }
    
    String checkToString() {
        
        if (checks == null) {
            return null;
        }
        
        return checks.toString();
    }

    void setFileError(String hash, String path) {

        JSONObject data = new JSONObject();
        data.put("path", path);
        data.put("hash", hash);

        json.put("fileerror", data);
    }
}