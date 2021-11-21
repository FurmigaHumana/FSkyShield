/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.scans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.number.NumberUtils;
import me.FurH.ShieldServer.MainServer;
import me.FurH.ShieldServer.bigdata.BigData;
import me.FurH.ShieldServer.database.DatabaseManager;
import static me.FurH.ShieldServer.database.DatabaseManager.heavy;
import me.FurH.ShieldServer.listener.ClientListener;
import me.FurH.ShieldServer.server.ShieldClient;
import me.FurH.ShieldServer.sources.SourceManager;
import me.FurH.ShieldServer.sources.SourceResult;
import me.FurH.SkyShield.packets.cls.Packet56RequestSources;
import me.FurH.SkyShield.packets.cls.Packet57SourceResult;
import me.FurH.SkyShield.packets.scan.Packet36ScanHeader;
import me.FurH.SkyShield.packets.scan.Packet39AgentHash;
import me.FurH.SkyShield.packets.scan.Packet40AccessDenied;
import me.FurH.SkyShield.packets.scan.Packet41ClassLoad;
import me.FurH.SkyShield.packets.scan.ZipHash2;
import me.FurH.SkyShield.packets.scan2.Packet46ScanHeader2;
import me.FurH.SkyShield.packets.setup.Packet35AgentData;

/**
 *
 * @author lgpse
 */
public class ScanData {
    
    private static final HashSet<String> validsigned;
    private static final HashSet<String> ignoresigned;
    
    static {
        
        validsigned = new HashSet<>();
        ignoresigned = new HashSet<>();
        
        validsigned.add("@@REMOVED.jar");
        validsigned.add("@@REMOVED.jar");
        validsigned.add("@@REMOVED.jar");
        validsigned.add("@@REMOVED.jar");
        
        ignoresigned.add("@@REMOVED.jar");
    }
    
    public final int userid;
    public final UUID uuid;
    public final String username;
    public final String cmdname;
    public final String version;
    public final int loadcount;
    
    public String commandline;

//    public final UUID scanid;
    private final long start;

    public int totalfiles = 0;
    public int totaltests = 0;
    public int totalerrors = 0;
    public int totallibs = 0;
    public int totalgame = 0;

    private final LinkedHashMap<String, Integer> versions = new LinkedHashMap<>();
    public double result = 100.0d;
    
    public String baseversion;
    public ArrayList<String> extraversions;
    
    // source retrieving
    
    public final HashMap<String, ArrayList<ZipHash2>> sourcerequest = new HashMap<>();
    public final HashMap<String, ArrayList<String>> sourcemap = new HashMap<>();
    public final HashMap<String, String> sourcenames = new HashMap<>();

    // errors and certificates
    
    public final HashMap<String, ArrayList<String>> errorfiles = new HashMap<>();
    public final HashMap<String, ArrayList<String>> signedskip = new HashMap<>();
    public final HashMap<String, CertData> certificates = new HashMap<>();
    
    public boolean validagent = false;
    
    public final ArrayList<String> denied = new ArrayList<>();
    public int scan = 0;
    
    public final HashSet<String> postload = new HashSet<>();
    public long tookms;
    
    public final ArrayList<String> blacklist = new ArrayList<>();
    
    public final ArrayList<String> filelist = new ArrayList<>();
    
    public final HashSet<String> filequeue = new HashSet<>();
    public boolean completed = false;
    public int pid;
    
    public String packetCls;
    public int cmdlineid;
    public int clslistid;
    
    /*public Integer playid;
    
    public int getUUID(SQLThread t) throws SQLException {
        
        if (playid == null) {
            playid = IDs.getUniqueID(uuid, true, t);
        }
        
        return playid;
    }*/

    public ScanData(int userId, Packet36ScanHeader header) {
        this(userId, header.username, header.uuid, header.cmdname, header.version, header.loadcount);
        this.commandline = header.commandline;
    }

    public ScanData(int userId, Packet46ScanHeader2 header) {
        this(userId, header.username, header.uuid, header.cmdname, header.version, header.loadcount);
    }

    public ScanData(int userId, String username, UUID uuid, String cmdname, String version, int loadcount) {
//        this.scanid = UUID.randomUUID();
        this.userid = userId;
        this.start = System.currentTimeMillis();
        this.username = username;
        this.uuid = uuid;
        this.cmdname = cmdname;
        this.version = version;
        this.loadcount = loadcount;
    }

    private void compact() {
        packetCls = null;
        commandline = null;
        versions.clear();
        extraversions = null;
        errorfiles.clear();
        signedskip.clear();
        certificates.clear();
        denied.clear();
        blacklist.clear();
        filelist.clear();
        filequeue.clear();
        validagent = false;
        result = 0.0d;
        baseversion = null;
        extraversions = null;
    }

