package me.FurH.SkyShield.attacher;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import me.FurH.Core.executors.TimerExecutor;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_attachtimeout;
import me.FurH.SkyShield.newscan.NewGameScanner;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public abstract class AgentCallback {
    
    private final NewGameScanner handler;
    final int pid;

    private ScheduledFuture<?> timeout;

    public AgentCallback(int pid, NewGameScanner handler) {
        this.pid = pid;
        this.handler = handler;
    }

    void result(NewAgentClient client) {
        
        if (timeout != null) {
            timeout.cancel(true);
        }
        
        this.callback(client);
    }

    public abstract void callback(NewAgentClient client);

    void countTimeout() {
        timeout = TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                handler.writeError("Não foi possível anexar a tempo", error_attachtimeout);
            }
        }, 15, TimeUnit.SECONDS);
    }

}