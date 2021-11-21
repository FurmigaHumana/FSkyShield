package me.FurH.SkyShield.handler;

import java.io.IOException;
import java.util.UUID;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Shield64UnlockPlayer extends Packet<PacketListener> {

    public UUID uniqueId;
    
    @Override
    public short getId() {
        return 64;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.unlockPlayer(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        dos.writeUUID(uniqueId);
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        uniqueId = dis.readUUID();
    }
}