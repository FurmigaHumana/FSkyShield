/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.api;

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
public class API32PrintResult extends Packet<PacketListener> {

    public String handler = "";
    public UUID requester;
    public boolean error;
    public String message;
    public int scan;

    @Override
    public short getId() {
        return 32;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.printResult(this);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        out.writeUTF(handler);
        out.writeUUID(requester);
        out.writeBoolean(error);
        out.writeUTF(message);
        out.writeInt(scan);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        handler =  in.readUTF();
        requester = in.readUUID();
        error = in.readBoolean();
        message = in.readUTF();
        scan = in.readInt();
    }
}