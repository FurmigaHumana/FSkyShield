/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.reference.ExpirableReference;
import me.FurH.Core.streams.ComprInputStream;
import me.FurH.Core.util.Callback;
import me.FurH.Core.util.Utils;
import me.FurH.ShieldServer.Stats;
import me.FurH.ShieldServer.bigdata.BigData;
import static me.FurH.ShieldServer.newlistener.Resources.getBuildVersion;
import me.FurH.ShieldServer.scans.CertData;
import me.FurH.ShieldServer.scans.ScanData;
import me.FurH.SkyShield.api.API31PrintScreen;
import me.FurH.SkyShield.api.API32PrintResult;
import me.FurH.SkyShield.encoder.Compressor;
import me.FurH.SkyShield.handler.Shield22RequestScan;
import me.FurH.SkyShield.handler.Shield23ScanResult;
import me.FurH.SkyShield.handler.Shield65PayloadData;
import me.FurH.SkyShield.packets.Packet0Handshake;
import me.FurH.SkyShield.packets.big.Packet47OfferBigData;
import me.FurH.SkyShield.packets.big.Packet49BigStore;
import me.FurH.SkyShield.packets.cls.Packet57SourceResult;
import me.FurH.SkyShield.packets.scan.Packet36ScanHeader;
import me.FurH.SkyShield.packets.scan.Packet37JarHash;
import me.FurH.SkyShield.packets.scan.Packet38ScanComplete;
import me.FurH.SkyShield.packets.scan.Packet39AgentHash;
import me.FurH.SkyShield.packets.scan.Packet3ScanUser;
import me.FurH.SkyShield.packets.scan.Packet40AccessDenied;
import me.FurH.SkyShield.packets.scan.Packet41ClassLoad;
import me.FurH.SkyShield.packets.scan.Packet42PidOff;
import me.FurH.SkyShield.packets.scan.Packet43BigScanComplete;
import me.FurH.SkyShield.packets.scan.Packet45JarHash2;
import me.FurH.SkyShield.packets.scan.ZipHash2;
import me.FurH.SkyShield.packets.scan2.Packet46ScanHeader2;
import me.FurH.SkyShield.packets.scan2.Packet50ScanCmdLine;
import me.FurH.SkyShield.packets.scan2.Packet51ScanComplete2;
import me.FurH.SkyShield.packets.scan2.Packet52ClassList;
import me.FurH.SkyShield.packets.scan2.Packet53OfferFile;
import me.FurH.SkyShield.packets.scan2.Packet54FileResult;
import me.FurH.SkyShield.packets.scan2.Packet55StoreFile;
import me.FurH.SkyShield.packets.setup.Packet35AgentData;
import me.FurH.SkyShield.packets.update.Packet58ClientUpdate;
import me.FurH.SkyShield.shot.Shield71ScreenShot;
import me.FurH.SkyShield.shot.Shield72ScreenResult;

/**
 *
 * @author lgpse
 */
public class ClientListener extends AbstractClientListener {
    
    private static final HashMap<UUID, Integer> temprequest = new HashMap<>();
    private static final HashMap<String, Integer> temprequest2 = new HashMap<>();

    private static ExpirableReference<Packet58ClientUpdate> clientupdate;
    private static ExpirableReference<Packet35AgentData> agent;
    private static int localbuild;
    
    private final HashMap<Integer, ScanData> lpids;

    public ClientListener(HandshakeListener base) {
        super(base);
        this.lpids = new HashMap<>();
    }
    
    @Override
    public void userQuit() {
        
        lpids.clear();
        temprequest.clear();
        
        super.userQuit();
    }
    
    @Override
    public void handshake(Packet0Handshake packet) {

        super.handshake(packet);

        client.write(getAgent());

        if (packet.clienthash != null && !packet.clienthash.isEmpty()) {
            Packet58ClientUpdate update = getClientUpdate();
            if (localbuild >= buildnumber && !update.md5.equals(packet.clienthash)) {
                client.write(update);
            }
        }
    }

