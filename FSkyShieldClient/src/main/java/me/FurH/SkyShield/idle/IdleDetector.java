package me.FurH.SkyShield.idle;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import me.FurH.Core.executors.TimerExecutor;
import me.FurH.Core.time.TimeUtils;
import me.FurH.SkyShield.ShieldClient;
import me.FurH.SkyShield.attacher.NewAgentServer;
import me.FurH.SkyShield.gui.DiagnosticGui;
import me.FurH.SkyShield.packets.Packet75Sleep;
import me.FurH.SkyShield.packets.ping.Packet16Ping;
import me.FurH.SkyShield.win32.NativeShield;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class IdleDetector implements Runnable {

    public static boolean isIdle() {
        return idle;
    }

    private long lastactivity = 0;
    private long lasttick;
    
    private static boolean idle = false;
    private final ShieldClient client;
    private ScheduledFuture<?> goingsleep;
    
    public IdleDetector(ShieldClient client) {
        this.client = client;
    }

    public void schedule() {
        TimerExecutor.schedule(this, 60, 60, TimeUnit.SECONDS);
        schedulePing();
    }

    private void schedulePing() {

        TimerExecutor.schedule(new Runnable() {

            @Override
            public void run() {

                if (idle) {
                    return;
                }

                try {

                    if (client.connection.connected) {
                        //Packet19KeepAlive
                        Packet16Ping ping = new Packet16Ping();
                        ping.created = System.currentTimeMillis();
                        client.connection.write(ping);
                    }

                } finally {
                    
                    schedulePing();
                    
                }
            }
        }, 15, TimeUnit.SECONDS);
    }
    
    private void wakeUp() {
        
        idle = false;
        DiagnosticGui.log("Wake up");
        
        schedulePing();
        
        if (goingsleep != null) {
            goingsleep.cancel(true);
        }
        
        try {
            client.connection.initialize();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void sleep() {
        
        NewAgentServer server = client.newserver;
        
        if (server != null && !server.isIdle()) {
            DiagnosticGui.log("wont idle, has attachment");
            return;
        }

        idle = true;
        DiagnosticGui.log("Set idle");
        
        Packet75Sleep sleep = new Packet75Sleep();
        client.connection.write(sleep);

        goingsleep = TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                goingsleep = null;
                if (idle) {
                    client.connection.disconnect();
                }
            }
        }, 3, TimeUnit.SECONDS);
    }

    @Override
    public void run() {

        long newtick = NativeShield.lastInput();

        if (newtick != lasttick) {
            
            lastactivity = System.currentTimeMillis();
            lasttick = newtick;
            
            if (idle) {
                wakeUp();
            }
        }
        
        if (!idle && TimeUtils.isExpired(lastactivity, 2, TimeUnit.MINUTES)) {
            sleep();
        }
    }
}