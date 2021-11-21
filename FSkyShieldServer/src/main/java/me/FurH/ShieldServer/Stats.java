/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer;

import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.executors.TimerExecutor;

/**
 *
 * @author lgpse
 */
public class Stats {

    private final MainServer server;
    
    public static int setups = 0;
    public static int scans = 0;
    
    public Stats(MainServer server) {
        this.server = server;
    }
    
    public void schedule() {
        TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                scheduleNow();
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    private void scheduleNow() {
        
        final int setups0 = setups;
        final int scans0 = scans;
        
        setups = 0;
        scans = 0;
        
        SQL.mslow(new SQLTask() {
            
            @Override
            public void execute(SQLDb sqldb, SQLThread t) throws Throwable {

                PreparedStatement ps = null;
                
                try {
                    
                    ps = t.prepare("@@REMOVED");
                    t.commitNext();
                    
                    int j1 = 0;

                    ps.setInt(++j1, server.server.connections().size());
                    ps.setInt(++j1, setups0);
                    ps.setInt(++j1, scans0);

                    ps.execute();
                    
                } finally {
                    
                    Closer.closeQuietly(ps);
                    
                }
            }
        });
    }
}
