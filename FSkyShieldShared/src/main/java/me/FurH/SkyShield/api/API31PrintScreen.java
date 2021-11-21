/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.api;

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
public class API31PrintScreen extends Packet<PacketListener> {
    
    public String handler = "";
    public UUID requester;
    public UUID uniqueId;
    public String username;
    public int pid;
    public String address;
    public int scan;

    @Override
    public short getId() {
        return 31;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.printRequest(this);
    }
    
    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeUTF(handler);
        out.writeUUID(requester);
        out.writeUUID(uniqueId);
        out.writeInt(pid);
        out.writeUTF(address);
        out.writeInt(scan);
        out.writeUTF(username);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        handler = in.readUTF();
        requester = in.readUUID();
        uniqueId = in.readUUID();
        pid = in.readInt();
        address = in.readUTF();
        scan = in.readInt();
        username = in.readUTF();
    }
}