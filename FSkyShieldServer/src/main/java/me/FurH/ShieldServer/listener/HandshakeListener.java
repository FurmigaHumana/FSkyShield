/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.listener;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.NIO.packet.Packet;
import me.FurH.ShieldServer.MainServer;
import me.FurH.ShieldServer.newlistener.NewClientListener;
import me.FurH.ShieldServer.server.ShieldClient;
import me.FurH.ShieldServer.server.ShieldServer;
import me.FurH.ShieldServer.users.ClientIds;
import me.FurH.SkyShield.PacketListener;
import me.FurH.SkyShield.api.API31PrintScreen;
import me.FurH.SkyShield.api.API32PrintResult;
import me.FurH.SkyShield.api.API59NamesDown;
import me.FurH.SkyShield.handler.Shield22RequestScan;
import me.FurH.SkyShield.handler.Shield23ScanResult;
import me.FurH.SkyShield.handler.Shield24Invalidate;
import me.FurH.SkyShield.handler.Shield33RequestResult;
import me.FurH.SkyShield.handler.Shield64UnlockPlayer;
import me.FurH.SkyShield.handler.Shield65PayloadData;
import me.FurH.SkyShield.handler.Shield66PayloadCmd;
import me.FurH.SkyShield.handler.Shield68MessageUser;
import me.FurH.SkyShield.packets.Packet0Handshake;
import me.FurH.SkyShield.packets.Packet18CheckSession;
import me.FurH.SkyShield.packets.Packet1Outdated;
import me.FurH.SkyShield.packets.Packet2ClientReady;
import me.FurH.SkyShield.packets.Packet75Sleep;
import me.FurH.SkyShield.packets.big.Packet47OfferBigData;
import me.FurH.SkyShield.packets.big.Packet48BigResult;
import me.FurH.SkyShield.packets.big.Packet49BigStore;
import me.FurH.SkyShield.packets.cls.Packet56RequestSources;
import me.FurH.SkyShield.packets.cls.Packet57SourceResult;
import me.FurH.SkyShield.packets.newscan.Packet60ResultData;
import me.FurH.SkyShield.packets.newscan.Packet61ResultError;
import me.FurH.SkyShield.packets.newscan.Packet62AttachTo;
import me.FurH.SkyShield.packets.newscan.Packet63ResultCode;
import me.FurH.SkyShield.packets.newscan.Packet67FileList;
import me.FurH.SkyShield.packets.newscan.Packet69AskCertificate;
import me.FurH.SkyShield.packets.newscan.Packet70Certificate;
import me.FurH.SkyShield.packets.newscan.Packet73ClientPayload;
import me.FurH.SkyShield.packets.newscan.Packet74Postload;
import me.FurH.SkyShield.packets.nicks.Packet77NickOpen;
import me.FurH.SkyShield.packets.nicks.Packet78IdList;
import me.FurH.SkyShield.packets.nicks.Packet79AddNick;
import me.FurH.SkyShield.packets.ping.Packet16Ping;
import me.FurH.SkyShield.packets.ping.Packet17Pong;
import me.FurH.SkyShield.packets.ping.Packet30ClientAddress;
import me.FurH.SkyShield.packets.scan.Packet20ScanResult;
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
import me.FurH.SkyShield.packets.scan2.Packet46ScanHeader2;
import me.FurH.SkyShield.packets.scan2.Packet50ScanCmdLine;
import me.FurH.SkyShield.packets.scan2.Packet51ScanComplete2;
import me.FurH.SkyShield.packets.scan2.Packet52ClassList;
import me.FurH.SkyShield.packets.scan2.Packet53OfferFile;
import me.FurH.SkyShield.packets.scan2.Packet54FileResult;
import me.FurH.SkyShield.packets.scan2.Packet55StoreFile;
import me.FurH.SkyShield.packets.setup.Packet34NameList;
import me.FurH.SkyShield.packets.setup.Packet35AgentData;
import me.FurH.SkyShield.packets.update.Packet58ClientUpdate;
import me.FurH.SkyShield.shot.Packet72ScreenData;
import me.FurH.SkyShield.shot.Shield71ScreenShot;
import me.FurH.SkyShield.shot.Shield72ScreenResult;

/**
 *
 * @author lgpse
 */
public class HandshakeListener implements PacketListener {
    
