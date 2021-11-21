package me.FurH.SkyShield.packets.nicks;

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
public class Packet77NickOpen extends Packet<PacketListener> {

    public String key;
    
    @Override
    public short getId() {
        return 77;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.nickOpen(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeBoolean(key != null);
        if (key != null) {
            out.writeUTF(key);
        }
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        if (in.readBoolean()) {
            key = in.readUTF();
        }
    }
}