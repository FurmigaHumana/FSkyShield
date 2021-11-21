package me.FurH.SkyShield.packets.cls;

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
public class Packet57SourceResult extends Packet<PacketListener> {

    public int pid;
    public String file;
    public boolean exists;

    public String hash;
    public byte[] data;
    public boolean found;

    @Override
    public short getId() {
        return 57;
    }
    
    @Override
    public boolean bigBuffer() {
        return true;
    }
    
    @Override
    public void handle(PacketListener listener) {
        listener.sourceResult(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        
        out.writeInt(pid);
        out.writeUTF(file);
        out.writeBoolean(exists);
        
        if (exists) {
            
            out.writeBoolean(found);
            out.writeUTF(hash);
            
            if (found) {
                out.writeNByteArr(data);
            }
        }
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        
        pid = in.readInt();
        file = in.readUTF();
        exists = in.readBoolean();
        
        if (exists) {
            
            found = in.readBoolean();
            hash = in.readUTF();
            
            if (found) {
                data = in.readNByteArr();
            }
        }
    }
}