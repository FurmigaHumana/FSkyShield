package me.FurH.SkyShield.shot;

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
public class Packet72ScreenData extends Packet<PacketListener> {

    public UUID requester;
    public int pid;
    public boolean error;
    public String message;
    public String url;
    
    @Override
    public short getId() {
        return 72;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.screenData(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        dos.writeUUID(requester);
        dos.writeInt(pid);
        dos.writeBoolean(error);
        if (error) {
            dos.writeUTF(message);
        } else {
            dos.writeUTF(url);
        }
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        requester = dis.readUUID();
        pid = dis.readInt();
        error = dis.readBoolean();
        if (error) {
            message = dis.readUTF();
        } else {
            url = dis.readUTF();
        }
    }

}