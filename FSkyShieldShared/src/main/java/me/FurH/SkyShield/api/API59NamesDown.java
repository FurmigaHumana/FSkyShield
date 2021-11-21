package me.FurH.SkyShield.api;

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
public class API59NamesDown extends Packet<PacketListener> {
    
    public int[] users;

    @Override
    public short getId() {
        return 59;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.namesDown(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        
        out.writeInt(users.length);
        
        for (int user : users) {
            out.writeInt(user);
        }
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        
        int size = in.readInt();
        users = new int[ size ];
        
        for (int j1 = 0; j1 < size; j1++) {
            users[ j1 ] = in.readInt();
        }
    }
}