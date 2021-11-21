package a;

import a.protocol.channel.AByteBuf;
import a.protocol.channel.NettyChannel;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface Handler {

    public void payload(NettyChannel channel, String tag, AByteBuf byteBuf);

    public void ready();

    public void failed();

    public void disconnected();

}