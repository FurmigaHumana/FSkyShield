/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.packets;

import java.io.IOException;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/**
 *
 * @author lgpse
 */
public class Packet18CheckSession extends Packet<PacketListener> {

    @Override
    public short getId() {
        return 18;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.checkSession(this);
    }
    
    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
    }
}