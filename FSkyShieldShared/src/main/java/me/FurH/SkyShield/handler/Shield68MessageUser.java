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
public class Shield68MessageUser extends Packet<PacketListener> {

    public UUID uniqueId;
    public String message;
    
    @Override
    public short getId() {
        return 68;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.messageUser(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        dos.writeUUID(uniqueId);
        dos.writeUTF(message);
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        uniqueId = dis.readUUID();
        message = dis.readUTF();
    }
}