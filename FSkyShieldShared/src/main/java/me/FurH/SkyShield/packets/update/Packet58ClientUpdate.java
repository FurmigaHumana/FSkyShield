package me.FurH.SkyShield.packets.update;

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
public class Packet58ClientUpdate extends Packet<PacketListener> {

    public String md5;
    public byte[] data;

    @Override
    public short getId() {
        return 58;
    }

    @Override
    public boolean bigBuffer() {
        return true;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.clientUpdate(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeUTF(md5);
        out.writeNByteArr(data);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        md5 = in.readUTF();
        data = in.readNByteArr();
    }
}