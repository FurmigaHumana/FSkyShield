/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.server;

import me.FurH.NIO.server.data.ClientConnection;
import me.FurH.NIO.sockets.ISocket;
import me.FurH.ShieldServer.MainServer;
import me.FurH.ShieldServer.listener.HandshakeListener;
import me.FurH.SkyShield.PacketListener;

/**
 *
 * @author lgpse
 */
public class ShieldClient extends ClientConnection<ShieldServer> {
   
    private ClientType clientype = ClientType.LOADING;
    private PacketListener packetlistener;
    
    public ShieldClient(MainServer main, ISocket socket, ShieldServer server, String host, int port) {
        super(socket, server, host, port);
        this.packetlistener = new HandshakeListener(main, server, this);
    }
    
    public boolean isClient() {
        return clientype == ClientType.CLIENT;
    }

    public boolean isHandler() {
        return clientype == ClientType.HANDLER && listener.port == 2020;
    }

    public void setClientType(boolean handler) {
        clientype = handler ? ClientType.HANDLER : ClientType.CLIENT;
    }
    
    @Override
    public PacketListener getPacketListener() {
        return packetlistener;
    }

    public void setPacketListener(PacketListener listener) {
        this.packetlistener = listener;
    }

    @Override
    public void disconnected() {
        packetlistener.disconnected();
    }
}