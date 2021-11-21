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
public class Packet79AddNick extends Packet<PacketListener> {

    public String name;
    public byte[] pw;
    
    public String reply;
    public Integer[] ids;
    
    @Override
    public short getId() {
        return 79;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.addNick(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        
        out.writeUTF(name);

        out.writeBoolean(reply != null);
        if (reply != null) {
            out.writeUTF(reply);
        } else {
            out.writeNByteArr(pw);
        }
        
        out.writeBoolean(ids != null);
        if (ids != null) {
            out.writeIntArr(ids);
        }
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        
        name = in.readUTF();
        
        if (in.readBoolean()) {
            reply = in.readUTF();
        } else {
            pw = in.readNByteArr();
        }
        
        if (in.readBoolean()) {
            ids = in.readIntArr();
        }
    }
}