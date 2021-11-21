/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.listener;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.util.Callback;
import me.FurH.SkyShield.api.API59NamesDown;
import me.FurH.SkyShield.handler.Shield22RequestScan;
import me.FurH.SkyShield.handler.Shield23ScanResult;
import me.FurH.SkyShield.packets.Packet0Handshake;
import me.FurH.SkyShield.packets.Packet1Outdated;
import me.FurH.SkyShield.packets.Packet2ClientReady;
import me.FurH.SkyShield.packets.scan.Packet3ScanUser;
import me.FurH.SkyShield.packets.scan.Packet42PidOff;
import me.FurH.SkyShield.packets.setup.Packet34NameList;
import me.FurH.SkyShield.shot.Shield71ScreenShot;

/**
 *
 * @author lgpse
 */
public class AbstractClientListener extends HandshakeListener {

    private static final HashMap<UUID, AbstractClientListener> clientindex = new HashMap<>();
    private static final HashMap<UUID, AbstractClientListener> scanning = new HashMap<>();

    private static final int min_build = 899;
    public Callback<Shield23ScanResult> callback;

    private final HashSet<Integer> scanids;
    private final HashSet<Integer> userids;

    public AbstractClientListener(HandshakeListener base) {
        super(base);
        this.userids = new HashSet<>();
        this.scanids = new HashSet<>();
    }
    
    public static <A extends AbstractClientListener> A getClientFor(UUID uniqueId) {
        return (A) clientindex.get(uniqueId);
    }
    
    public static <A extends AbstractClientListener> A getScanning(UUID uniqueId) {
        return (A) scanning.get(uniqueId);
    }

    public void addScanning(UUID uniqueId) {
        scanning.put(uniqueId, this);
    }

    public void registerClient(UUID uniqueId) {
        clientindex.put(uniqueId, this);
        scanning.remove(uniqueId);
    }

    protected void logScanId(int scanid, int userid) {
        scanids.add(scanid);
        userids.add(userid);
    }

    @Override
    public void nameList(Packet34NameList packet) {
    }

    public void scanRequest(Shield22RequestScan request, Packet3ScanUser scan, HandlerListener handler, boolean unique) {
    }
    
    public void authenticate(Shield23ScanResult result) {
    }
    
    @Override
    public void disconnected() {
        super.disconnected();
        fullCleanup();
    }
    
    private void fullCleanup() {
        
        disconnectAll();
        invalidateAll();

        cleanup(scanning);
        cleanup(clientindex);
    }
    
    private void disconnectAll() {
        
        if (!userids.isEmpty()) {

            int[] arr = new int[ userids.size() ];
            Iterator<Integer> it = userids.iterator();

            for (int j1 = 0; j1 < arr.length; j1++) {
                arr[ j1 ] = it.next();
            }

            userids.clear();

            API59NamesDown packet = new API59NamesDown();
            packet.users = arr;
            server.writeHandler(packet);
        }
    }

    private void cleanup(HashMap clients) {
        Iterator it = clients.values().iterator();
        while (it.hasNext()) {
            if (it.next() == this) {
                it.remove();
            }
        }
    }

    private void invalidateAll() {

        if (scanids.isEmpty()) {
            return;
        }

        SQL.mslow(new SQLTask() {

            @Override
            public void execute(SQLDb database, SQLThread t) throws Throwable {

                PreparedStatement ps = null;

                try {

                    ps = t.prepare("@@REMOVED");
                    t.commitNext();
                    
                    for (int id : scanids) {
                        ps.setInt(1, id);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                    scanids.clear();

                } finally {

                    Closer.closeQuietly(ps);

                }
            }
        });
    }
    
    @Override
    public void handshake(Packet0Handshake packet) {

        if (buildnumber < min_build) {

            try {
                client.write(new Packet1Outdated());
            } catch (Throwable ex) {
                ex.printStackTrace();
            }

            return;
        }

        try {
            client.write(new Packet2ClientReady());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    protected void appendShot(int scanid, String message) {
        
        SQL.mslow(new SQLTask() {

            @Override
            public void execute(SQLDb sqldb, SQLThread t) throws Throwable {

                PreparedStatement ps = null;

                try {

                    String url = message.substring(message.lastIndexOf('/') + 1);
                    
                    ps = t.prepare("@@REMOVED");
                    t.commitNext();
                    
                    ps.setInt(1, scanid);

                    ps.execute();

                } finally {

                    Closer.closeQuietly(ps);

                }
            }
        });
    }
    
    @Override
    public void pidOff(Packet42PidOff packet) {
    }
    
    protected void invalidate(int scanid, int userid) {

        API59NamesDown downpkt = new API59NamesDown();
        downpkt.users = new int[] { userid };
        server.writeHandler(downpkt);
        
        if (scanid <= 0) {
            return;
        }

        SQL.mslow(new SQLTask() {

            @Override
            public void execute(SQLDb database, SQLThread t) throws Throwable {

                PreparedStatement ps = null;

                try {

                    ps = t.prepare("@@REMOVED");
                    t.commitNext();

                    ps.setInt(1, scanid);
                    ps.execute();

                } finally {

                    Closer.closeQuietly(ps);

                }
            }
        });
    }

    public void printRequest(Shield71ScreenShot packet, HandlerListener handler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void userQuit() {
        fullCleanup();
    }
}
