package me.FurH.SkyShield.packets.newscan;

import java.io.IOException;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Packet70Certificate extends Packet69AskCertificate {

    public boolean signed;
    public String sigdata;
    
    @Override
    public short getId() {
        return 70;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.fileCertificate(this);
    }

    @Override
    public void writeExternal(DataOutputStream dos) throws IOException {
       
        super.writeExternal(dos);
       
        dos.writeBoolean(signed);
        dos.writeUTF(sigdata);
    }

    @Override
    public void readExternal(DataInputStream dis) throws IOException {
        
        super.readExternal(dis);
        
        signed = dis.readBoolean();
        sigdata = dis.readUTF();
    }
}