    private Packet58ClientUpdate getClientUpdate() {
        
        Packet58ClientUpdate packet = null;
        
        if (clientupdate != null) {
            packet = clientupdate.getRaw();
        }
        
        if (packet == null) {
            
            packet = new Packet58ClientUpdate();
            
            try {
                
                File file = new File("resources", "old-client.jar");
                localbuild = getBuildVersion(file);
                                
                packet.data = FileUtils.getBytesFromFile(file);
                packet.md5 = Encrypto.hash("MD5", packet.data);

                clientupdate = new ExpirableReference<>(packet, 1, TimeUnit.HOURS, null);
                
            } catch (Exception ex) {
                
                ex.printStackTrace();
                
            }
        }
        
        return packet;
    }
    
    @Override
    public void scanRequest(Shield22RequestScan request, Packet3ScanUser scan, HandlerListener handler, boolean unique) {

        temprequest.put(request.uuid, request.userId);
        temprequest2.put(request.username, request.userId);

        client.write(scan);

        super.scanRequest(request, scan, handler, unique);
    }
    
    @Override
    public void authenticate(Shield23ScanResult result) {
        
        ScanData data = null;
        
        for (ScanData test : lpids.values()) {
            if (test.uuid.equals(result.uniqueId)) {
                data = test;
                break;
            }
        }

        if (data == null) {
            return;
        }

        registerClient(result.uniqueId);        
        saveScan(data);
    }

    @Override
    public void disconnected() {
        
        lpids.clear();
        
        super.disconnected();
    }

    @Override
    public void pidOff(Packet42PidOff packet) {
        ScanData data = lpids.remove(packet.pid);
        if (data != null) {
            invalidate(data.scan, data.userid);
        }
    }

    private Packet35AgentData getAgent() {

        Packet35AgentData result = null;
        
        if (agent != null) {
            result = agent.getRaw();
        }

        if (result == null) {
            
            try {
                
                result = new Packet35AgentData();
                
                result.data = FileUtils.getBytesFromFile(new File("resources", "FSkyShieldAgent-out.jar"));
                result.md5 = Encrypto.hash("MD5", result.data);
                
                agent = new ExpirableReference<>(result, 1, TimeUnit.HOURS, null);
                
            } catch (Exception ex) {
                
                ex.printStackTrace();
                
            }
        }
        
        return result;
    }

    @Override
    public void scanHeader(Packet36ScanHeader packet) {
        newScan(packet.pid, new ScanData(temprequest.get(packet.uuid), packet));
    }

    @Override
    public void scanHashes(Packet37JarHash packet) {
        ScanData data = lpids.get(packet.pid);
        if (data != null) {
            data.scanHashes(packet.hashes, false);
        }
    }
    
    @Override
    public void scanHashes2(Packet45JarHash2 packet) {
        ScanData data = lpids.get(packet.pid);
        if (data != null) {
            data.scanHashes(packet.hashes, true);
        }
    }

    @Override
    public void bigComplete(Packet43BigScanComplete packet) {
        scanCompleted(packet);
    }

