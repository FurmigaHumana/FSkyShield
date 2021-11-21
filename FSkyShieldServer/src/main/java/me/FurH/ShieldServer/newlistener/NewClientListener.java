package me.FurH.ShieldServer.newlistener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import me.FurH.Core.cache.soft.SoftMap;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.util.Utils;
import me.FurH.ShieldServer.MainServer;
import me.FurH.ShieldServer.Stats;
import me.FurH.ShieldServer.filelist.FileCrawler;
import me.FurH.ShieldServer.filelist.FileObject;
import me.FurH.ShieldServer.listener.HandlerListener;
import me.FurH.ShieldServer.listener.HandshakeListener;
import me.FurH.ShieldServer.newdb.FileEntry;
import me.FurH.ShieldServer.newdb.ProtocolList;
import me.FurH.ShieldServer.newdb.RevisionList;
import me.FurH.ShieldServer.newdb.VersionEntry;
import static me.FurH.ShieldServer.newlistener.CmdLine.path;
import me.FurH.ShieldServer.protocol.MapEntry;
import me.FurH.ShieldServer.protocol.MapList;
import me.FurH.ShieldServer.protocol.ProtocolEntry;
import me.FurH.ShieldServer.tasklist.ProcessList;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_certificatewrongtime;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_filecomputedalready;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_multipleagents;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_noagentsdetected;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_pidoff;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_unkownfiledetected;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_unrequestedfile;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_userquitserver;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_wrongagentcertificate;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_wrongfilesequence;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_wronghashpid;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_wrongjavacertificate;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askfilelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askhash;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askid;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askziphash;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_fileinfo;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_filelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_handshake;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_modulelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_processlist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_takeshot;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_ziphash;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_attachwrongtime;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_brokenpayload;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_disconnected;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_errordecodehash;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_faileddecodehandshake;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_failedwritepayload;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_fileencodeerr;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_filehashwrongtime;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_filelistwrongtime;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_ignore;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_invalidcryptpid;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_invalidfile1;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_namesmismatch;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_parsecmdline;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_payloadwronguuid;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_restarted;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_unsuportedprotocol;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_unsuportedrev;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_unsuportedver;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_wrongattachtime;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_wrongfilehash;
import me.FurH.SkyShield.encoder.Compressor;
import me.FurH.SkyShield.handler.Shield22RequestScan;
import me.FurH.SkyShield.handler.Shield23ScanResult;
import me.FurH.SkyShield.handler.Shield65PayloadData;
import me.FurH.SkyShield.handler.Shield66PayloadCmd;
import me.FurH.SkyShield.handler.Shield68MessageUser;
import me.FurH.SkyShield.packets.Packet0Handshake;
import me.FurH.SkyShield.packets.Packet1Outdated;
import me.FurH.SkyShield.packets.newscan.Packet60ResultData;
import me.FurH.SkyShield.packets.newscan.Packet61ResultError;
import me.FurH.SkyShield.packets.newscan.Packet62AttachTo;
import me.FurH.SkyShield.packets.newscan.Packet63ResultCode;
import static me.FurH.SkyShield.packets.newscan.Packet63ResultCode.attached;
import me.FurH.SkyShield.packets.newscan.Packet67FileList;
import me.FurH.SkyShield.packets.newscan.Packet69AskCertificate;
import me.FurH.SkyShield.packets.newscan.Packet70Certificate;
import me.FurH.SkyShield.packets.newscan.Packet73ClientPayload;
import me.FurH.SkyShield.packets.newscan.Packet74Postload;
import me.FurH.SkyShield.packets.nicks.Packet77NickOpen;
import me.FurH.SkyShield.packets.nicks.Packet78IdList;
import me.FurH.SkyShield.packets.nicks.Packet79AddNick;
import me.FurH.SkyShield.packets.scan.Packet3ScanUser;
import me.FurH.SkyShield.packets.scan.Packet42PidOff;
import me.FurH.SkyShield.shot.Packet72ScreenData;
import me.FurH.SkyShield.shot.Shield71ScreenShot;
import me.FurH.SkyShield.shot.Shield72ScreenResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class NewClientListener extends ExcludeClientlistener {

    private static final SoftMap<String, Packet70Certificate> certs;
    private static String namekey;
    
    static {
        certs = new SoftMap<>(1000);
    }

    private final HashSet<Integer> knownames;

    private CurrentScan scan;
    private String processlistkey;
    
    public NewClientListener(HandshakeListener base) {
        super(base);
        this.knownames = new HashSet<>();
    }

    public boolean hasUserId(int userId) {
        return knownames.contains(userId);
    }
    
    @Override
    public void idList(Packet78IdList packet) {
        knownames.addAll(Arrays.asList(packet.ids));
    }

    @Override
    public void authenticate(Shield23ScanResult packet) {

        if (scan == null || !packet.randomId.equals(scan.getRandomId())) {
            client.info("No scan to authenticate");
            return;
        }

        registerClient(scan.getRequestUUID());
        saveScan(scan, true);
    }

    private void successScan() {
        scan.setResult(100d);
        scanFinished();
    }

    private void scanFinished() {

        scan.finished = true;

        Shield23ScanResult result = new Shield23ScanResult();

        result.randomId = scan.getRandomId();
        result.uniqueId = scan.getRequestUUID();
        result.result   = scan.getResult();

        scan.handler.getOwner().write(result);
    }

    private void askHwid() {
        
        String key = scan.newKey();
        
        this.writePayload(new CallbackWritter() {
            @Override
            public void write(DataOutputStream dos) throws Throwable {
                dos.writeInt(payload_askid);
                dos.writeUTF(key);
            }
        });
    }
    
    private void hardwareId(byte[] data) {
        
        try {

            String id = Encryption.decrypt(scan.currentKey(), data);
            String[] ids = id.split(":");
            
            // pid:volumeid:productid
            
            SQL.mslow(new SQLTask() {

                @Override
                public void execute(SQLDb sqldb, SQLThread t) throws Throwable {

                    PreparedStatement ps = null;

                    try {

                        ps = t.prepare("@@REMOVED");
                        t.commitNext();

                        ps.setString(1, ids[1]);
                        ps.setString(2, ids[2]);

                        ps.setInt(3, scan.scanid);
                        ps.execute();

                    } finally {

                        Closer.closeQuietly(ps);

                    }
                }
            });
            
        } catch (Exception ex) {

            ex.printStackTrace();
            
        }
        
        askProcessList();
    }
    
    private void askProcessList() {
        
        if (processlistkey != null) {
            System.err.println("process list already requested");
        }

        processlistkey = Encrypto.genRndPass(16);

        this.writePayload(new CallbackWritter() {
            @Override
            public void write(DataOutputStream dos) throws Throwable {
                dos.writeInt(payload_processlist);
                dos.writeUTF(processlistkey);
            }
        });
    }

    private void sendMessage(String message) {

        Shield68MessageUser packet = new Shield68MessageUser();

        packet.uniqueId = scan.getRequestUUID();
        packet.message = message;

        scan.handler.getOwner().write(packet);
    }

    private void scanError(String message, boolean silent, int error_code) {
        
        if (scan == null || scan.finished) {
            return;
        }

        if (!silent) {
            sendMessage(message);
        }

        scan.errorCode(error_code);
        scanFinished();

        Packet61ResultError packet = new Packet61ResultError();
        packet.message = (silent ? "" : message);
        client.write(packet);
    }

    @Override
    public void handshake(Packet0Handshake packet) {
        
        super.handshake(packet);
        
        Resources resources = MainServer.instance.resources;
        resources.sendAgent(packet, client);
        
        try {

            if (!resources.isValidClient(client, packet.fullhash, packet.filename)) {
                client.info("Unkown client " + packet.fullhash);
                client.write(new Packet1Outdated());
            }

        } catch (Throwable ex) {
            
            ex.printStackTrace();
            
        }

        MainServer.instance.resources.updateClient(this, packet);
    }

    @Override
    public void disconnected() {
        
        scanError("Cliente descontado", false, serror_disconnected);
        super.disconnected();
        
        scan = null;
    }
    
    @Override
    public void userQuit() {
        
        super.userQuit();
        
        scanError("Usuario desconectado", false, error_userquitserver);
        scan = null;
    }

    @Override
    public void pidOff(Packet42PidOff packet) {
        
        if (scan == null) {
            return;
        }

        if (packet.pid != scan.pid) {
            return;
        }

        super.invalidate(scan.scanid, scan.userid);
        scanError("Cliente encerrado", false, error_pidoff);
    }

    @Override
    public void scanRequest(Shield22RequestScan request, Packet3ScanUser packet, HandlerListener handler, boolean unique) {

        if (scan != null && !scan.finished) {
            scanError("Pedido reiniciado", false, serror_restarted);            
        }

        scan = new CurrentScan();
        scan.handler = handler;
        scan.unique = unique;

        scan.userid = request.userId;
        scan.setProtocol(request.protocol);
        scan.setRequest(packet);

        scan.scanstage = ServerStage.FETCHCMDLINE;
        client.write(packet.withKey(scan.newKey()));

        super.scanRequest(request, packet, handler, unique);
    }

    private void parseAndAttach() {
       
        if (!scan.namesMatch()) {
            scanError("Seu launcher ou mod aparenta não ser compativel", !scan.unique, serror_namesmismatch);
            return;
        }
        
        int protocol = scan.getProtocol();
        MapEntry mapentry = MapList.byProtocol(protocol);

        if (mapentry == null) {
            scanError("A versão do seu jogo não é suportada! x" + protocol, false, serror_unsuportedprotocol);
            return;
        }

        scan.scanstage = ServerStage.ATTACHREQUEST;

        scan.setMapEntry(mapentry);

        StringBuilder sb = new StringBuilder();
        MapList maplist = mapentry.list;

        for (ProtocolEntry entry : maplist.protocol) {

            if (sb.length() != 0) {
                sb.append("\n");
            }
            
            sb.append(entry.protocol);
        }
        
        addScanning(scan.getRequestUUID());

        Packet62AttachTo packet = new Packet62AttachTo();
        
        packet.pid = scan.pid;
        packet.protocol = sb.toString();
        packet.key = scan.newKey();
        
        client.write(packet);
    }

    @Override
    public void resultData(Packet60ResultData packet) {
       
        String key = null;
        
        try {
            
            if (scan.scanstage == ServerStage.FETCHCMDLINE) {
                
                Stats.scans++;

                key = scan.currentKey();
                String result = Encryption.decrypt(key, packet.data);                

                if (result.equals("NOPID")) {
                    scanError("Não foi possível detectar o jogo em execução!", !scan.unique, serror_ignore);
                    return;
                }

                if (result.equals("MULTIPID")) {
                    scanError("Deixe apenas um jogo aberto!", false, serror_ignore);
                    return;
                }
                
                sendMessage("Detectado com sucesso!");

                CmdLine cmdline = new CmdLine();
                cmdline.parse(result);

                scan.setCmdLine(cmdline);
                parseAndAttach();
            }
            
        } catch (BadPaddingException ex) {
            
            sendMessage("Erro na criptografia!");

        } catch (Exception ex) {
            
            scanError("Não foi possível completar a verificação", false, serror_parsecmdline);
            ex.printStackTrace();

        }
    }

    @Override
    public void resultError(Packet61ResultError packet) {

        scanError(packet.message, false, packet.errorid);

    }
    
    @Override
    public void resultCode(Packet63ResultCode packet) {

        switch (packet.code) {
            case attached: {
                attachResult(packet);
                break;
            }
        }
    }

    private void attachResult(Packet63ResultCode packet) {
        
        if (scan.scanstage != ServerStage.ATTACHREQUEST) {
            scanError("Resultado do anexo em tempo errado!", false, serror_wrongattachtime);
            return;
        }

        scan.scanstage = ServerStage.ATTACHED;

        scan.protocolindex = packet.protocolindex;
        scan.setSessionId(packet.sessionId);

        UUID requestId = scan.getRequestUUID();
        scan.handler.unlockPlayer(requestId);
    }

    @Override
    public void payloadData(Shield65PayloadData packet) {

        if (!scan.getRequestUUID().equals(packet.uniqueId)) {
            scanError("Pacote de dados mal identificado", false, serror_payloadwronguuid);
            return;
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(packet.payload);
        DataInputStream dis = new DataInputStream(bais);

        try {

            int code = dis.readInt();

            int size = dis.readInt();
            byte[] data = new byte[ size ];

            dis.readFully(data);

            switch (code) {
                
                case payload_handshake: {

                    payloadHandshake(data);
                    
                    return;
                }
                
                case payload_fileinfo: {
                    
                    fileHash(data, false);

                    return;
                }
                case payload_ziphash: {
                    
                    fileHash(data, true);
                    
                    return;
                }
                case payload_askid: {
                    
                    hardwareId(data);
                    
                    return;
                }
            }

        } catch (Throwable ex) {

            scanError("Falha na leitura do pacote", false, serror_brokenpayload);
            ex.printStackTrace();
            
        } finally {
            
            Closer.closeQuietly(dis);
            
        }
    }

    public boolean isAgent(String path) {
        return path.contains("@@REMOVED") && path.endsWith("@@REMOVED");
    }

    @Override
    public void fileList(Packet67FileList packet) {

        if (scan.scanstage != ServerStage.REQUESTFILES) {
            scanError("Resultado de arquivos não requisitado", false, serror_filelistwrongtime);
            return;
        }

        packet.files = path(packet.files);

        scan.setJavaVer(packet.javaver);
        scan.setJavaHome(packet.javadir);

        String javahome = scan.getJavaHome();

        CmdLine cmdline = scan.getCmdLine();
        HashSet<String> allpaths = new HashSet<>();

        List<String> filelist = new ArrayList<>(Arrays.asList(packet.files));
        List<String> cmdlist = Arrays.asList(cmdline.classpath);

        allpaths.addAll(filelist);
        allpaths.addAll(cmdlist);

        allpaths.remove("SRC");
        allpaths.remove("URL"); // THIS MUST BE PRESENT

        filelist.removeAll(cmdlist);
        Collections.sort(filelist);

        scan.logFileList(filelist);

        ProtocolList protocols = this.main.newdatabase.byProtocol(scan.getProtocol());

        if (protocols == null) {
            scanError("Versão não suportada", false, serror_unsuportedver);
            return;
        }

        RevisionList revision = protocols.byRevision(scan.protocolindex);
        
        if (revision == null) {
            scanError("Revisão não suportada", false, serror_unsuportedrev);
            return;
        }

        revision.loadAll();

        HashMap<VersionEntry, Integer> checks = new HashMap<>();
        TreeMap<String, FileInfo> infos = new TreeMap<>();

        boolean agentfound = false;
        scan.askcerts = new HashSet<>();

        for (String path : allpaths) {

            FileInfo info = new FileInfo();
            infos.put(path, info);

            String cut = revision.cutPath(path);

            if (cut == null) {

                if (path.startsWith(javahome)) {

                    scan.askcerts.add(path);
                    info.type = FileType.JAVALIB;

                } else {

                    if (!isAgent(path)) {

                        updateChecks(checks);
                        scan.setFileError("unkown", path);
                        scanError("Arquivo desconhecido detectado", false, error_unkownfiledetected);

                        return;

                    } else {

                        if (!agentfound) {

                            info.type = FileType.AGENT;
                            scan.askcerts.add(path);
                            
                            agentfound = true;

                        } else {

                            updateChecks(checks);
                            scan.setFileError("agent", path);
                            scanError("Multiplos agentes detectados", false, error_multipleagents);

                            return;
                        }
                    }
                }

            } else {

                info.type = FileType.GAMEFILE;
                boolean hit = false;
                
                for (VersionEntry entry : revision.entries()) {

                    if (entry.belongs(cut)) {
                        
                        hit = true;

                        Integer cur = checks.get(entry);
                        if (cur == null) {
                            cur = 0;
                        }
                        
                        cur++;
                        checks.put(entry, cur);
                    }
                }
                
                if (!hit) {
                    
                    updateChecks(checks);
                    scan.setFileError("nohit", path);
                    scanError("Versão desconhecida .1", false, serror_invalidfile1);

                    return;
                }
            }
        }

        updateChecks(checks);

        if (!agentfound) {
            scanError("Nenhum agente detectado", false, error_noagentsdetected);
            return;
        }

        scan.fileinfos = infos;

        scan.scanstage = ServerStage.ASKHASH;
        scan.totalchecks = allpaths.size() + scan.askcerts.size();

        scan.revision = revision;
        scan.filelist = allpaths;
        scan.askiterator = allpaths.iterator();

        askNextFile();
    }
    
    private void updateChecks(HashMap<VersionEntry, Integer> checks) {
        checks = sortEntry(checks);
        scan.setPathChecks(checks);
    }
    
    private HashMap<VersionEntry, Integer> sortEntry(HashMap<VersionEntry, Integer> checks) {
        
        HashMap<VersionEntry, Integer> result = new HashMap<>();
        Entry<VersionEntry, Integer> current = null;
        
        for (Entry<VersionEntry, Integer> entry : checks.entrySet()) {
            if (current == null || entry.getValue() > current.getValue()) {
                current = entry;
            }
        }
        
        if (current == null) {
            return result;
        }
        
        result.put(current.getKey(), 0);

        for (Entry<VersionEntry, Integer> entry : checks.entrySet()) {
            if (Objects.equals(entry.getValue(), current.getValue())) {
                result.put(entry.getKey(), 0);
            }
        }

        return result;
    }
    
    private void askNexCertificate() {

        if (!scan.askiterator.hasNext()) {

            scan.scanstage = ServerStage.ASKCERTIFICATES;
            scan.askiterator = scan.filelist.iterator();

            successScan();

            return;
        }

        String path = scan.askiterator.next();
        scan.nexthash = scan.fileinfos.get(path);
        
        Packet70Certificate certcache = certs.get(scan.nexthash.hash);
        
        if (certcache != null) {
            fileCertificate(certcache, path, true);
        } else {
            Packet69AskCertificate packet = new Packet69AskCertificate();
            packet.path = path;
            client.write(packet);
        }
    }

    @Override
    public void fileCertificate(Packet70Certificate packet) {
        fileCertificate(packet, packet.path, false);
    }

    private void fileCertificate(Packet70Certificate packet, String path, boolean cached) {

        if (scan.scanstage != ServerStage.ASKCERTIFICATES) {
            scanError("Certificados recebido no tempo errado!", false, error_certificatewrongtime);
            return;
        }

        FileInfo info = validatePath(path);
        increment();
        
        if (info == null) {
            return;
        }
        
        if (info.sigdata != null) {
            scanError("Arquivo já computado .2", false, error_filecomputedalready);
            return;
        }
        
        if (!cached) {
            certs.put(info.hash, packet);
        }

        scan.nexthash   = null;
        info.signed     = packet.signed;
        info.sigdata    = packet.sigdata;

        switch (info.type) {

            case AGENT:

                if (info.sigdata == null || info.sigdata.equals("unsigned jar")) {

                    scan.setFileError(info.sigdata, path);
                    scanError("Agente não assinado", false, error_wrongagentcertificate);

                    return;
                }

                break;

            case JAVALIB:

                if (!info.signed && (
                        !path.endsWith("/@@REMOVED.jar") &&
                        !path.endsWith("/@@REMOVED.jar") &&
                        !path.endsWith("/@@REMOVED.jar")
                        )) {

                    scan.setFileError(info.sigdata, path);
                    scanError("Biblioteca não assinada", false, error_wrongjavacertificate);

                    return;
                }

                break;

            default:
                break;
        }

        askNexCertificate();
    }
    
    private FileInfo validatePath(String path) {
       
        FileInfo info = scan.fileinfos.get(path);

        if (info == null) {
            scan.setFileError("no info", path);
            scanError("Arquivo não solicitado", false, error_unrequestedfile);
            return null;
        }

        if (info != scan.nexthash) {
            scan.setFileError("wrong info", path);
            scanError("Resultado na sequencia errada", false, error_wrongfilesequence);
            return null;
        }
        
        return info;
    }

    private void askNextFile() {
        
        try {
            
            if (!scan.askiterator.hasNext()) {

                scan.scanstage = ServerStage.ASKCERTIFICATES;
                scan.askiterator = scan.askcerts.iterator();

                askNexCertificate();

                return;
            }

            String path = scan.askiterator.next();
            String key = scan.newKey();

            File file = new File(path);
            boolean ziphash = file.getName().startsWith("@@REMOVED-");

            scan.nexthash = scan.fileinfos.get(path);
            scan.nexthash.ziphash = ziphash;
            
            byte[] encrypted = Encryption.encrypt(key, path);

            if (ziphash) {

                writePayload(new CallbackWritter() {
                    
                    @Override
                    public void write(DataOutputStream dos) throws Throwable {
                        
                        dos.writeInt(payload_askziphash);
                        dos.writeUTF(path);
                        dos.writeUTF(FileEntry.getKey());
                        dos.writeUTF(key);
                        
                        dos.writeInt(encrypted.length);
                        dos.write(encrypted);
                    }
                });

            } else {
                
                writePayload(new CallbackWritter() {

                    @Override
                    public void write(DataOutputStream dos) throws Throwable {

                        dos.writeInt(payload_askhash);
                        dos.writeUTF(key);

                        dos.writeInt(encrypted.length);
                        dos.write(encrypted);
                    }
                });
            }
            
        } catch (Exception ex) {

            this.scanError("Falha ao codificar mensagem", false, serror_fileencodeerr);
            ex.printStackTrace();
            
        }
    }
    
    private void increment() {

        if (scan.checkfiles == 0) {
            this.sendMessage("Verificação iniciada 0%");
        }

        scan.checkfiles++;

        int newpercent = (int) NumberUtils.getWorkDoneDouble(scan.checkfiles, scan.totalchecks);

        if ((scan.checkfiles == scan.totalchecks) || (newpercent - scan.currentporcent) >= 9) {
            scan.currentporcent = newpercent;
            this.sendMessage("Verificação em andamento " + newpercent + "%");
        }
    }
    
    private void fileHash(byte[] data, boolean ziphash) {

        try {
            
            if (scan.scanstage != ServerStage.ASKHASH) {
                scanError("Pacote de dados recebido no tempo errado!", false, serror_filehashwrongtime);
                return;
            }

            String filedata = Encryption.decrypt(scan.currentKey(), data);
            increment();

            int j1 = filedata.indexOf(':');
            int pid = Integer.parseInt(filedata.substring(0, j1));

            if (pid != scan.pid) {
                scanError("Resultado com identificador inválido", false, error_wronghashpid);
                return;
            }

            int j2 = filedata.indexOf(':', j1 + 1);
            
            String hash = filedata.substring(j1 + 1, j2);
            String path = filedata.substring(j2 + 1);

            FileInfo info = validatePath(path);
            
            if (info == null) {
                return;
            }

            if (info.hash != null) {
                scanError("Arquivo já computado", false, error_filecomputedalready);
                return;
            }
            
            if (ziphash != info.ziphash) {
                scanError("Tipo de teste inválido", false, error_filecomputedalready);
                return;
            }

            scan.nexthash = null;
            boolean hit = false;

            switch (info.type) {
                
                case AGENT:

                    if (!client.server.main.resources.isValidAgent(hash)) {
                        scan.setFileError(hash, path);
                        scanError("Agent invalido", false, serror_wrongfilehash);
                        return;
                    }
                    
                    hit = true;
                    break;
                    
                case JAVALIB:
                    
                    hit = true;
                    break;

                default:
                    
                    String cut = scan.revision.cutPath(path);
                    HashMap<VersionEntry, Integer> checks = scan.getCheckList();
                    
                    for (Entry<VersionEntry, Integer> entry : checks.entrySet()) {
                        if (entry.getKey().hasHash(cut, hash, ziphash)) {
                            hit = true;
                            entry.setValue(entry.getValue() + 1);
                        }
                    }

                    break;
            }

            if (hit) {

                info.hash = hash;
                
            } else {

                scan.setFileError(hash, path);
                scanError("Versão desconhecida .2", false, serror_wrongfilehash);
                
                return;
            }
            
            this.askNextFile();

        } catch (Exception ex) {

            scanError("Falha ao decodificar pacote .2", false, serror_errordecodehash);
            ex.printStackTrace();
            
        }
    }

    private void payloadHandshake(byte[] data) {

        try {
            
            if (scan.scanstage != ServerStage.ATTACHED) {
                scanError("Resultado do anexo em tempo errado!", false, serror_attachwrongtime);
                return;
            }

            int decryptedpid = Integer.parseInt(Encryption.decrypt(scan.currentKey(), data));

            if (scan.pid != decryptedpid) {
                scanError("Pacote de dados de fonte invalida", false, serror_invalidcryptpid);
                return;
            }
            
            scan.scanstage = ServerStage.REQUESTFILES;
            
            writePayload(new CallbackWritter() {
                @Override
                public void write(DataOutputStream dos) throws Throwable {
                    dos.writeInt(payload_filelist);
                }
            });
            
        } catch (Exception ex) {

            scanError("Falha ao decodificar pacote", false, serror_faileddecodehandshake);
            ex.printStackTrace();
            
        }
    }
    
    public void writePayload(CallbackWritter callback) {
        
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            
            callback.write(dos);

            Shield66PayloadCmd packet = new Shield66PayloadCmd();

            packet.uniqueId = scan.getRequestUUID();
            packet.payload = baos.toByteArray();

            scan.handler.getOwner().write(packet);

        } catch (Throwable ex) {
            
            this.scanError("Falha ao enviar pacote " + ex.getMessage(), false, serror_failedwritepayload);
            ex.printStackTrace();

        }
    }
    
    @Override
    public void printRequest(Shield71ScreenShot packet, HandlerListener handler) {
        writeShotRequest(packet.requester);
    }

    private void writeShotRequest(UUID requester) {
        writePayload(new CallbackWritter() {
            @Override
            public void write(DataOutputStream dos) throws Throwable {
                dos.writeInt(payload_takeshot);
                if (requester == null) {
                    dos.writeLong(0);
                    dos.writeLong(0);
                } else {
                    dos.writeLong(requester.getMostSignificantBits());
                    dos.writeLong(requester.getLeastSignificantBits());
                }
            }
        });
    }

    @Override
    public void screenData(Packet72ScreenData packet) {

        if (!packet.error && scan != null && scan.scanid > 0) {
            appendShot(scan.scanid, packet.url);
        }

        UUID requester = packet.requester;
        String text;

        if (packet.error) {
            text = packet.message;
        } else {
            text = "Screenshot: " + packet.url;
        }

        writeShotResult(requester, packet.error, text);
    }
    
    private void writeShotResult(UUID requester, boolean error, String text) {

        if (requester.getMostSignificantBits() == 0 && requester.getLeastSignificantBits() == 0) {
            return;
        }
        
        Shield72ScreenResult result = new Shield72ScreenResult();

        result.requester = requester;
        result.error = error;
        result.message = text;

        client.server.writeHandler(result);
    }
    
    private void saveScan(CurrentScan scan, boolean askhid) {

        if (scan.getSessionId() == null) {
            return;
        }

        String json = scan.serialize();

        SQL.mslow(new SQLTask() {

            @Override
            public void execute(SQLDb database, SQLThread t) throws Throwable {

                int id = loadSessionId(scan, t);

                if (id <= 0) {
                    insertScan(scan, json, t);
                } else {
                    scan.scanid = id;
                    updateScan(scan, json, t);
                }

                writeShotRequest(null);
                logScanId(scan.scanid, scan.userid);
                
                if (askhid) {
                    askHwid();
                }
            }
        });
    }
    
    private void updateScan(CurrentScan scan, String json, SQLThread t) throws SQLException {
        
        CmdLine cmdline = scan.getCmdLine();

        PreparedStatement ps = null;

        try {

            ps = t.prepare("@@REMOVED");
            t.commitNext();

            int i = 1;

            ps.setString(i++, cmdline.version);
            ps.setString(i++, scan.checkToString());
            ps.setDouble(i++, scan.getResult());
            ps.setBoolean(i++, true);
            ps.setInt(i++, buildnumber);
            ps.setBytes(i++, Compressor.encode(json));
            ps.setLong(i++, Utils.currentTimeMillis() / 1000l);

            ps.setInt(i++, scan.scanid);
            ps.execute();

        } finally {

            Closer.closeQuietly(ps);

        }
    }
    
    private void insertScan(CurrentScan scan, String json, SQLThread t) throws SQLException {
        
        CmdLine cmdline = scan.getCmdLine();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = t.prepareAutoId("@@REMOVED");
            t.commitNext();

            int i = 1;

            ps.setInt(i++, scan.pid);
            ps.setString(i++, scan.getSessionId().toString());
            ps.setInt(i++, clientid);
            ps.setInt(i++, scan.userid);
            ps.setString(i++, cmdline.version);
            ps.setString(i++, scan.checkToString());
            ps.setDouble(i++, scan.getResult());
            ps.setBoolean(i++, true);
            ps.setInt(i++, buildnumber);
            ps.setBytes(i++, Compressor.encode(json));
            ps.setLong(i++, Utils.currentTimeMillis() / 1000l);

            ps.execute();

            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                System.err.println("Failed to generate scan keys [ 1 ]");
                return;
            }

            scan.scanid = rs.getInt(1);

        } finally {

            Closer.closeQuietly(ps, rs);

        }
    }

    private int loadSessionId(CurrentScan scan, SQLThread t) throws Exception {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = t.prepare("@@REMOVED");

            ps.setInt(1, scan.pid);
            ps.setString(2, scan.getSessionId().toString());

            ps.execute();

            rs = ps.getResultSet();

            if (rs.next()) {

                int id = rs.getInt(1);
                int user = rs.getInt(2);

                if (user != scan.userid) {
                    System.err.println("User mismatch: " + id + " - " + user + " != " + scan.userid);
                    return 0;
                }

                int pid = rs.getInt(3);

                if (pid != scan.pid) {
                    System.err.println("PID mismatch: " + id + " - " + pid + " != " + scan.pid);
                    return 0;
                }
                
                int scanclient = rs.getInt(4);

                if (scanclient != clientid) {
                    System.err.println("Client mismatch: " + id + " - " + scanclient + " != " + clientid);
                    return 0;
                }
                
                byte[] tasklist = rs.getBytes(5);
                
                if (tasklist != null) {

                    String json = Compressor.toString(tasklist);

                    if (scan.tasklist == null) {
                        scan.tasklist = new ProcessList();
                    }

                    scan.tasklist.fromJson(json);
                }
                
                byte[] modules = rs.getBytes(6);

                if (modules != null) {
                    appendModules(Compressor.toString(modules));
                }
                
                byte[] filelist = rs.getBytes(7);

                if (filelist != null) {
                    appendFiles(Compressor.toString(filelist));
                }

                return id;
            }

            return 0;

        } finally {

            Closer.closeQuietly(ps, rs);

        }
    }
    
    @Override
    public void clientPayload(Packet73ClientPayload packet) {

        try {

            switch (packet.action) {
                case payload_processlist: {
                    payloadProcessList(packet.data);
                    break;
                }
                case payload_modulelist: {
                    payloadModuleList(packet.data);
                    break;
                }
                case payload_askfilelist: {
                    payloadFileList(packet.data);
                    break;
                }
            }

        } catch (Throwable ex) {
            
            ex.printStackTrace();
            
        }
    }
    
    private void payloadProcessList(byte[] bytes) throws Exception {
        
        String data = Encryption.decrypt(processlistkey, bytes);
        processlistkey = null;

        int j1 = data.indexOf(':');
        int listpid = Integer.parseInt(data.substring(0, j1));

        if (scan == null || scan.pid != listpid) {
            client.info("process list from wrong pid");
            return;
        }
        
        if (scan.tasklist == null) {
            scan.tasklist = new ProcessList();
        }

        scan.tasklist.parse(data.substring(j1 + 1));
        String serialize = scan.tasklist.serialize();
        
        askModuleList();

        SQL.mslow(new SQLTask() {

            @Override
            public void execute(SQLDb sqldb, SQLThread t) throws Throwable {
                
                PreparedStatement ps = null;

                try {

                    ps = t.prepare("@@REMOVED");
                    t.commitNext();

                    ps.setBytes(1, Compressor.encode(serialize));

                    ps.setInt(2, scan.scanid);
                    ps.execute();

                } finally {

                    Closer.closeQuietly(ps);

                }
            }
        });
    }
    
    private void askModuleList() {
        
        processlistkey = Encrypto.genRndPass(16);

        this.writePayload(new CallbackWritter() {
            @Override
            public void write(DataOutputStream dos) throws Throwable {
                dos.writeInt(payload_modulelist);
                dos.writeUTF(processlistkey);
            }
        });
    }

    private void payloadModuleList(byte[] bytes) throws Exception {

        String data = Encryption.decrypt(processlistkey, bytes);
        processlistkey = null;

        int j1 = data.indexOf(':');
        int listpid = Integer.parseInt(data.substring(0, j1));

        if (scan == null || scan.pid != listpid) {
            client.info("module list from wrong pid");
            return;
        }

        String modules = data.substring(j1 + 1);
        appendModules(modules);
        
        // validate java path
        // validate shield.dll

        StringBuilder sb = new StringBuilder();
        
        for (String module : scan.modules) {
            
            if (sb.length() != 0) {
                sb.append('\n');
            }
            
            sb.append(module);
        }

        startFileList(scan.getCmdLine().gamedir);

        SQL.mslow(new SQLTask() {

            @Override
            public void execute(SQLDb sqldb, SQLThread t) throws Throwable {
                
                PreparedStatement ps = null;

                try {

                    ps = t.prepare("@@REMOVED");
                    t.commitNext();

                    ps.setBytes(1, Compressor.encode(sb.toString()));

                    ps.setInt(2, scan.scanid);
                    ps.execute();

                } finally {

                    Closer.closeQuietly(ps);

                }
            }
        });
    }
    
    private void appendModules(String modlist) {
        
        boolean addmark = true;
        
        if (scan.modules == null) {
            scan.modules = new ArrayList<>();
            addmark = false;
        }
        
        StringTokenizer it = new StringTokenizer(modlist, "\n");

        while (it.hasMoreTokens()) {

            String path = it.nextToken();

            if (scan.modules.contains(path)) {
                continue;
            }

            if (addmark) {
                addmark = false;
                scan.modules.add("---");
            }
            
            scan.modules.add(path);
        }
    }

    private void startFileList(String gamedir) throws Exception {
        FileCrawler crawler = getCrawler(gamedir);
        scan.curcrawler = crawler;
        crawler.start(this);
    }

    private FileCrawler getCrawler(String gamedir) {
       
        if (scan.crawler == null) {
            scan.crawler = new HashMap<>();
        }

        FileCrawler crawler = scan.crawler.get(gamedir);

        if (crawler == null) {
            crawler = new FileCrawler(gamedir);
            scan.crawler.put(gamedir, crawler);
        }
        
        return crawler;
    }
    
    private void payloadFileList(byte[] bytes) throws Exception {

        if (scan == null || scan.curcrawler == null) {
            client.info("crawler or scan is missing");
            return;
        }

        scan.curcrawler.process(scan, bytes);
    }

    private void appendFiles(String json) {
       
        JSONTokener token = new JSONTokener(json);
        JSONObject obj = new JSONObject(token);
        
        for (String dir : obj.keySet()) {

            FileCrawler crawler = getCrawler(dir);
            
            JSONArray arr = obj.getJSONArray(dir);
            crawler.load(arr);
        }
    }

    public void saveFiles() {
       
        JSONObject obj = new JSONObject();

        for (FileCrawler crawler : scan.crawler.values()) {
        
            JSONArray list = new JSONArray();

            for (FileObject file : crawler.files) {
                list.put(file.toJson());
            }

            obj.put(crawler.basepath, list);
        }

        String filejson = obj.toString();
        
        SQL.mslow(new SQLTask() {

            @Override
            public void execute(SQLDb sqldb, SQLThread t) throws Throwable {
                
                PreparedStatement ps = null;

                try {

                    ps = t.prepare("@@REMOVED");
                    t.commitNext();

                    ps.setBytes(1, Compressor.encode(filejson));

                    ps.setInt(2, scan.scanid);
                    ps.execute();

                } finally {

                    Closer.closeQuietly(ps);

                }
            }
        });
    }
    
    @Override
    public void postload(Packet74Postload packet) {
        
        client.info("post loaded: " + packet.postloaded.toString());
        
    }
    
    @Override
    public void nickOpen(Packet77NickOpen packet) {
        
        if (namekey == null) {
            namekey = Encrypto.genRndPass(36);
        }

        packet.key = namekey;
        this.client.write(packet);
    }
    
    @Override
    public void addNick(Packet79AddNick packet) {
        this.main.nicks.addNick(this.client, packet, namekey);
    }
}