    public void accessDenied(Packet40AccessDenied packet) {
        denied.add(packet.file);
    }
    
    public void agentHash(Packet39AgentHash packet, Packet35AgentData agent) {
        
        filequeue.remove("@@REMOVED");
        
        if (packet.checksum.equals(agent.md5)) {
            validagent = true;
        } else {
            errorFile("@@REMOVED.jar", packet.checksum, agent.data.length, "@@REMOVED");
        }
    }
    
    public boolean classLoad(Packet41ClassLoad packet) {

        /*if (MainServer.instance.manager.isKnown(packet.clsname)) {
            return false;
        }*/

        synchronized (postload) {
            return postload.add(packet.clsname);
        }
    }

    public void scanHashes(ArrayList<ZipHash2> hashes, boolean usesize) {
        
        totalfiles++;
        
        for (ZipHash2 entry : hashes) {
            scanHashes("", null, null, 0, entry, usesize);
        }
    }

    public void scanHashes(String file, String checksum, CertData cert, long size, ZipHash2 entry, boolean usesize) {

        if (cert != null) {

            certificates.put(file + "/" + checksum, cert);
            
            if ((cert.signed && validsigned.contains(file)) || ignoresigned.contains(file)) {

                String key = file + "/" + checksum + "/" + size;
                ArrayList<String> list = signedskip.get(key);

                if (list == null) {
                    list = new ArrayList<>();
                    signedskip.put(key, list);
                }

                list.add(entry.name);

                return;
            }
        }

        MainServer server = MainServer.instance;
        DatabaseManager manager = server.database;
        
        totaltests++;
                    
        String hex = Encrypto.hex(entry.md5);
        
        if (manager.isBlacklisted(hex) || manager.isBlacklisted(entry.name)) {

            blacklist.add(entry.name);
            return;
        }
        
        String info = manager.getHexInfo(hex);

        if (info == null && manager.isLibrary(hex)) {

            totallibs++;

        } else {

            if (info != null) {

                totalgame++;

                Integer count = versions.get(info);
                if (count == null) {
                    count = 0;
                }

                count++;
                versions.put(info, count);

            } else {
                
                totalerrors++;
                errorFile(file, checksum, size, entry.name);

                ArrayList<ZipHash2> hashes = sourcerequest.get(checksum);

                if (hashes == null) {
                    hashes = new ArrayList<>();
                    sourcerequest.put(checksum, hashes);
                }

                hashes.add(entry);
                sourcenames.put(checksum, file);
            }
        }
    }
    
    private void errorFile(String file, String checksum, long size, String entryname) {
        
        String key = file + "/" + checksum + "/" + size;
        ArrayList<String> list = errorfiles.get(key);

        if (list == null) {
            list = new ArrayList<>();
            errorfiles.put(key, list);
        }

        list.add(entryname);
    }

    public void scanCompleted() {

        if (!blacklist.isEmpty()) {
            
            result = 0.0D;
            
        } else {

            if (!validagent || totalgame < 2400) {

                result = 0.0D;

            } else {

                result = totalgame;

                if (totalerrors > 0) {
                    result = 100.0D - NumberUtils.getWorkDoneDouble(totalerrors, Math.min(totalgame, loadcount));
                }
            }
        }
        
        if (totalerrors > 300) {
            result = 0.0d;
        }

        if (result > 100) {
            result = 100.0;
        } else if (result <= 0) {
            result = 0.0;
        }
        
        String base = null;
        int lastc = 0;
        
        for (Entry<String, Integer> entry : versions.entrySet()) {
            if (base == null || entry.getValue() > lastc) {
                base = entry.getKey();
                lastc = entry.getValue();
            }
        }
        
        baseversion = (base == null ? "null" : base);
        
        if (base != null) {
            versions.remove(base);
        }
        
        extraversions = new ArrayList<>(versions.keySet());
        tookms = ( System.currentTimeMillis() - start );
    }

    public String errorFileStr() {
        return serialize(errorfiles);
    }
    
    public String signFileStr() {
        return serialize(signedskip);
    }
    
    private String serialize(HashMap<String, ArrayList<String>> map) {
        
        StringBuilder sb = new StringBuilder();

        for (Entry<String, ArrayList<String>> entry : map.entrySet()) {
            
            sb.append(entry.getKey()).append(": [");
            boolean first = true;

            for (String cls : entry.getValue()) {
                sb.append(!first ? "," : "").append(cls);
                first = false;
            }

            sb.append("]\n");
        }
        
        return sb.toString();
    }

    public String certToStr() {

        StringBuilder sb = new StringBuilder();
        
        for (Entry<String, CertData> entry : certificates.entrySet()) {
            
            String file = entry.getKey();
            CertData certificate = entry.getValue();

            sb.append(file).append("/").append(certificate.signed)
                    .append(": $X|").append(certificate.sigdata).append("|X$");
        }

        return sb.toString();
    }

