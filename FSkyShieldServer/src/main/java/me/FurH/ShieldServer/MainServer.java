/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import me.FurH.Async.AsyncExecutor;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.executors.TaskExecutor;
import me.FurH.Core.executors.TimerExecutor;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.http.HttpUtils;
import me.FurH.Core.internal.RemoteSets;
import me.FurH.Core.util.Callback;
import me.FurH.Core.util.JMX;
import me.FurH.Core.util.Sleeper;
import me.FurH.Logger.InputReader;
import me.FurH.Logger.LogFactory;
import me.FurH.NIO.buffer.BufferAllocator;
import me.FurH.NIO.executor.AbstractExecutor;
import me.FurH.NIO.server.Server;
import me.FurH.ShieldServer.database.DatabaseManager;
import me.FurH.ShieldServer.listener.HandshakeListener;
import me.FurH.ShieldServer.listener.PacketLoader;
import me.FurH.ShieldServer.newdb.NewDatabase;
import me.FurH.ShieldServer.newlistener.Resources;
import me.FurH.ShieldServer.nicks.NickManager;
import me.FurH.ShieldServer.server.ShieldClient;
import me.FurH.ShieldServer.server.ShieldServer;
import me.FurH.ShieldServer.sources.SourceManager;
import me.FurH.SkyShield.packets.ping.Packet16Ping;

/**
 *
 * @author Luis
 */
public class MainServer {

    public static MainServer instance;
    public final ShieldServer server;

    public final DatabaseManager database;
    public final SourceManager sources;
    public final Stats stats;
    public final NewDatabase newdatabase;
    public final Resources resources;

    public final NickManager nicks;
    private static String shieldip;

    public MainServer() throws Exception {
        this.server = new ShieldServer(this);
        this.database = new DatabaseManager(this);
        this.sources = new SourceManager(this);
        this.stats = new Stats(this);
        this.newdatabase = new NewDatabase();
        this.resources = new Resources(this);
        this.nicks = new NickManager(this);
    }

    public static void main(String[] args) throws Exception {
                
        Sleeper.schedule(new Runnable() {

            @Override
            public void run() {
                
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ex) { }
                
                System.err.println("Server shutdown time");
                Sleeper.hardExirOrKill();
            }
        });

        LogFactory.initialize(new File("logs"));

        PacketLoader.loadPackets();

        File temp = new File("temp");
        
        if (temp.exists()) {
            FileUtils.deleteDirectory(temp);
        }
        
        try {

            instance = new MainServer();
            instance.initialize();
       
            System.out.println("Done");
            
            Thread.sleep(Integer.MAX_VALUE);

        } catch (Throwable ex) {
            
            ex.printStackTrace();
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex1) { }
        }
    }
    
    private void initialize() throws IOException {

        AsyncExecutor.setServerMode();

        database.loadAll();
        sources.loadAll();
        
        stats.schedule();
        newdatabase.loadAll();

        TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtils.readString("@@REMOVED");
                } catch (Throwable ex) { }
            }
        }, 1, 1, TimeUnit.MINUTES);

        /*TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                for (ShieldClient client : instance.server.connections()) {
                    client.ping();
                }
            }
        }, 30, 30, TimeUnit.SECONDS);*/

        SQL.mslow(new SQLTask() {
            
            @Override
            public void execute(SQLDb database, SQLThread t) throws Throwable {

                invalidateAll(t);
                resources.loadAll(t);

                try {
                    
                    shieldip = RemoteSets.getStr("shield", "ShieldIp", "@@REMOVED, t);
                    server.initialize(shieldip);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        InputReader in = new InputReader(System.in) {
            @Override
            public void input(String line) {
                try {
                    doCommand(line);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };

        in.start(false);
    }

    private void invalidateAll(SQLThread t) throws SQLException {
        
        PreparedStatement ps = null;

        try {

            ps = t.prepare("@@REMOVED");
            t.commitNext();
            
            ps.execute();

        } finally {

            Closer.closeQuietly(ps);

        }
    }

    private static void doCommand(String line) throws SQLException {

        switch (line) {
            
            case "jmx stop": {

                JMX.stopJMX();
                System.out.println("JMX Stopped.");

                break;
            }
            case "jmx start": {
                
                JMX.startJMX(shieldip, new Callback<Object>() {
                    
                    @Override
                    public void invoke(Object result) {
                        
                        if (result == null) {
                            System.out.println("§cO JMX já esta em execução!");
                            return;
                        }

                        if (result instanceof Integer) {
                            System.out.println("§aIniciado em: " + (shieldip+":"+result));
                            return;
                        }

                        if (result instanceof Throwable) {

                            Throwable ex = ((Throwable) result);
                            ex.printStackTrace();

                            System.out.println("§cErro: " + ex.getMessage());
                        }
                    }
                });
                
                break;
            }

            case "info": {

                System.out.println("Connections: " + instance.server.connections().size());

                AbstractExecutor rexec = Server.getPoolExecutor();

                System.out.println("Executor queue: " + rexec.getQueueSize0());
                System.out.println("Executor threads: " + rexec.getMaxPoolSize());
                System.out.println("Executor actived: " + rexec.getPoolSize());
                System.out.println("Executor available: " + rexec.getAvailable());

                int wqueue = 0;
                for (ShieldClient client : instance.server.connections()) {
                    wqueue += client.size();
                }

                System.out.println("Write queue: " + wqueue);
                BufferAllocator.printHeapData();

                break;
            }
            case "test": {

                final long start1 = System.currentTimeMillis();

                TimerExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("TimerExecutor is working: " + (System.currentTimeMillis() - start1) + " ms");
                    }
                }, 1, TimeUnit.MILLISECONDS);

                final long start2 = System.currentTimeMillis();

                TaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("TaskExecutor is working: " + (System.currentTimeMillis() - start2) + " ms");
                    }
                });

                break;
            }
            case "reload": {

                try {
                    instance.database.loadAll();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                instance.newdatabase.loadAll();

                System.out.println("Database reloaded");

                break;
            }
            case "handlers": {

                int pings = 0;

                for (ShieldClient client : instance.server.connections()) {

                    if (!client.isHandler()) {
                        continue;
                    }

                    pings++;

                    try {
                        client.write(new Packet16Ping());
                    } catch (Throwable ex) {
                        System.out.println("Error on ping: " + ex.getMessage());
                    }
                }

                System.out.println("Sent out " + pings + " ping requests");

                break;
            }
            case "ping": {

                HandshakeListener.presp = 0;
                HandshakeListener.psents = 0;
                
                for (ShieldClient client : instance.server.connections()) {

                    HandshakeListener.psents++;

                    try {
                        client.write(new Packet16Ping());
                    } catch (Throwable ex) {
                        System.out.println("Error on ping: " + ex.getMessage());
                    }
                }

                System.out.println("Sent out " + HandshakeListener.psents + " ping requests");
                break;
            }
            case "stop": {
                System.exit(0);
                break;
            }
        }
    }
}