    public static int presp = 0;
    public static int psents = 0;
    
    private final ConcurrentLinkedQueue<Packet> queue;

    public int clientid;
    public int buildnumber;
    public double version;

    protected final ShieldServer server;
    protected final MainServer main;
    public    final ShieldClient client;

    public HandshakeListener(MainServer main, ShieldServer server, ShieldClient client) {
        this.queue  = new ConcurrentLinkedQueue<>();
        this.server = server;
        this.client = client;
        this.main   = main;
    }
    
    public HandshakeListener(HandshakeListener copy) {
        this.queue          = copy.queue;
        this.clientid       = copy.clientid;
        this.buildnumber    = copy.buildnumber;
        this.version        = copy.version;
        this.server         = copy.server;
        this.client         = copy.client;
        this.main           = copy.main;
    }

    @Override
    public ShieldClient getOwner() {
        return client;
    }

    @Override
    public void disconnected() {
    }
    
    @Override
    public void handshake(Packet0Handshake packet) {
        SQL.mslow(new SQLTask() {
            @Override
            public void execute(SQLDb sqldb, SQLThread t) throws Throwable {
                handshake(packet, t);
            }
        });
    }

    private void handshake(Packet0Handshake packet, SQLThread t) throws SQLException {

        clientid    = ClientIds.getUniqueID(packet.uniqueId, true, t);
        buildnumber = packet.buildnumber;
        version     = packet.version;
        
        client.setClientType(packet.handler);

        if (client.isHandler()) {

            HandlerListener listener = new HandlerListener(this);
            client.setPacketListener(listener);

            listener.handshake(packet);
            
        } else {
            
            HandshakeListener listener;
            
            if (buildnumber > 1020) {
                listener = new NewClientListener(this);
            } else {
                listener = new ClientListener(this);
            }

            client.setPacketListener(listener);
            listener.handshake(packet);

            if (buildnumber > 570 && buildnumber < 1020) {
                Packet30ClientAddress addr = new Packet30ClientAddress();
                addr.ip = client.host;
                client.write(addr);
            }
        }
        
        while (!queue.isEmpty()) {
            queue.poll().handle(client.getPacketListener());
        }
    }
    
    @Override
    public void outdated(Packet1Outdated packet) {
        enqueue(packet);
    }

    @Override
    public void ready(Packet2ClientReady packet) {
        enqueue(packet);
    }

    @Override
    public void scan(Packet3ScanUser packet) {
        enqueue(packet);
    }

