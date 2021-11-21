package a.protocol.channel;

import static a.protocol.channel.NettyChannel.getMethod;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class AEventLoop {
    
    private static Method execute;
    private static Method schedule;
    
    private final Object eventloop;

    AEventLoop(Object eventloop) {
        this.eventloop = eventloop;
    }

    public void execute(Runnable runnable) throws Exception {

        if (execute == null) {
            execute = getMethod(eventloop, "execute", Runnable.class);
        }

        execute.invoke(eventloop, runnable);
    }

    public void schedule(Runnable runnable, int i, TimeUnit timeUnit) throws Exception {

        if (schedule == null) {
            schedule = getMethod(eventloop, "schedule", Runnable.class, Long.TYPE, TimeUnit.class);
        }

        schedule.invoke(eventloop, runnable, i, timeUnit);
    }

}