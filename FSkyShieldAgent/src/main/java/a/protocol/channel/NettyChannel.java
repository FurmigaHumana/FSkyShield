package a.protocol.channel;

import static a.Agent.print;
import java.lang.reflect.Method;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class NettyChannel {

    private static Method eventLoop;
    private static Method alloc;
    private static Method isActive;
    private static Method pipeline;
    private static Method writeAndFlush;
    private static Method closeFuture;
    
    private final Object channel;

    public NettyChannel(Object channel) {
        this.channel = channel;
    }
    
    public static Method getMethod(Object object, String name, Class<?>...parameters) {
        
        Method m = null;
        
        try {
            
            m = object.getClass().getMethod(name, parameters);
            
        } catch (Throwable ex) {
            
            print(ex);
            
            try {
                m = object.getClass().getDeclaredMethod(name, parameters);
            } catch (Throwable ex2) {
                print(ex2);
            }
        }

        m.setAccessible(true);

        return m;
    }
    
    public AEventLoop eventLoop() throws Exception {
        
        if (eventLoop == null) {
            eventLoop = getMethod(channel, "eventLoop");
        }
        
        return new AEventLoop(eventLoop.invoke(channel));
    }

    public AByteBufAllocator alloc() throws Exception {
        
        if (alloc == null) {
            alloc = getMethod(channel, "alloc");
        }
        
        return new AByteBufAllocator(alloc.invoke(channel));
    }

    public boolean isActive() throws Exception {
        
        if (isActive == null) {
            isActive = getMethod(channel, "isActive");
        }
        
        return (Boolean) isActive.invoke(channel);
    }

    public AChannelPipeline pipeline() throws Exception {
        
        if (pipeline == null) {
            pipeline = getMethod(channel, "pipeline");
        }
        
        return new AChannelPipeline(pipeline.invoke(channel));
    }

    public AChannelFuture writeAndFlush(Object packet) throws Exception {
        
        if (writeAndFlush == null) {
            writeAndFlush = getMethod(channel, "writeAndFlush", Object.class);
        }
        
        return new AChannelFuture(writeAndFlush.invoke(channel, packet));
    }
    
    public AChannelFuture closeFuture() throws Exception {
        
        if (closeFuture == null) {
            closeFuture = getMethod(channel, "closeFuture");
        }
        
        return new AChannelFuture(closeFuture.invoke(channel));
    }

    public Object getHandle() {
        return channel;
    }
}