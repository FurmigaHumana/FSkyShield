/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.packets;

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
public class Packet0Handshake extends Packet<PacketListener> {

    public boolean handler;    
    public double version;
    public int buildnumber;
    public UUID uniqueId;
    public String clienthash;
    public String agenthash;
    public String fullhash;
    public String filename;

    @Override
    public short getId() {
        return 0;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.handshake(this);
    }
    
    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeBoolean(handler);
        out.writeDouble(version);
        out.writeInt(buildnumber);
        out.writeUUID(uniqueId);
        out.writeUTF(clienthash);
        out.writeUTF(agenthash);
        out.writeUTF(fullhash);
        out.writeUTF(filename);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        handler = in.readBoolean();
        version = in.readDouble();
        buildnumber = in.readInt();
        uniqueId = in.readUUID();
        clienthash = in.readUTF();
        agenthash = in.readUTF();
        fullhash = in.readUTF();
        filename = in.readUTF();
    }
}