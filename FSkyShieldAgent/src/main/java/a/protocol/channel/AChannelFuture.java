package a.protocol.channel;

import java.lang.reflect.Method;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class AChannelFuture {
    
    private static Method syncUninterruptibly;
    private final Object channelfuture;

    AChannelFuture(Object channelfuture) {
        this.channelfuture = channelfuture;
    }

    public void syncUninterruptibly() throws Exception {
        
        if (syncUninterruptibly == null) {
            syncUninterruptibly = NettyChannel.getMethod(channelfuture, "syncUninterruptibly");
        }
        
        syncUninterruptibly.invoke(channelfuture);
    }

    Object getHandle() {
        return channelfuture;
    }
}