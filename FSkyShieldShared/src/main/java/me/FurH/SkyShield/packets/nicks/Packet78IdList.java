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
public class Packet78IdList extends Packet<PacketListener> {

    public Integer[] ids;
    
    @Override
    public short getId() {
        return 78;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.idList(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeIntArr(ids);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        ids = in.readIntArr();
    }
}