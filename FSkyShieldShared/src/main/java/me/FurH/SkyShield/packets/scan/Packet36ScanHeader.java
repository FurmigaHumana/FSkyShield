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
public class Packet36ScanHeader extends Packet<PacketListener> {

    public int pid;
   
    public String username;
    public UUID uuid;
    
    public String version = "#ver";
    public String cmdname = "#usr";
    public String commandline = "#cmdline";
    
    public int loadcount;

    @Override
    public short getId() {
        return 36;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.scanHeader(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {

        out.writeInt(pid);
        
        out.writeUTF(username);
        out.writeUUID(uuid);

        out.writeUTF(version);
        out.writeUTF(cmdname);
        out.writeUTF("");
        out.writeUTF(commandline);

        out.writeInt(loadcount);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        
        pid         = in.readInt();
        
        username    = in.readUTF();
        uuid        = in.readUUID();

        version     = in.readUTF();
        cmdname     = in.readUTF();
        in.readUTF();
        commandline = in.readUTF();

        loadcount   = in.readInt();
    }
}