package a.protocol;

import a.Handler;
import a.protocol.channel.AChannelDuplexHandler;
import a.protocol.channel.NettyChannel;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PacketInbound {

    final Handler handler;
    private final NettyChannel channel;
    private final CustomPayload payload;

    PacketInbound(NettyChannel channel, Handler handler, CustomPayload payload) {
        this.channel = channel;
        this.handler = handler;
        this.payload = payload;
    }

    /*@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }*/

    public Object getNative() {
        return new io.netty.channel.ChannelDuplexHandler() {
            @Override
            public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object packet) throws Exception {
                if (!payload.handlePacket(handler, channel, packet)) {
                    super.channelRead(ctx, packet);
                }
            }
        };
    }

    public Object getProxy() throws Exception {

        AChannelDuplexHandler proxy = new AChannelDuplexHandler() {
            @Override
            public boolean channelRead(Object packet) {
                return payload.handlePacket(handler, channel, packet);
            }
        };
        
        return proxy.inject(channel.getHandle());
    }
}