/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.packets.scan;

import java.io.IOException;
import java.util.UUID;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/**
 *
 * @author lgpse
 */
public class Packet3ScanUser extends Packet<PacketListener> {
    
    public UUID uniqueId;
    public String username;
    public String[] database;
    public Integer[] remortport;
    public String key;
    
    public Packet3ScanUser withKey(String newkey) {

        Packet3ScanUser ret = new Packet3ScanUser();
        
        ret.uniqueId = uniqueId;
        ret.username = username;
        ret.database = database;
        ret.remortport = remortport;
        ret.key = newkey;

        return ret;
    }
    
    @Override
    public short getId() {
        return 3;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.scan(this);
    }
    
    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeUUID(uniqueId);
        out.writeUTF(username);
        out.writeUTFArr(database);
        out.writeUTFArr(new String[ 0 ]);
        out.writeIntArr(remortport);
        out.writeUTF(key);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        uniqueId = in.readUUID();
        username = in.readUTF();
        database = in.readUTFArr();
        in.readUTFArr();
        remortport = in.readIntArr();
        key = in.readUTF();
    }
}