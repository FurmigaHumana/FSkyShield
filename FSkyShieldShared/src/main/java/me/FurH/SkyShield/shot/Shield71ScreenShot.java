package me.FurH.SkyShield.shot;

import java.io.IOException;
import java.util.UUID;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Shield71ScreenShot extends Packet<PacketListener> {
    
    public UUID requester;
    public UUID targetuuid;

    @Override
    public short getId() {
        return 71;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.reqScreenShot(this);
    }
    
    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        
        out.writeBoolean(requester != null);
        
        if (requester != null) {
            out.writeUUID(requester);
        }
        
        out.writeUUID(targetuuid);
    }

    @Override
    public void readExternal(DataInputStream in) throws IOException {
        
        if (in.readBoolean()) {
            requester = in.readUUID();
        }
        
        targetuuid = in.readUUID();
    }
}