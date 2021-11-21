package me.FurH.ShieldServer.filelist;

import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import me.FurH.Core.encript.Encrypto;
import me.FurH.ShieldServer.newlistener.CallbackWritter;
import me.FurH.ShieldServer.newlistener.CurrentScan;
import me.FurH.ShieldServer.newlistener.Encryption;
import me.FurH.ShieldServer.newlistener.NewClientListener;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askfilelist;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class FileCrawler {

    private String key;

    public final String basepath;
    private boolean donext;
    
    public final LinkedHashSet<FileObject> files = new LinkedHashSet<>();
   
    private Iterator<FileObject> sublist;
    private NewClientListener listener;
    private FileObject curlist;
    
    private String currentpath;

    public FileCrawler(String basepath) {
        this.basepath = basepath;
    }
    
    private void askNext() throws Exception {
        
        while (sublist.hasNext()) {
            
            FileObject obj = sublist.next();
            
            if (!obj.isDir()) {
                continue;
            }

            this.curlist = obj;
            currentpath = currentpath + "/" + obj.name;

            askList(currentpath);

            return;
        }

        sublist = null;
        listener.saveFiles();
    }

    public void start(NewClientListener listener) throws Exception {

        this.donext = true;
        this.listener = listener;
        
        askList(basepath);
    }

    private void askList(String path) throws Exception {

        this.currentpath = path;
        
        key = Encrypto.genRndPass(16);
        byte[] encrypted = Encryption.encrypt(key, path);

        listener.writePayload(new CallbackWritter() {

            @Override
            public void write(DataOutputStream dos) throws Throwable {

                dos.writeInt(payload_askfilelist);
                dos.writeUTF(key);

                dos.writeInt(encrypted.length);
                dos.write(encrypted);
            }
        });
    }

    public void process(CurrentScan scan, byte[] bytes) throws Exception {

        String data = Encryption.decrypt(key, bytes);
        key = null;

        int j1 = data.indexOf(':');
        int listpid = Integer.parseInt(data.substring(0, j1));

        if (scan.pid != listpid) {
            scan.handler.client.info("module list from wrong pid");
            return;
        }

        String list = data.substring(j1 + 1);
        StringTokenizer it = new StringTokenizer(list, "\n");

        while (it.hasMoreTokens()) {

            String input = it.nextToken();
            
            if (input.equals(".:1") || input.equals("..:1")) {
                continue;
            }
            
            int j2 = input.lastIndexOf(':');

            if (j2 <= 0) {
                continue;
            }

            String name = input.substring(0, j2);
            int type = Integer.parseInt(input.substring(j2 + 1));

            if (curlist != null) {
                curlist.add(new FileObject(name, type));
            } else {
                files.add(new FileObject(name, type));
            }
        }
        
        if (sublist != null) {
            
            askNext();
            
        } else if (donext) {
            
            donext = false;
            sublist = files.iterator();
            
            askNext();
        }
    }

    public void load(JSONArray arr) {
        for (int j1 = 0; j1 < arr.length(); j1++) {
            Object next = arr.get(j1);
            if (next instanceof String) {
                files.add(new FileObject((String) next, 0));
            } else {
                files.add(new FileObject((JSONObject) next));
            }
        }
    }
}