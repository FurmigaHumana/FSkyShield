package me.FurH.SkyShield.packets.newscan;

import java.io.IOException;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Packet73ClientPayload extends Packet<PacketListener> {

    public int action;
    public byte[] data;

    @Override
    public short getId() {
        return 76;
    }
    
    @Override
    public boolean bigBuffer() {
        return true;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.clientPayload(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        dos.writeInt(action);
        dos.writeInt(data.length);
        dos.write(data);
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        
        action = dis.readInt();
        
        int size = dis.readInt();
        data = new byte[ size ];
        dis.readFully(data);
    }
    
}