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
public class Shield65PayloadData extends Packet<PacketListener> {

    public UUID uniqueId;
    public byte[] payload;
    
    @Override
    public short getId() {
        return 65;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.payloadData(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        dos.writeUUID(uniqueId);
        dos.writeNByteArr(payload);
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        uniqueId = dis.readUUID();
        payload = dis.readNByteArr();
    }
}