package me.FurH.SkyShield.packets.cls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;
import me.FurH.SkyShield.packets.scan.ZipHash2;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Packet56RequestSources extends Packet<PacketListener> {
    
    public int pid;
    public HashMap<String, ArrayList<ZipHash2>> requests;
    
    @Override
    public short getId() {
        return 56;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.requestSources(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        
        out.writeInt(pid);
        out.writeInt(requests.size());
        
        for (Entry<String, ArrayList<ZipHash2>> entry : requests.entrySet()) {
            
            out.writeUTF(entry.getKey());
            
            ArrayList<ZipHash2> list = entry.getValue();
            
            out.writeInt(list.size());

            for (ZipHash2 hash : list) {
                out.writeUTF(hash.name);
                out.write(hash.md5);
            }
        }
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {

        pid = in.readInt();
        requests = new HashMap<>();
        
        int size1 = in.readInt();
        
        for (int j1 = 0; j1 < size1; j1++) {
            
            String key = in.readUTF();
            
            ArrayList<ZipHash2> list = new ArrayList<>();
            int size2 = in.readInt();
            
            for (int j2 = 0; j2 < size2; j2++) {
                
                ZipHash2 hash = new ZipHash2(false);
                
                hash.name = in.readUTF();
                hash.md5 = new byte[ 16 ];
                
                in.readFully(hash.md5);
                
                list.add(hash);
            }
            
            requests.put(key, list);
        }
    }
}