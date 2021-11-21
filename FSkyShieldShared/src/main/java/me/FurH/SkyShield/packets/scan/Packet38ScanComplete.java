/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.packets.scan;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;
import me.FurH.Core.close.Closer;
import me.FurH.Core.util.Utils;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/**
 *
 * @author lgpse
 */
public class Packet38ScanComplete extends Packet<PacketListener> {

    public int pid;
    public byte[] clslist;
    
    @Override
    public short getId() {
        return 38;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.scanCompleted(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeInt(pid);
        out.writeInt(clslist.length);
        out.write(clslist);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        pid = in.readInt();
        clslist = new byte[ in.readInt() ];
        in.readFully(clslist);
    }
    
    public String getClassList() throws IOException {
        
        StringBuilder sb = new StringBuilder();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(clslist);
        InflaterInputStream dis = null;
        
        try {
            
            dis = new InflaterInputStream(bais);
            byte[] buffer = new byte[ 1024 ];
            
            int read;
            while ((read = dis.read(buffer)) >= 0) {
                sb.append(new String(buffer, 0, read, Utils.UTF8));
            }

        } finally {
            
            Closer.closeQuietly(dis);
            
        }
        
        return sb.toString();
    }
}