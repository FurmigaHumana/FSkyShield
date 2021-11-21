package me.FurH.ShieldServer.newdb;

import java.io.File;
import java.util.HashMap;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ProtocolList {

    private final HashMap<Integer, RevisionList> revisions;
    
    public ProtocolList() {
        this.revisions = new HashMap<>();
    }

    public RevisionList byRevision(int revision) {
        return revisions.get(revision);
    }

    void load(File subdir) {

        for (File revdir : subdir.listFiles()) {

            if (!revdir.isDirectory()) {
                continue;
            }
            
            String name = revdir.getName();
            
            if (!name.startsWith("rev")) {
                continue;
            }
            
            try {
                
                Integer index = Integer.parseInt(name.substring(3));

                RevisionList list = new RevisionList();
                list.load(revdir);
                revisions.put(index, list);

            } catch (Throwable ex) {
                
                ex.printStackTrace();
                
            }
        }
    }

}