package me.FurH.SkyShield.packets.scan2;

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
public class Packet55StoreFile extends Packet<PacketListener> {

    public int pid;
    public String hash;
    public byte[] checksum;

    @Override
    public short getId() {
        return 55;
    }
    
    @Override
    public boolean bigBuffer() {
        return true;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.storeFile(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeInt(pid);
        out.writeUTF(hash);
        out.writeNByteArr(checksum);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        pid = in.readInt();
        hash = in.readUTF();
        checksum = in.readNByteArr();
    }
}