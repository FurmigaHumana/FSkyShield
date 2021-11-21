package me.FurH.ShieldServer.nicks;

import java.util.concurrent.TimeUnit;
import me.FurH.Core.time.TimeUtils;
import me.FurH.Core.util.Utils;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class LoginTry {

    private final long timestamp;
    private int tries;
    
    public LoginTry() {
        this.timestamp = Utils.currentTimeMillis();
    }

    boolean isAllowedToTry() {
        return tries < 5;
    }

    void incTries() {
        this.tries++;
    }

    boolean isExpired() {
        return TimeUtils.isExpired(timestamp, 15, TimeUnit.MINUTES);
    }
}