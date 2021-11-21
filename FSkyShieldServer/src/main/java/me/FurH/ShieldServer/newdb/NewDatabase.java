package me.FurH.ShieldServer.newdb;

import java.io.File;
import java.util.HashMap;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class NewDatabase {

    private final HashMap<Integer, ProtocolList> protocols;
    
    public NewDatabase() {
        this.protocols = new HashMap<>();
    }

    public ProtocolList byProtocol(int protocol) {
        return protocols.get(protocol);
    }

    public void loadAll() {
        
        protocols.clear();
        
        // database structure
        // protocol
        //      - obfuscation revision number (compatible with maplist index)
        //          - version name
        //              - libraries
        //              - versions
        
        File dir = new File("protocols");
        
        for (File subdir : dir.listFiles()) {
            
            if (!subdir.isDirectory()) {
                continue;
            }
            
            try {
                
                Integer protocol = Integer.parseInt(subdir.getName());
                
                ProtocolList list = new ProtocolList();
                list.load(subdir);

                protocols.put(protocol, list);

            } catch (Throwable ex) {
                
                ex.printStackTrace();
                
            }
        }
    }

}