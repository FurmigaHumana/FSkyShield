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
public class Packet67FileList extends Packet<PacketListener> {

    public String javaver;
    public String javadir;
    public String[] files;
    
    @Override
    public short getId() {
        return 67;
    }

    @Override
    public boolean bigBuffer() {
        return true;
    }
    
    @Override
    public void handle(PacketListener listener) {
        listener.fileList(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        dos.writeUTF(javaver);
        dos.writeUTF(javadir);
        dos.writeUTFArr(files);
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        javaver = dis.readUTF();
        javadir = dis.readUTF();
        files = dis.readUTFArr();
    }
}