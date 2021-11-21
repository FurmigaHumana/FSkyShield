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
public class Shield33RequestResult extends Packet<PacketListener> {

    public int writen;
    public UUID uniqueId;
    public boolean unlock;

    @Override
    public short getId() {
        return 33;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.requestResult(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeInt(writen);
        out.writeUUID(uniqueId);
        out.writeBoolean(unlock);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        writen = in.readInt();
        uniqueId = in.readUUID();
        unlock = in.readBoolean();
    }
    
}
