package me.FurH.SkyShield.Constants;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class FileHash {

    /*private static final SoftMap<String, String> quickHash;

    public static final int CLASS_HEADER  = -889275714;
    public static final int ZIP_HEADER    = 1347093252;

    static {
        quickHash = new SoftMap<>();
    }
    
    public static HashMap<String, byte[]> fetchSources(File file, ArrayList<ZipHash2> entries) throws Exception {
        
        HashMap<String, byte[]> ret = new HashMap<>();
        
        for (ZipHash2 entry : entries) {
            entry.md5cache = Encrypto.hex(entry.md5);
            ret.put(entry.md5cache, new byte[ 0 ]);
        }

        MessageDigest md2 = MessageDigest.getInstance("MD5");
        
        BufferedInputStream bis = null;
        FileInputStream fis     = null;
        ZipInputStream zis      = null;

        try {

            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            byte[] buffer   = new byte[ 1024 * 8 ];
            
            zis = new ZipInputStream(bis, Charset.forName("CP866"));

            ZipEntry entry = zis.getNextEntry();
            
            StrBuffers buffers = Compressor.getLocal();
            buffers.reset();

            ByteArrayOutputStream baos = buffers.baos;

            while (entry != null) {

                boolean check = false;
                Integer ehead = null;

                int read;
                
                baos.reset();

                while ((read = zis.read(buffer)) > 0) {

                    if (ehead == null) {
                        ehead = getInt(buffer, 0);
                    }

                    if (ehead == ZIP_HEADER || ehead == CLASS_HEADER) {

                        check = true;

                        md2.update(buffer, 0, read);
                        baos.write(buffer, 0, read);
                    }
                }

                if (check) {

                    String md5 = Encrypto.hex(md2.digest());

                    for (ZipHash2 hash : entries) {
                        if (hash.md5cache.equals(md5)) {
                            ret.put(md5, baos.toByteArray());
                        }
                    }
                }

                entry = zis.getNextEntry();
            }

            return ret;
            
        } finally {

            Closer.closeQuietly(fis);
            Closer.closeQuietly(bis);
            Closer.closeQuietly(zis);
            
        }
    }

    public static String quickMD5(File file) throws Exception {

        String key = file.getAbsolutePath() + ":" + file.lastModified() + ":" + file.length();
        String hash = quickHash.get(key);
        
        if (hash != null) {
            return hash;
        }
        
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = null;
        
        try {

            fis = new FileInputStream(file);
            byte[] buffer = new byte[ 4096 ];
            
            int read;
            while ((read = fis.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
            
            md.update("@@REMOVED".getBytes(Utils.UTF8));
            
        } finally {
            
            Closer.closeQuietly(fis);
            
        }
        
        hash = Encrypto.hex(md.digest());
        quickHash.put(key, hash);
        
        return hash;
    }*/
    
    public static byte[] zipHash(File file, HashProvider provider) throws Exception {
        
        MessageDigest md = MessageDigest.getInstance("MD5");

        BufferedInputStream bis = null;
        FileInputStream fis     = null;
        ZipInputStream zis      = null;

        try {
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            zis = new ZipInputStream(bis, Charset.forName("CP866"));

            byte[] buffer = new byte[ 1024 * 8 ];
            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {

                baos.reset();
                int read;

                while ((read = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, read);
                }

                md.update(provider.hash(baos.toByteArray()));
                entry = zis.getNextEntry();
            }

            return md.digest();

        } finally {

            closeQuietly(fis);
            closeQuietly(bis);
            closeQuietly(zis);

        }
    }
    
    private static void closeQuietly(InputStream is) {
        try {
            is.close();
        } catch (Throwable ex) { }
    }

    /*
    private static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF)      ) +
               ((b[off + 2] & 0xFF) <<  8) +
               ((b[off + 1] & 0xFF) << 16) +
               ((b[off    ]       ) << 24);
    }

    private static int getInputHead(BufferedInputStream in) {

        byte[] b = new byte[ 4 ];
        in.mark(b.length);

        try {

            for (int i = 0; i < b.length; i++) {
                b[ i ] = (byte) in.read();
            }

        } catch (IOException e) {

            return 0;

        } finally {
            try {
                in.reset();
            } catch (IOException ex) { }
        }

        return getInt(b, 0);
    }*/
}