    public void requestSources(int pid, int build, ShieldClient client, ClientListener listener) {
        
        final double retd = result;
        compact();
        
        if (retd < 92 || build < 995) {
            cleanup();
            return;
        }

        SQL.mslow(new SQLTask() {
            
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                
                Iterator<Entry<String, ArrayList<ZipHash2>>> it = sourcerequest.entrySet().iterator();
                int playid = userid;
                
                while (it.hasNext()) {

                    Entry<String, ArrayList<ZipHash2>> entry = it.next();
                    
                    ArrayList<ZipHash2> entries = entry.getValue();
                    Iterator<ZipHash2> it2 = entries.iterator();

                    ArrayList<String> sources = getSourceList(entry.getKey());

                    while (it2.hasNext()) {

                        ZipHash2 next = it2.next();
                        
                        String hash = Encrypto.hex(next.md5);
                        next.md5cache = hash;
                        
                        int id = BigData.fetchIdByHash(hash, t);

                        if (id > 0) {
                            
                            sources.add(next.name + "|$-" + id);
                            it2.remove();
                            
                            MainServer.instance.sources.checkCompare(client, playid, id, next.name, t);
                        }
                    }
                    
                    if (entries.isEmpty()) {
                        it.remove();
                    }
                }
                                
                if (sourcerequest.isEmpty()) {
                    listener.insertSources(pid, ScanData.this);
                    return;
                }
                
                Packet56RequestSources packet = new Packet56RequestSources();

                packet.pid = pid;
                packet.requests = sourcerequest;

                client.write(packet);
            }
        });
    }

    public void sourceResult(int pid, Packet57SourceResult packet, ClientListener listener) {

        SQL.mslow(new SQLTask() {
            
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                
                ArrayList<ZipHash2> list = sourcerequest.get(packet.file);
                Iterator<ZipHash2> it = list.iterator();

                ArrayList<String> sources = getSourceList(packet.file);

                while (it.hasNext()) {

                    ZipHash2 next = it.next();
                    
                    if (!packet.exists) {

                        sources.add(next.name + "|$-err1");
                        it.remove();
                        
                    } else {

                        if (!next.md5cache.equals(packet.hash)) {
                            continue;
                        }

                        if (!packet.found) {
                            
                            sources.add(next.name + "|$-err2");
                            it.remove();
                            
                            continue;
                        }
                        
                        int id = BigData.fetchIdByHash(packet.hash, t);
                        
                        if (id > 0) {

                            sources.add(next.name + "|$-" + id);
                            it.remove();
                            
                        } else {

                            heavy(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        decompileHeavy(pid, packet, listener, list, next);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }

                checkSources(pid, packet, listener);
            }
        });
    }
    
    private void decompileHeavy(int pid, Packet57SourceResult packet, ClientListener listener, ArrayList<ZipHash2> list, ZipHash2 next) throws IOException {
        
        MainServer server = MainServer.instance;
        SourceManager srcs = server.sources;

        SourceResult srcret = srcs.heavyDecompile(listener.client, next.name, packet.hash, packet.data);
        
        SQL.mslow(new SQLTask() {

            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                
                ArrayList<String> sources = getSourceList(packet.file);
                
                int playid = userid;
                int id = BigData.store(packet.hash, srcret.source, null, playid, t);
                
                srcret.storeCompare(playid, id, t);

                sources.add(next.name + "|$-" + id);
                list.remove(next);
                
                checkSources(pid, packet, listener);
            }
        });
    }

    private void checkSources(int pid, Packet57SourceResult packet, ClientListener listener) {
        
        ArrayList<ZipHash2> list = sourcerequest.get(packet.file);
                
        if (list == null || list.isEmpty()) {
            sourcerequest.remove(packet.file);
        }

        if (sourcerequest.isEmpty()) {
            listener.insertSources(pid, this);
        }
    }
    
    private ArrayList<String> getSourceList(String checksum) {
        
        String name = sourcenames.get(checksum);
        ArrayList<String> sources = sourcemap.get(name);

        if (sources == null) {
            sources = new ArrayList<>();
            sourcemap.put(name, sources);
        }

        return sources;
    }

    public String sourcesStr() {
        
        StringBuilder sb = new StringBuilder();
        
        for (Entry<String, ArrayList<String>> entry : sourcemap.entrySet()) {
            
            sb.append(entry.getKey()).append(": [");
            boolean first = true;

            for (String cls : entry.getValue()) {
                sb.append(!first ? "%^" : "").append(cls);
                first = false;
            }

            sb.append("]&|x");
        }
        
        return sb.toString();
    }

    public void cleanup() {
        sourcerequest.clear();
        sourcenames.clear();
        sourcemap.clear();
    }
}