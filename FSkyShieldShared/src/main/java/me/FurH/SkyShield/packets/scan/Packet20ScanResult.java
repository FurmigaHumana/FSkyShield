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
public class Packet20ScanResult extends Packet<PacketListener> {

    public int pid;
    public UUID scanid;
   
    public String username;
    public UUID uuid;
    
    public String mainclass;
    public String gameversion;
    public String baseversion;
    public String[] extraversion;
    
    public String[] macs;
    
    public int totalfiles;
    public int totaltests;
    public int totalerrors;
    
    public double result;
    public String[] blacklists;

    public String[] errorfiles;

    @Override
    public short getId() {
        return 20;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.scanResult(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeUUID(scanid);
   
        out.writeUTF(username);
        out.writeUUID(uuid);
    
        out.writeUTF(mainclass);
        out.writeUTF(gameversion);
        out.writeUTF(baseversion);
        out.writeUTFArr(extraversion);
    
        out.writeUTFArr(macs);
    
        out.writeInt(totalfiles);
        out.writeInt(totaltests);
        out.writeInt(totalerrors);
    
        out.writeDouble(result);
        out.writeUTFArr(blacklists);

        out.writeUTFArr(errorfiles);
        
        out.writeInt(pid);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        scanid = in.readUUID();
   
        username = in.readUTF();
        uuid = in.readUUID();
    
        mainclass = in.readUTF();
        gameversion = in.readUTF();
        baseversion = in.readUTF();
        extraversion = in.readUTFArr();
    
        macs = in.readUTFArr();
    
        totalfiles = in.readInt();
        totaltests = in.readInt();
        totalerrors = in.readInt();
    
        result = in.readDouble();
        blacklists = in.readUTFArr();

        errorfiles = in.readUTFArr();
        
        try {
            pid = in.readInt();
        } catch (Throwable ex) { }
    }
}