package me.FurH.ShieldServer.server;

import java.io.IOException;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.server.Server;
import me.FurH.NIO.sockets.ISocket;
import me.FurH.ShieldServer.MainServer;
import me.FurH.ShieldServer.listener.PacketLoader;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ShieldServer extends Server<ShieldClient, ShieldServer> {
    
    public final MainServer main;

    public ShieldServer(MainServer main) throws Exception {
        super(PacketLoader.loadPackets(), 10);
        this.main = main;
    }

    public void initialize(String ip) throws IOException {
        super.initialize(ip, -1);
        super.initialize(ip, -1);
        super.initialize(ip, -1);
        super.initialize(ip, -1);
    }

    @Override
    public ShieldServer getServer() {
        return this;
    }

    @Override
    public ShieldClient newClientConnection(ISocket client_socket, ShieldServer server, String address, int port) {
        return new ShieldClient(main, client_socket, server, address, port);
    }

    @Override
    public void connected(ShieldClient conn) {
        
    }

    @Override
    public void down(ShieldClient client) {
        client.disconnected();
    }

    public void writeHandler(Packet packet) {
        for (ShieldClient conn : connections()) {
            if (conn.isHandler()) {
                conn.write(packet);
            }
        }
    }
}