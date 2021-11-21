package me.FurH.SkyShield.nicks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import me.FurH.Core.close.Closer;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.file.FileUtils;
import static me.FurH.SkyShield.ShieldClient.uniqueId;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class NickManager {
    
    private static final String filepw = "@@REMOVED";
    
    private final ArrayList<NickEntry> names;
    private final File file;

    private boolean loaded = false;

    public NickManager() {
        this.names = new ArrayList<>();
        this.file = new File("@@REMOVED");
    }
    
    public boolean has(int i) {
        
        if (names.isEmpty()) {
            return false;
        }

        return names.get(0).ids[0] == i;
    }
    
    public void remove(String name) {
        Iterator<NickEntry> it = names.iterator();
        while (it.hasNext()) {
            if (it.next().name.equalsIgnoreCase(name)) {
                it.remove();
            }
        }
    }
    
    public String[] getNameList() {
       
        String[] arr = new String[ this.names.size() ];
        for (int j1 = 0; j1 < arr.length; j1++) {
            arr[ j1 ] = names.get(j1).name;
        }
        
        return arr;
    }
    
    public boolean isEmpty() {
        return names.isEmpty();
    }

    public void add(NickEntry nickEntry) {
        this.names.add(nickEntry);
    }

    public NickEntry[] getList() {
        return this.names.toArray(new NickEntry[0]);
    }
    
    private String genPw() {
        return filepw + file.getAbsolutePath() + uniqueId.toString();
    }

    public void loadAll() {

        if (loaded || !file.exists()) {
            return;
        }
        
        loaded = true;
        
        try {

            byte[] data = Encrypto.aes_decrypt(file, genPw());
            readAll(data);
            
        } catch (Exception ex) {

            ex.printStackTrace();
            
        }
    }

    private void readAll(byte[] data) throws IOException {
        
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        GZIPInputStream giz = null;
        DataInputStream dis = null;
        
        try {
            
            giz = new GZIPInputStream(bais);
            dis = new DataInputStream(giz);
            
            int size = dis.readInt();
            
            for (int j1 = 0; j1 < size; j1++) {
                NickEntry entry = new NickEntry(dis);
                names.add(entry);
            }
            
        } finally {
            
            Closer.closeQuietly(dis);
            Closer.closeQuietly(giz);
            
        }
    }
    
    public void saveAll() {

        if (names.isEmpty()) {
            return;
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        GZIPOutputStream goz = null;
        DataOutputStream dos = null;
        
        try {
            
            goz = new GZIPOutputStream(baos);
            dos = new DataOutputStream(goz);
            
            dos.writeInt(names.size());
            
            for (NickEntry entry : names) {
                entry.write(dos);
            }
            
            dos.flush();
            goz.flush();
            goz.finish();
            
            Closer.closeQuietly(dos);
            Closer.closeQuietly(goz);
            
            byte[] data = baos.toByteArray();
            byte[] encrypt = Encrypto.aes_encrypt(data, genPw());

            FileUtils.setBytesOfFile(file, encrypt);

        } catch (Exception ex) {

            ex.printStackTrace();
            
        } finally {
            
            Closer.closeQuietly(dos);
            Closer.closeQuietly(goz);
            
        }
    }
}