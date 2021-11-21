package me.FurH.SkyShield.packets.big;

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
public class Packet48BigResult extends Packet<PacketListener> {

    public String hash;
    public int id;
    
    @Override
    public short getId() {
        return 48;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.bigResult(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeUTF(hash);
        out.writeInt(id);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        hash = in.readUTF();
        id = in.readInt();
    }

}