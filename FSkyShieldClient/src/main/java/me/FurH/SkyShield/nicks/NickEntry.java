package me.FurH.SkyShield.nicks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class NickEntry {

    final String name;
    public final Integer[] ids;

    public NickEntry(String name, Integer[] ids) {
        this.name = name;
        this.ids = ids;
    }

    NickEntry(DataInputStream dis) throws IOException {
        this.name = dis.readUTF();
        this.ids = new Integer[ dis.readByte()];
        for (int j1 = 0; j1 < ids.length; j1++) {
            this.ids[ j1 ] = dis.readInt();
        }
    }

    void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(name);
        dos.writeByte(ids.length);
        for (int id : ids) {
            dos.writeInt(id);
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}