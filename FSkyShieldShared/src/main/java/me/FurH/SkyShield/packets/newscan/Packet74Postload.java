package me.FurH.SkyShield.packets.newscan;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Packet74Postload extends Packet<PacketListener> {

    public HashMap<String, HashSet<String>> postloaded;
    
    @Override
    public short getId() {
        return 74;
    }
    
    @Override
    public boolean bigBuffer() {
        return true;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.postload(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
        
        dos.writeInt(postloaded.size());
        
        for (Entry<String, HashSet<String>> entry : postloaded.entrySet()) {
            
            dos.writeUTF(entry.getKey());
            
            HashSet<String> list = entry.getValue();
            
            dos.writeInt(list.size());
            
            for (String cls : list) {
                dos.writeUTF(cls);
            }
        }
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {

        postloaded = new HashMap<>();
        int size = dis.readInt();

        for (int j1 = 0; j1 < size; j1++) {
            
            String path = dis.readUTF();
            int size2 = dis.readInt();
            
            HashSet<String> list = new HashSet<>();
            postloaded.put(path, list);
            
            for (int j2 = 0; j2 < size2; j2++) {
                list.add(dis.readUTF());
            }
        }
    }
}