    @Override
    public void scanCompleted(Packet38ScanComplete packet) {
        try {
            scanCompleted(packet.pid, packet.getClassList());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void scanCompleted(int pid, String packetCls) {

        final ScanData data = lpids.get(pid);

        if (data != null) {
            
            data.pid = pid;

            if (data.completed || data.scan > 0) {
                return;
            }

            data.completed = true;
            
            if (!data.filequeue.isEmpty()) {
                return;
            }
            
            data.packetCls = packetCls;
            data.scanCompleted();
            
            addScanning(data.uuid);

            Shield23ScanResult result = new Shield23ScanResult();

            result.randomId = UUID.randomUUID();
            result.uniqueId = data.uuid;
            result.result = data.result;

            if (callback != null) {
                callback.invoke(result);
            }
        }
    }
    
    private void saveScan(ScanData data) {
        
        SQL.mslow(new SQLTask() {

            @Override
            public void execute(SQLDb database, SQLThread t) throws Throwable {

                if (data.scan > 0) {
                    return;
                }

                Stats.scans++;

                PreparedStatement ps = null;
                ResultSet rs = null;

                try {

                    int playid = data.userid;

                    int errorfiles = BigData.store(data.errorFileStr(), playid, t);
                    int signfiles = BigData.store(data.signFileStr(), playid, t);
                    int certificates = BigData.store(data.certToStr(), playid, t);

                    int denied = BigData.store(data.denied.toString(), playid, t);

                    int clslist;
                    int cmdline;

                    String packetCls = data.packetCls;
                    data.packetCls = null;

                    if (packetCls != null) {
                        clslist = BigData.store(packetCls, playid, t);
                    } else {
                        clslist = data.clslistid;
                    }

                    if (data.commandline != null) {
                        cmdline = BigData.store(data.commandline, playid, t);
                    } else {
                        cmdline = data.cmdlineid;
                    }

                    ps = t.prepareAutoId("@@REMOVED");

                    t.commitNext();
                    int i = 1;

                    ps.setInt(i++, data.pid);
                    ps.setInt(i++, clientid);
                    ps.setInt(i++, playid);

                    ps.setString(i++, data.version);
                    ps.setString(i++, data.baseversion);
                    ps.setString(i++, data.extraversions.toString());

                    ps.setInt(i++, data.totalfiles);
                    ps.setInt(i++, data.totaltests);
                    ps.setInt(i++, data.totalerrors);
                    ps.setInt(i++, data.totallibs);
                    ps.setInt(i++, data.totalgame);
                    ps.setInt(i++, data.loadcount);

                    ps.setDouble(i++, data.result);
                    ps.setString(i++, data.blacklist.toString());

                    ps.setInt(i++, errorfiles);
                    ps.setInt(i++, denied);

                    ps.setBoolean(i++, true);
                    ps.setInt(i++, buildnumber);
                    ps.setLong(i++, data.tookms);
                    ps.setBoolean(i++, data.validagent);
                    ps.setInt(i++, cmdline);
                    ps.setInt(i++, clslist);
                    ps.setInt(i++, signfiles);
                    ps.setInt(i++, certificates);
                    ps.setLong(i++, Utils.currentTimeMillis() / 1000l);

                    ps.execute();

                    rs = ps.getGeneratedKeys();

                    if (!rs.next()) {
                        System.err.println("Failed to generate scan keys [ 1 ]");
                        return;
                    }

                    int scanid = rs.getInt(1);
                    data.scan = scanid;

                    writeShotRequest(data, null);
                    data.requestSources(data.pid, buildnumber, client, ClientListener.this);

                    logScanId(scanid, data.userid);
                    
                } finally {

                    Closer.closeQuietly(ps, rs);

                }
            }
        });
    }

    @Override
    public void agentHash(Packet39AgentHash packet) {
        ScanData data = lpids.get(packet.pid);
        if (data != null) {
            data.agentHash(packet, getAgent());
            checkWaitingFiles(packet.pid, data);
        }
    }
    
    @Override
    public void accessDenied(Packet40AccessDenied packet) {
        ScanData data = lpids.get(packet.pid);
        if (data != null) {
            data.accessDenied(packet);
        }
    }

    @Override
    public void payloadData(Shield65PayloadData packet) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void classLoad(Packet41ClassLoad packet) {
        
        ScanData data = lpids.get(packet.pid);
       
        if (data != null && data.scan > 0) {
            
            if (data.classLoad(packet)) {
                
                SQL.mslow(new SQLTask() {

                    @Override
                    public void execute(SQLDb database, SQLThread t) throws Throwable {

                        PreparedStatement ps = null;

                        try {

                            String str;
                            synchronized (data.postload) {
                                str = data.postload.toString();
                            }

                            ps = t.prepare("@@REMOVED");
                            t.commitNext();

                            long now = Utils.currentTimeMillis() / 1000l;
                            
                            byte[] cpr = Compressor.encode(str);

                            ps.setInt(1, data.scan);
                            ps.setBytes(2, cpr);
                            ps.setLong(3, now);
                            ps.setBytes(4, cpr);
                            ps.setLong(5, now);

                            ps.execute();

                        } finally {

                            Closer.closeQuietly(ps);

                        }
                    }
                });
            }
        }
    }

    @Override
    public void scanHeader2(Packet46ScanHeader2 packet) {

        Integer id = temprequest.get(packet.uuid);
        if (id == null) {
            id = temprequest2.get(packet.username);
        }

        newScan(packet.pid, new ScanData(id, packet));
    }
    
    private void newScan(int pid, ScanData data) {
        
        if (buildnumber >= 995) {
            data.filequeue.add("agent");
        }

        lpids.put(pid, data);
    }

    @Override
    public void offerBigData(Packet47OfferBigData packet) {
        BigData.offerBigData(client, packet);
    }
    
    @Override
    public void bigStore(Packet49BigStore packet) {
        SQL.mslow(new SQLTask() {
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                int playid = getCachedIdOrAny(t);
                BigData.bigStore(client, packet, playid, t);
            }
        });
    }
    
    private int getCachedIdOrAny(SQLThread t) throws SQLException {

        for (ScanData data : lpids.values()) {
            return data.userid;
        }

        return 0;
    }
    
    @Override
    public void setCmdLine(Packet50ScanCmdLine packet) {
        
        ScanData data = lpids.get(packet.pid);
        
        if (data == null) {
            return;
        }

        if (data.scan > 0) {
            updateBigId(data.scan, packet.bigid, "cmdline");
        } else {
            data.cmdlineid = packet.bigid;
        }
    }

    @Override
    public void scanCompleted2(Packet51ScanComplete2 packet) {
        scanCompleted(packet.pid, null);
    }

    @Override
    public void classList(Packet52ClassList packet) {
        
        ScanData data = lpids.get(packet.pid);
        
        if (data == null) {
            return;
        }

        if (data.scan > 0) {
            updateBigId(data.scan, packet.bigid, "clslist");
        } else {
            data.clslistid = packet.bigid;
        }
    }

    private void updateBigId(int scanid, int bigid, String field) {
        
        SQL.mslow(new SQLTask() {
            
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                
                PreparedStatement ps = null;

                try {

                    ps = t.prepare("@@REMOVED");
                    t.commitNext();

                    ps.setInt(1, bigid);
                    ps.setInt(2, scanid);

                    ps.execute();
                    
                } finally {

                    Closer.closeQuietly(ps);

                }
            }
        });
    }
    
