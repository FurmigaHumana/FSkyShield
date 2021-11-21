package me.FurH.ShieldServer.sources;

import java.sql.PreparedStatement;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQLThread;
import me.FurH.ShieldServer.bigdata.BigData;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class SourceResult {
    
    public String source;
    String compared;
    boolean interesting;

    public void storeCompare(int playid, int sourceid, SQLThread t) throws Exception {

        if (compared == null) {
            compared = "No compatible source code found";
        }
        
        int compareid = BigData.store(compared, playid, t);

        PreparedStatement ps = null;
        
        try {

            ps = t.prepare("@@REMOVED");
            t.commitNext();

            ps.setInt(1, sourceid);
            ps.setInt(2, compareid);
            ps.setBoolean(3, interesting);
            ps.setInt(4, compareid);
            ps.setBoolean(5, interesting);

            ps.execute();
            
        } finally {
            
            Closer.closeQuietly(ps);
            
        }
    }
}