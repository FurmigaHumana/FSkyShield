package a.protocol.channel;

import java.lang.reflect.Method;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class BypassInboundHandler {

    private static Method channelRegistered;
    private static Method channelUnregistered;
    private static Method channelActive;
    private static Method exceptionCaught;
    private static Method channelWritabilityChanged;
    private static Method userEventTriggered;
    private static Method channelReadComplete;
    private static Method channelRead;
    private static Method channelInactive;
    
    public void channelRegistered(Object ctx) throws Exception {
        
        if (channelRegistered == null) {
            channelRegistered = NettyChannel.getMethod(ctx, "fireChannelRegistered");
        }

        channelRegistered.invoke(ctx);
    }

    public void channelUnregistered(Object ctx) throws Exception {
        
        if (channelUnregistered == null) {
            channelUnregistered = NettyChannel.getMethod(ctx, "fireChannelUnregistered");
        }

        channelUnregistered.invoke(ctx);
    }

    public void channelActive(Object ctx) throws Exception {
       
        if (channelActive == null) {
            channelActive = NettyChannel.getMethod(ctx, "fireChannelActive");
        }

        channelActive.invoke(ctx);
    }

    public void channelInactive(Object ctx) throws Exception {
        
        if (channelInactive == null) {
            channelInactive = NettyChannel.getMethod(ctx, "fireChannelInactive");
        }

        channelInactive.invoke(ctx);
    }

    public void channelRead(Object ctx, Object msg) throws Exception {
        
        if (channelRead == null) {
            channelRead = NettyChannel.getMethod(ctx, "fireChannelRead", Object.class);
        }

        channelRead.invoke(ctx, msg);
    }

    public void channelReadComplete(Object ctx) throws Exception {
        
        if (channelReadComplete == null) {
            channelReadComplete = NettyChannel.getMethod(ctx, "fireChannelReadComplete");
        }

        channelReadComplete.invoke(ctx);
    }

    public void userEventTriggered(Object ctx, Object evt) throws Exception {
       
        if (userEventTriggered == null) {
            userEventTriggered = NettyChannel.getMethod(ctx, "fireUserEventTriggered", Object.class);
        }

        userEventTriggered.invoke(ctx, evt);
    }

    public void channelWritabilityChanged(Object ctx) throws Exception {
        
        if (channelWritabilityChanged == null) {
            channelWritabilityChanged = NettyChannel.getMethod(ctx, "fireChannelWritabilityChanged");
        }

        channelWritabilityChanged.invoke(ctx);
    }

    public void exceptionCaught(Object ctx, Object cause) throws Exception {
        
        if (exceptionCaught == null) {
            exceptionCaught = NettyChannel.getMethod(ctx, "fireExceptionCaught", Throwable.class);
        }

        exceptionCaught.invoke(ctx, cause);
    }

    public void handlerAdded(Object chc) throws Exception {
    }

    public void handlerRemoved(Object chc) throws Exception {
    }
}