    @Override
    public void offerFile(Packet53OfferFile packet) {

        ScanData data = lpids.get(packet.pid);

        if (data == null) {
            return;
        }

        data.filelist.add(packet.file);
        data.filequeue.add(packet.hash);

        BigData.offerBigData(packet.hash, new Callback<Integer>() {

            @Override
            public void invoke(Integer id) {

                writeFileResult(packet.pid, id, packet.hash);

                if (id > 0) {
                    fromCache(packet.pid, packet.hash, id, data);
                }
            }
        });
    }
    
    private void writeFileResult(int pid, int id, String hash) {
        
        Packet54FileResult result = new Packet54FileResult();

        result.pid = pid;
        result.hash = hash;
        result.bigid = id;

        client.write(result);
    }
    
    @Override
    public void storeFile(Packet55StoreFile packet) {
        
        ScanData data = lpids.get(packet.pid);

        if (data == null) {
            return;
        }
        
        try {
            fromCache(0, packet.pid, packet.hash, packet.checksum, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SQL.mslow(new SQLTask() {
            
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                
                int playid = data.userid;
                int id = BigData.store(packet.hash, null, packet.checksum, playid, t);
                
                if (id > 0) {
                    writeFileResult(packet.pid, id, packet.hash);
                }
            }
        });
    }
    
    private void fromCache(int pid, String hash, int bigid, ScanData data) {
        SQL.mslow(new SQLTask() {
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                byte[] fetch = BigData.fetch(bigid, t);
                if (fetch != null) {
                    fromCache(bigid, pid, hash, fetch, data);
                } else {
                    client.error("Missing fetch from file cache");
                }
            }
        });
    }
    
