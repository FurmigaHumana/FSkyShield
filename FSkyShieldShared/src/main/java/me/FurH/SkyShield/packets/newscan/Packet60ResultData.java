package me.FurH.SkyShield.packets.newscan;

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
public class Packet60ResultData extends Packet<PacketListener> {

    public UUID uniqueId;
    public byte[] data;
    
    @Override
    public short getId() {
        return 60;
    }

    @Override
    public boolean bigBuffer() {
        return true;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.resultData(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        dos.writeUUID(uniqueId);
        dos.writeNByteArr(data);
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        uniqueId = dis.readUUID();
        data = dis.readNByteArr();
    }
}