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
public class Packet69AskCertificate extends Packet<PacketListener> {

    public String path;
    
    @Override
    public short getId() {
        return 69;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.askCertificate(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        dos.writeUTF(path);
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        path = dis.readUTF();
    }
}