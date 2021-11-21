/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.bigdata;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import me.FurH.Core.cache.soft.SoftMap;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.executors.TimerExecutor;
import me.FurH.Core.util.Callback;
import me.FurH.Core.util.Utils;
import me.FurH.ShieldServer.server.ShieldClient;
import me.FurH.SkyShield.encoder.Compressor;
import me.FurH.SkyShield.packets.big.Packet47OfferBigData;
import me.FurH.SkyShield.packets.big.Packet48BigResult;
import me.FurH.SkyShield.packets.big.Packet49BigStore;

/**
 *
 * @author lgpse
 */
public class BigData {
    
    private static final SoftMap<String, Integer> cache;
    
    static {
        
        cache = new SoftMap<>();
        
        TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                cleanup();
            }
        }, 1, TimeUnit.HOURS);
    }
    
    private static void cleanup() {
        SQL.mslow(new SQLTask() {
            @Override
            public void execute(SQLDb sqldb, SQLThread t) throws Throwable {
                cleanup("shield_bigdata", 30, t);
                cleanup("shield_pload", 30, t);
                cleanup("shield_scans", 30*6, t);
            }
        });
    }

    private static void cleanup(String table, int days, SQLThread t) throws SQLException {

        long time = (Utils.currentTimeMillis() / 1000l) - (86400 * days);

        PreparedStatement ps = null;
        
        try {
            
            ps = t.prepare("@@REMOVED");
            t.commitNext();

            ps.setLong(1, time);
            ps.execute();
            
            cache.clear();

        } finally {
            
            Closer.closeQuietly(ps);
            
        }
    }

    public static int store(String data, int owner, SQLThread t) throws Exception {
        String hash = Encrypto.hash("MD5", data);
        
        return store(hash, data, null, owner, t);
    }
    
    public static int store(String hash, String data, byte[] bytes, int owner, SQLThread t) throws Exception {

        Integer id = cache.get(hash);

        if (id != null) {
            return id;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            
            ps = t.prepareAutoId("@@REMOVED");
            t.commitNext();
            
            long now = Utils.currentTimeMillis() / 1000l;

            ps.setString(1, hash);
            
            if (bytes != null) {
                ps.setBytes(2, bytes);
            } else {
                ps.setBytes(2, Compressor.encode(data));
            }

            ps.setInt(3, owner);
            ps.setLong(4, now);
            ps.setLong(5, now);

            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            
            if (id != null) {
                
                cache.put(hash, id);
                
                return id;
            }
            
            System.err.println("Failed to generate bigdata id");
            return 0;
            
        } finally {
            
            Closer.closeQuietly(ps, rs);
            
        }
    }
    
    public static void offerBigData(ShieldClient client, Packet47OfferBigData packet) {
        offerBigData(packet.hash, new Callback<Integer>() {
            @Override
            public void invoke(Integer id) {
                replyOffer(id, client, packet.hash);
            }
        });
    }
    
    public static void offerBigData(String hash, Callback<Integer> callback) {
        
        Integer id = cache.get(hash);
        
        if (id != null) {
            callback.invoke(id);
            return;
        }
        
        SQL.mslow(new SQLTask() {
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                callback.invoke(fetchIdByHash(hash, t));
            }
        });
    }
    
    private static void replyOffer(int id, ShieldClient client, String hash) {
        
        Packet48BigResult result = new Packet48BigResult();
        
        result.hash = hash;
        result.id = id;

        client.write(result);
    }
    
    private static void touchBigId(int id, SQLThread t) throws SQLException {
        
        PreparedStatement ps = null;
        
        try {
            
            ps = t.prepare("@@REMOVED");
            t.commitNext();

            long now = Utils.currentTimeMillis() / 1000l;

            ps.setLong(1, now);
            ps.setInt(2, id);

            ps.execute();

        } finally {
            
            Closer.closeQuietly(ps);
            
        }
    }

    public static void bigStore(ShieldClient client, Packet49BigStore packet, int playid, SQLThread t) throws Exception {
        int id = store(packet.hash, null, packet.data, playid, t);
        replyOffer(id, client, packet.hash);
    }

    public static byte[] fetch(int bigid, SQLThread t) throws SQLException {
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            
            ps = t.prepare("@@REMOVED");

            ps.setInt(1, bigid);

            ps.execute();
            
            rs = ps.getResultSet();
            
            if (rs.next()) {
                return rs.getBytes(1);
            }

            return null;

        } finally {
            
            Closer.closeQuietly(ps, rs);
            
        }
    }
    
    public static int fetchIdByHash(String hash, SQLThread t) throws SQLException {
        
        Integer id = cache.get(hash);

        if (id != null) {
            return id;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            
            ps = t.prepare("@@REMOVED");

            ps.setString(1, hash);

            ps.execute();
            
            rs = ps.getResultSet();
            
            if (rs.next()) {
                
                int newid = rs.getInt(1);
                
                SQL.mslow(new SQLTask() {
                    @Override
                    public void execute(SQLDb db, SQLThread t) throws Throwable {
                        touchBigId(newid, t);
                    }
                });
                
                cache.put(hash, newid);
                
                return newid;
            }

            return 0;

        } finally {
            
            Closer.closeQuietly(ps, rs);
            
        }
    }
}