    private void fromCache(int bigid, int pid, String key, byte[] hash, ScanData data) throws IOException {

        ComprInputStream cis = null;
        DataInputStream dis = null;
        
        try {
        
            ByteArrayInputStream bais = new ByteArrayInputStream(hash);

            int ch1 = bais.read();
            int ch2 = bais.read();
            int ch3 = bais.read();
            int ch4 = bais.read();

            int size = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4));

            cis = new ComprInputStream(bais);
            dis = new DataInputStream(cis);

            String file = dis.readUTF();
            String checksum = dis.readUTF();
            long length = dis.readLong();

            boolean signed = dis.readBoolean();
            String sigdata = dis.readUTF();
            
            CertData cert = new CertData(signed, sigdata);
            
            data.totalfiles++;

            for (int j1 = 0; j1 < size; j1++) {
                
                ZipHash2 zhash = new ZipHash2(true);
                zhash.read(dis);

                data.scanHashes(file, checksum, cert, length, zhash, true);
            }

            data.filequeue.remove(key);
            checkWaitingFiles(pid, data);
            
        } catch (Throwable ex) {

            client.error("CORRUPTED CACHE: " + bigid);
            ex.printStackTrace();
            
        } finally {
            
            Closer.closeQuietly(dis);
            Closer.closeQuietly(cis);
            
        }
    }
    
    private void checkWaitingFiles(int pid, ScanData data) {
        if (data.completed && data.filequeue.isEmpty()) {
            this.scanCompleted(pid, null);
        }
    }
    
    @Override
    public void sourceResult(Packet57SourceResult packet) {
        
        ScanData data = lpids.get(packet.pid);

        if (data == null) {
            return;
        }
        
        data.sourceResult(packet.pid, packet, this);
    }
    
    public void insertSources(int pid, ScanData data) {
       
        SQL.mslow(new SQLTask() {
          
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                
                int playid = data.userid;
                int id = BigData.store(data.sourcesStr(), playid, t);
                
                updateBigId(pid, id, "sources");
                
                data.cleanup();
            }
        });
    }
    
    @Override
    public void printRequest(Shield71ScreenShot packet, HandlerListener handler) {
        
        ScanData data = null;

        for (ScanData temp : lpids.values()) {
            data = temp;
            break;
        }

        if (data == null) {
            client.error("No scan to screenshot");
            return;
        }

        writeShotRequest(data, packet.requester);
    }

    @Override
    public void printResult(final API32PrintResult packet) {

        final String msgcopy = packet.message;

        if (!packet.error) {
            packet.message = "Screenshot: " + packet.message;
        }

        Shield72ScreenResult result = new Shield72ScreenResult();

        result.requester = packet.requester;
        result.error = packet.error;
        result.message = packet.message;

        client.server.writeHandler(result);
        
        if (packet.error) {
            return;
        }
        
        ScanData data = lpids.get(packet.scan); // this is actualy the pid
        
        if (data == null) {
            return;
        }

        appendShot(data.scan, msgcopy);
    }

    private void writeShotRequest(ScanData scan, UUID requester) {

        API31PrintScreen packet = new API31PrintScreen();
        
        if (requester == null) {
            requester = UUID.randomUUID();
        }

        packet.handler = "";
        packet.requester = requester;
        packet.uniqueId = scan.uuid;
        packet.username = "";
        packet.pid = scan.pid;
        packet.address = "";
        packet.scan = scan.pid;

        client.write(packet);
    }
}