    @Override
    public void ping(Packet16Ping packet) {
        Packet17Pong pong = new Packet17Pong();
        pong.created = packet.created;
        try {
            client.write(pong);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void pong(Packet17Pong packet) {
        client.info("[ " + client.key() + "/" + buildnumber + "] pong is " + (System.currentTimeMillis() - packet.created) + " ms " + (++presp) + "/" + psents);
    }

    @Override
    public void checkSession(Packet18CheckSession packet) {
        enqueue(packet);
    }

    @Override
    public void scanResult(Packet20ScanResult packet) {
        enqueue(packet);
    }

    @Override
    public void scanRequest(Shield22RequestScan packet) {
        enqueue(packet);
    }

    @Override
    public void scanData(Shield23ScanResult packet) {
        enqueue(packet);
    }

    @Override
    public void invalidate(Shield24Invalidate packet) {
        enqueue(packet);
    }

    @Override
    public void clientip(Packet30ClientAddress packet) {
        enqueue(packet);
    }
    
    @Override
    public void printRequest(API31PrintScreen packet) {
        enqueue(packet);
    }
    
    private void enqueue(Packet packet) {
        queue.add(packet);
        if (queue.size() > 10) {
            client.info("Queue for " + client.host + " " + queue.size() + " packet: " + packet.getClass().getName());
            client.close();
        }
    }

    @Override
    public void printResult(API32PrintResult packet) {
        enqueue(packet);
    }

    @Override
    public void requestResult(Shield33RequestResult packet) {
        enqueue(packet);
    }

    @Override
    public void nameList(Packet34NameList packet) {
        enqueue(packet);
    }

    @Override
    public void agentData(Packet35AgentData packet) {
        enqueue(packet);
    }

    @Override
    public void scanHeader(Packet36ScanHeader packet) {
        enqueue(packet);
    }

    @Override
    public void scanHashes(Packet37JarHash packet) {
        enqueue(packet);
    }

    @Override
    public void scanCompleted(Packet38ScanComplete packet) {
        enqueue(packet);
    }

    @Override
    public void agentHash(Packet39AgentHash packet) {
        enqueue(packet);
    }

    @Override
    public void accessDenied(Packet40AccessDenied packet) {
        enqueue(packet);
    }

    @Override
    public void classLoad(Packet41ClassLoad packet) {
        enqueue(packet);
    }

    @Override
    public void pidOff(Packet42PidOff packet) {
        enqueue(packet);
    }

    @Override
    public void bigComplete(Packet43BigScanComplete packet) {
        enqueue(packet);
    }

    @Override
    public void namesDown(API59NamesDown packet) {
        enqueue(packet);
    }

    @Override
    public void scanHashes2(Packet45JarHash2 packet) {
        enqueue(packet);
    }

    @Override
    public void scanHeader2(Packet46ScanHeader2 packet) {
        enqueue(packet);
    }

    @Override
    public void offerBigData(Packet47OfferBigData packet) {
        enqueue(packet);
    }

    @Override
    public void bigResult(Packet48BigResult packet) {
        enqueue(packet);
    }

    @Override
    public void bigStore(Packet49BigStore packet) {
        enqueue(packet);
    }

    @Override
    public void setCmdLine(Packet50ScanCmdLine packet) {
        enqueue(packet);
    }

    @Override
    public void scanCompleted2(Packet51ScanComplete2 packet) {
        enqueue(packet);
    }

    @Override
    public void classList(Packet52ClassList packet) {
        enqueue(packet);
    }

    @Override
    public void offerFile(Packet53OfferFile packet) {
        enqueue(packet);
    }

    @Override
    public void fileResult(Packet54FileResult packet) {
        enqueue(packet);
    }

    @Override
    public void storeFile(Packet55StoreFile packet) {
        enqueue(packet);
    }

    @Override
    public void requestSources(Packet56RequestSources packet) {
        enqueue(packet);
    }

    @Override
    public void sourceResult(Packet57SourceResult packet) {
        enqueue(packet);
    }

    @Override
    public void clientUpdate(Packet58ClientUpdate packet) {
        enqueue(packet);
    }

    @Override
    public void resultData(Packet60ResultData packet) {
        enqueue(packet);
    }

    @Override
    public void resultError(Packet61ResultError packet) {
        enqueue(packet);
    }

    @Override
    public void attachTo(Packet62AttachTo packet) {
        enqueue(packet);
    }

    @Override
    public void resultCode(Packet63ResultCode packet) {
        enqueue(packet);
    }

    @Override
    public void unlockPlayer(Shield64UnlockPlayer packet) {
        enqueue(packet);
    }

    @Override
    public void payloadData(Shield65PayloadData packet) {
        enqueue(packet);
    }

    @Override
    public void payloadCmd(Shield66PayloadCmd packet) {
        enqueue(packet);
    }

    @Override
    public void fileList(Packet67FileList packet) {
        enqueue(packet);
    }

    @Override
    public void messageUser(Shield68MessageUser packet) {
        enqueue(packet);
    }

    @Override
    public void askCertificate(Packet69AskCertificate packet) {
        enqueue(packet);
    }

    @Override
    public void fileCertificate(Packet70Certificate packet) {
        enqueue(packet);
    }

    @Override
    public void reqScreenShot(Shield71ScreenShot packet) {
        enqueue(packet);
    }

    @Override
    public void screenResult(Shield72ScreenResult packet) {
        enqueue(packet);
    }

    @Override
    public void screenData(Packet72ScreenData packet) {
        enqueue(packet);
    }

    @Override
    public void clientPayload(Packet73ClientPayload packet) {
        enqueue(packet);
    }

    @Override
    public void postload(Packet74Postload packet) {
        enqueue(packet);
    }

    @Override
    public void sleep(Packet75Sleep packet) {
        client.disconnect();
    }

    @Override
    public void nickOpen(Packet77NickOpen packet) {
        enqueue(packet);
    }

    @Override
    public void addNick(Packet79AddNick packet) {
        enqueue(packet);
    }

    @Override
    public void idList(Packet78IdList packet) {
        enqueue(packet);
    }
}