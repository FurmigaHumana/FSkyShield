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
public class Packet62AttachTo extends Packet<PacketListener> {

    public int pid;
    public String protocol;
    public String key;

    @Override
    public short getId() {
        return 62;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.attachTo(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        dos.writeInt(pid);
        dos.writeUTF(protocol);
        dos.writeUTF(key);
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        pid = dis.readInt();
        protocol = dis.readUTF();
        key = dis.readUTF();
    }

}