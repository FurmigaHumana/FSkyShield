/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.connect;

import java.net.InetSocketAddress;
import me.FurH.NIO.client.ServerConnection;
import me.FurH.NIO.executor.AbstractExecutor;
import me.FurH.NIO.sockets.ISocket;
import me.FurH.SkyShield.PacketListener;
import me.FurH.SkyShield.ShieldClient;

/**
 *
 * @author lgpse
 */
public class ConnectToServer extends ServerConnection {

    private final DefaultListener packetlistener;
    
    private SocketType stype = SocketType.ASYNC;
    private boolean firstconn = true;
    private int asynctries = 0;
    private final ShieldClient shield;

    public ConnectToServer(String host, ShieldClient shield) throws Exception {

        super(host, ShieldClient.port, true);

        super.enableCompression();

        this.packetlistener = new DefaultListener(shield);
        this.setPacketMap(packetlistener.loadPackets());

        this.shield = shield;
    }
    
    @Override
    public void disconnected() {
        super.disconnected();
        packetlistener.disconnected();
        shield.game_scanner.terminateScan();
    }

    @Override
    protected ISocket newSocket(InetSocketAddress addr, AbstractExecutor executor) throws Exception {
        if (stype.isAsync()) {
            return super.newSocket(addr, executor);
        } else {
            return new LegacySocket(addr, executor);
        }
    }

    @Override
    protected boolean connect0() throws Exception {
        
        boolean ret = false;
        
        try {

            ret = super.connect0();

        } finally {

            if (firstconn && !ret) {
                
                if (!stype.isAsync()) {

                    System.err.println("Socket adapter failure, locking on async");
                    stype = SocketType.LOCKASYNC;

                } else {

                    if (asynctries < 2) {
                        asynctries++;
                    } else {
                        stype = SocketType.SYNC;
                    }
                }
            }
        }

        if (ret) {
            stype = stype.lock();
            firstconn = false;
        }

        return ret;
    }
    
    @Override
    public PacketListener getPacketListener() {
        return packetlistener;
    }

    @Override
    public void connected() {
        
        super.connected();
        
        try {
            packetlistener.connected();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}