/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.packets.setup;

import java.io.IOException;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/**
 *
 * @author lgpse
 */
public class Packet35AgentData extends Packet<PacketListener> {

    public byte[] data;
    public String md5;
    
    @Override
    public short getId() {
        return 35;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.agentData(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        
        out.writeInt(data.length);
        out.write(data);
        
        out.writeUTF(md5);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
       
        data = new byte[ in.readInt() ];
        in.readFully(data);
        
        md5 = in.readUTF();
    }
}