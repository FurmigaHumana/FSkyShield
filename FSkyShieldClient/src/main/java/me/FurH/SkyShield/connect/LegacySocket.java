/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.connect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import me.FurH.NIO.buffer.BufferHolder;
import me.FurH.NIO.executor.AbstractExecutor;
import me.FurH.NIO.sockets.ISocket;

/**
 *
 * @author lgpse
 */
public class LegacySocket implements ISocket {
    
    private final AbstractExecutor executor;
    private Socket socket;

    private ReadableByteChannel rchan;
    private WritableByteChannel wchan;
    
    public LegacySocket(InetSocketAddress addr, AbstractExecutor executor) throws IOException {

        this.executor = executor;
        socket = new Socket();
        
        socket.connect(addr, 30000);

        if (!socket.isConnected() || socket.isClosed()) {
            throw new IOException("Connection failed");
        }

        socket.setReuseAddress(true);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
    }

    @Override
    public String name() {
        return "Legacy Socket";
    }

    @Override
    public void read(BufferHolder buffer, long timeout, CompletionHandler<Integer, BufferHolder> handler) {
        async(buffer, handler, true);
    }

    @Override
    public void write(BufferHolder buffer, CompletionHandler<Integer, BufferHolder> handler) {
        async(buffer, handler, false);
    }

    private void async(BufferHolder buffer, CompletionHandler<Integer, BufferHolder> handler, boolean read) {
        
        if (socket == null) {
            return;
        }
        
        executor.execute(new Runnable() {
            @Override
            public void run() {
                process(buffer, handler, read);
            }
        });
    }
    
    private void process(BufferHolder buffer, CompletionHandler<Integer, BufferHolder> handler, boolean read) {
        
        if (socket == null) {
            return;
        }
        
        int bytes = -1;
        
        try {

            if (read) {

                if (rchan == null) {
                    rchan = Channels.newChannel(socket.getInputStream());
                }

                bytes = rchan.read(buffer.getBuffer());
                
            } else {

                if (wchan == null) {
                    wchan = Channels.newChannel(socket.getOutputStream());
                }

                bytes = wchan.write(buffer.getBuffer());
            }

        } catch (Throwable ex) {

            handler.failed(ex, buffer);

        } finally {
            
            handler.completed(bytes, buffer);
            
        }
    }

    @Override
    public void close() {
        
        try {
            socket.close();
        } catch (Throwable ex) {
        }
        
        socket = null;
        
        try {
            rchan.close();
        } catch (Throwable ex) {
        }
        
        try {
            wchan.close();
        } catch (Throwable ex) {
        }
    }
}