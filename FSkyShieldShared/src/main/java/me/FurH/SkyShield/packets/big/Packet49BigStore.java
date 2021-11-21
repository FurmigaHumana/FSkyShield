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
public class Packet49BigStore extends Packet<PacketListener> {

    public String hash;
    public byte[] data;
    
    @Override
    public short getId() {
        return 49;
    }
    
    @Override
    public boolean bigBuffer() {
        return true;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.bigStore(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeUTF(hash);
        out.writeNByteArr(data);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        hash = in.readUTF();
        data = in.readNByteArr();
    }

}