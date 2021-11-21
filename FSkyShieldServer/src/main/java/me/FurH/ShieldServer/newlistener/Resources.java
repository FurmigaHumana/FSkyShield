package me.FurH.ShieldServer.newlistener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.number.NumberUtils;
import me.FurH.ShieldServer.MainServer;
import me.FurH.ShieldServer.server.ShieldClient;
import me.FurH.SkyShield.packets.Packet0Handshake;
import me.FurH.SkyShield.packets.setup.Packet35AgentData;
import me.FurH.SkyShield.packets.update.Packet58ClientUpdate;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Resources {

    private Packet58ClientUpdate clientupdate;
    private long lastclientedit = 0;
    private int localbuild;

    private final HashSet<String>[] hashes = new HashSet[2];

    private Packet35AgentData agentpacket;
    private long agentedit = 0;

    public Resources(MainServer server) {
    }
    
    boolean isValidAgent(String hash) {
        return hashes[0].contains(hash);
    }
    
    boolean isValidClient(ShieldClient sclient, String fullhash, String filename) throws Exception {

        File client = new File("resources", "client.jar");

        if (client.lastModified() != lastclientedit) {

            try {
                registerClient(Encrypto.hash("MD5", client));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            clientupdate = null;
            lastclientedit = client.lastModified();
        }

        StringBuilder filehash = new StringBuilder();
        StringBuilder namehash = new StringBuilder();
        StringBuilder signature = new StringBuilder();
        
        int pos = 0;

        for (int j1 = 0; j1 < 96; j1++) {

            switch (pos) {
                case 0:
                    pos++;
                    filehash.append(fullhash.charAt(j1));
                    break;
                case 1:
                    pos++;
                    namehash.append(fullhash.charAt(j1));
                    break;
                case 2:
                    pos = 0;
                    signature.append(fullhash.charAt(j1));
                    break;
                default:
                    break;
            }
        }
        
        if (!hashes[1].contains(filehash.toString())) {
            sclient.error("invalid file hash " + filehash.toString() + " - " + fullhash + " - " + filename);
            return false;
        }
        
        String nametest = Encrypto.hash("MD5", filename);

        if (!nametest.equals(namehash.toString())) {
            sclient.info("invalid name hash: " + nametest + " != " + namehash.toString() + " - " + fullhash + " - " + filename);
            return false;
        }
        
        String sigtest = Encrypto.hash("MD5", filename + filehash.toString() + "me/FurH/JavaPacker/loader/AClassLoader");
        
        if (!sigtest.equals(signature.toString())) {
            sclient.info("invalid signature: " + sigtest + " != " + signature.toString() + " - " + fullhash + " - " + filename);
            return false;
        }

        return true;
    }
    
    void sendAgent(Packet0Handshake packet, ShieldClient client) {

        File agent = new File("resources", "FSkyShieldAgent-1.0-SNAPSHOT-signed.jar");

        if (agentpacket == null || agent.lastModified() != agentedit) {

            try {
                                
                agentpacket         = new Packet35AgentData();
                
                agentpacket.data    = FileUtils.getBytesFromFile(agent);
                agentpacket.md5     = Encrypto.hash("MD5", agent);
                
                agentedit           = agent.lastModified();
                
                registerAgent(agentpacket.md5);
                
            } catch (Exception ex) {
                
                ex.printStackTrace();

            }
        }

        if (!packet.agenthash.equals(agentpacket.md5)) {

            client.write(agentpacket);
            
        } else {

            Packet35AgentData result = new Packet35AgentData();
            
            result.data = new byte[ 0 ];
            result.md5 = agentpacket.md5;
            
            client.write(result);
        }
    }

    private void registerAgent(String hash) {
        registerHash(0, hash);
    }
    
    private void registerClient(String hash) {
        registerHash(1, hash);
    }
    
    private void registerHash(int type, String hash) {

        if (!hashes[type].add(hash)) {
            return;
        }
        
        SQL.mslow(new SQLTask() {
            
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {

                PreparedStatement ps = null;
                
                try {
                    
                    long date = System.currentTimeMillis() / 1000l;
                    
                    ps = t.prepare("INSERT INTO `skyshield_v2`.`shield_resources` (type, hash, date) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `date` = ?;");
                    t.commitNext();
                    
                    ps.setInt(1, type);
                    ps.setString(2, hash);
                    ps.setLong(3, date);
                    ps.setLong(4, date);

                    ps.execute();
                    
                } finally {

                    Closer.closeQuietly(ps);

                }
            }
        });
    }

    public void loadAll(SQLThread t) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {

            ps = t.prepare("SELECT type, hash FROM `skyshield_v2`.`shield_resources`;");
            ps.execute();

            rs = ps.getResultSet();

            hashes[ 0 ] = new HashSet<>();
            hashes[ 1 ] = new HashSet<>();

            while (rs.next()) {

                int type    = rs.getInt(1);
                String md5  = rs.getString(2);

                hashes[ type ].add(md5);
            }

        } finally {
            
            Closer.closeQuietly(ps, rs);
            
        }
    }
    
    private Packet58ClientUpdate getClientUpdate() {
        
        if (clientupdate == null) {
            
            Packet58ClientUpdate packet = new Packet58ClientUpdate();
            
            try {
                
                File file = new File("resources", "client.jar");
                localbuild = getBuildVersion(file);
                                
                packet.data = FileUtils.getBytesFromFile(file);
                packet.md5 = Encrypto.hash("MD5", packet.data);

                clientupdate = packet;
                
            } catch (Exception ex) {
                
                ex.printStackTrace();
                
            }
        }
        
        return clientupdate;
    }
    
    public static int getBuildVersion(File file) throws IOException {
        
        FileInputStream fis = null;
        ZipInputStream zis = null;

        try {
            
            fis = new FileInputStream(file);
            zis = new ZipInputStream(fis);
            
            ZipEntry entry = zis.getNextEntry();
            byte[] buffer = new byte[ 8192 ];
            
            while (entry != null) {
                
                if (entry.getName().contains("version.properties")) {
                
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    int read;
                    while ((read = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, read);
                    }

                    Properties props = new Properties();

                    props.load(new ByteArrayInputStream(baos.toByteArray()));

                    return NumberUtils.toInteger(props.getProperty("buildnumber"));
                }
                
                entry = zis.getNextEntry();
            }
            
            return 0;

        } finally {
            
            Closer.closeQuietly(fis);
            Closer.closeQuietly(zis);
            
        }
    }

    void updateClient(NewClientListener listener, Packet0Handshake packet) {
        if (packet.clienthash != null && !packet.clienthash.isEmpty()) {
            Packet58ClientUpdate update = getClientUpdate();
            if (localbuild >= listener.buildnumber && !update.md5.equals(packet.clienthash)) {
                listener.getOwner().write(update);
            }
        }
    }
}