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
public class Packet63ResultCode extends Packet<PacketListener> {

    public static final int attached = 1;
   
    public int code;
    public int protocolindex;
    public UUID sessionId;
    
    @Override
    public short getId() {
        return 63;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.resultCode(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        
        dos.writeInt(code);

        if (code == attached) {
            dos.writeInt(protocolindex);
            dos.writeUUID(sessionId);
        }
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {

        this.code = dis.readInt();

        if (code == attached) {
            protocolindex = dis.readInt();
            sessionId = dis.readUUID();
        }
    }
}