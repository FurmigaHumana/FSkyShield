package me.FurH.SkyShield.packets.ping;

import java.io.IOException;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/**
/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Packet16Ping extends Packet<PacketListener> {

    public long created;
    
    public Packet16Ping() {
        this.created = System.currentTimeMillis();
    }
    
    @Override
    public short getId() {
        return 16;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.ping(this);
    }
    
    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeLong(created);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        created = in.readLong();
    }
}