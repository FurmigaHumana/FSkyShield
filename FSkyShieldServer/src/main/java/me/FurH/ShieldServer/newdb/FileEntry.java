package me.FurH.ShieldServer.newdb;

import java.io.File;
import java.security.MessageDigest;
import me.FurH.Core.encript.Encrypto;
import me.FurH.SkyShield.Constants.FileHash;
import me.FurH.SkyShield.Constants.HashProvider;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class FileEntry {
    
    private static final HashProvider provider;
    private static String key;

    static {

        provider = new HashProvider() {

            @Override
            public byte[] hash(byte[] baos) throws Exception {

                MessageDigest md = MessageDigest.getInstance("MD5");

                md.update(baos);
                md.update(getKey().getBytes("UTF-8"), 0, 5);

                return md.digest();
            }
        };
    }
    
    private String file;
    private String md5;

    FileEntry(File file) {
        this.file = file.getAbsolutePath();
    }
    
    public static String getKey() {

        if (key == null) {
            key = Encrypto.genRndPass(16);
        }
        
        return key;
    }

    boolean compatible(String hash, boolean ziphash) {

        if (md5 == null) {
            
            try {
                
                if (!ziphash) {
                    this.md5 = Encrypto.hash("MD5", new File(file));
                } else {
                    this.md5 = Encrypto.hex(FileHash.zipHash(new File(file), provider));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            this.file = null;
        }

        return (md5 == null ? hash == null : md5.equals(hash));
    }

}