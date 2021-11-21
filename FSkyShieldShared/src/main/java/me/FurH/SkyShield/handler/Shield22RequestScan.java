/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.handler;

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
public class Shield22RequestScan extends Packet<PacketListener> {

    public int userId;
    public UUID uuid;
    public String username;
    public String address;
    public int remortport;
    public int protocol;

    @Override
    public short getId() {
        return 22;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.scanRequest(this);
    }
    
    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeInt(userId);
        out.writeUUID(uuid);
        out.writeUTF(username);
        out.writeUTF(address);
        out.writeInt(remortport);
        out.writeInt(protocol);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        userId = in.readInt();
        uuid = in.readUUID();
        username = in.readUTF();
        address = in.readUTF();
        remortport = in.readInt();
        protocol = in.readInt();
    }
}