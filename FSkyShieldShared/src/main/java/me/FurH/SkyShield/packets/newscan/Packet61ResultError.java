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
public class Packet61ResultError extends Packet<PacketListener> {
    
    public int errorid;
    public String message;
    
    @Override
    public short getId() {
        return 61;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.resultError(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        
        dos.writeInt(errorid);
        
        dos.writeBoolean(message != null);
        if (message != null) {
            dos.writeUTF(message);
        }
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        
        errorid = dis.readInt();
        
        if (dis.readBoolean()) {
            message = dis.readUTF();
        }
    }
}