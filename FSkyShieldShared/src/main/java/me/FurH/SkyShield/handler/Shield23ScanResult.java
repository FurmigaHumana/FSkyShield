/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.handler;

import java.io.IOException;
import java.util.UUID;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/**
 *
 * @author lgpse
 */
public class Shield23ScanResult extends Packet<PacketListener> {

    public UUID randomId;
    public UUID uniqueId;
    public double result;

    @Override
    public short getId() {
        return 23;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.scanData(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeUUID(randomId);
        out.writeUUID(uniqueId);
        out.writeDouble(result);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        randomId    = in.readUUID();
        uniqueId    = in.readUUID();
        result      = in.readDouble();
    }
}