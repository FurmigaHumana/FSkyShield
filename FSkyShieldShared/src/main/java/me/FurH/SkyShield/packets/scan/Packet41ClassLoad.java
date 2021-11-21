/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.packets.scan;

import java.io.IOException;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/**
 *
 * @author lgpse
 */
public class Packet41ClassLoad extends Packet<PacketListener> {

    public int pid;
    public String clsname;
    
    @Override
    public short getId() {
        return 41;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.classLoad(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeInt(pid);
        out.writeUTF(clsname);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        pid = in.readInt();
        clsname = in.readUTF();
    }
}