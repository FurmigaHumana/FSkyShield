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
public class Packet54FileResult extends Packet<PacketListener> {

    public int pid;
    public String hash;
    public int bigid;

    @Override
    public short getId() {
        return 54;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.fileResult(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeInt(pid);
        out.writeUTF(hash);
        out.writeInt(bigid);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        pid = in.readInt();
        hash = in.readUTF();
        bigid = in.readInt();
    }

}