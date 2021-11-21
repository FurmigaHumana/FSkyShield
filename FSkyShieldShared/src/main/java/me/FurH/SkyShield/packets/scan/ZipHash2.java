package me.FurH.SkyShield.packets.scan;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ZipHash2 {
    
    private final boolean loadsize;

    public String name;
    public byte[] md5;
    public int size;
    
    // internal use only
    public String md5cache;
    private int hashCode;
    
    public ZipHash2(boolean loadsize) {
        this.loadsize = loadsize;
    }

    @Override
    public boolean equals(Object obj) {
       
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        final ZipHash2 other = (ZipHash2) obj;
        return Arrays.equals(this.md5, other.md5);
    }

    @Override
    public int hashCode() {

        if (hashCode == -1) {

            int hash = 3;
            hash = 83 * hash + Arrays.hashCode(this.md5);
            
            hashCode = hash;
        }
        
        return hashCode;
    }

    public void read(DataInputStream dis) throws IOException {

        name = dis.readUTF();
        md5 = new byte[ 16 ];
        dis.readFully(md5);

        if (loadsize) {
            size = dis.readInt();
        }
    }

    public void write(DataOutputStream dos) throws IOException {
       
        dos.writeUTF(name);
        dos.write(md5);
        
        if (loadsize) {
            dos.writeInt(size);
        }
    }
}