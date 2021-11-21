/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.listener;

import java.util.ArrayList;
import java.util.UUID;
import me.FurH.Core.util.Callback;
import static me.FurH.ShieldServer.listener.AbstractClientListener.getClientFor;
import static me.FurH.ShieldServer.listener.AbstractClientListener.getScanning;
import me.FurH.ShieldServer.newlistener.NewClientListener;
import me.FurH.ShieldServer.server.ShieldClient;
import me.FurH.SkyShield.PacketListener;
import me.FurH.SkyShield.handler.Shield22RequestScan;
import me.FurH.SkyShield.handler.Shield23ScanResult;
import me.FurH.SkyShield.handler.Shield24Invalidate;
import me.FurH.SkyShield.handler.Shield33RequestResult;
import me.FurH.SkyShield.handler.Shield64UnlockPlayer;
import me.FurH.SkyShield.handler.Shield65PayloadData;
import me.FurH.SkyShield.packets.Packet0Handshake;
import me.FurH.SkyShield.packets.Packet2ClientReady;
import me.FurH.SkyShield.packets.scan.Packet3ScanUser;
import me.FurH.SkyShield.shot.Shield71ScreenShot;
import me.FurH.SkyShield.shot.Shield72ScreenResult;

/**
 *
 * @author lgpse
 */
public class HandlerListener extends HandshakeListener {
    
    public HandlerListener(HandshakeListener base) {
        super(base);
    }
    
    @Override
    public void disconnected() {
        super.disconnected();
        super.client.info("Handler disconnected");
    }

    @Override
    public void payloadData(Shield65PayloadData packet) {

        AbstractClientListener clientlistener = AbstractClientListener.getScanning(packet.uniqueId);

        if (clientlistener == null) {
            clientlistener = AbstractClientListener.getClientFor(packet.uniqueId);
        }

        if (clientlistener == null) {
            client.error("No current listener for " + packet.uniqueId);
            return;
        }

        clientlistener.payloadData(packet);
    }

    @Override
    public void handshake(Packet0Handshake packet) {

        try {
            client.write(new Packet2ClientReady());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void scanRequest(Shield22RequestScan packet) {

        invalidate(packet.uuid);

        Packet3ScanUser scan = new Packet3ScanUser();

        scan.uniqueId = packet.uuid;
        scan.username = packet.username;
        scan.database = new String[ 0 ];
        scan.remortport = new Integer[]{ packet.remortport };

        Shield33RequestResult result = new Shield33RequestResult();

        result.writen = 0;
        result.uniqueId = scan.uniqueId;
                
        AbstractClientListener uniqueip = findUniqueByIP(packet.address);
        result.unlock = true;
        
        if (uniqueip != null) {

            result.unlock = write(uniqueip, packet, scan, result, true);
            
        } else {

            ArrayList<AbstractClientListener> listeners = findByUserId(packet.userId);
            if (listeners != null) {
                for (AbstractClientListener listener : listeners) {
                    if (!write(listener, packet, scan, result, listeners.size() == 1)) {
                        result.unlock = false;
                    }
                }
            }
        }
        
        client.write(result);
    }
    
    private boolean write(AbstractClientListener listener, Shield22RequestScan packet, Packet3ScanUser scan, Shield33RequestResult result, boolean unique) {
        
        boolean unlock = true;
        
        if (listener.buildnumber > 1020) {
            
            unlock = false;
            
        } else {
            
            listener.callback = new Callback<Shield23ScanResult>() {
                @Override
                public void invoke(Shield23ScanResult result) {
                    try {
                        client.write(result);
                    } catch (Throwable ex) {
                    }
                }
            };
        }

        listener.scanRequest(packet, scan, this, unique);
        result.writen++;
        
        return unlock;
    }
    
    private AbstractClientListener findUniqueByIP(String addr) {
        
        AbstractClientListener hit = null;
        
        for (ShieldClient online : main.server.connections()) {

            if (!online.isClient() || !online.host.equals(addr)) {
                continue;
            }

            PacketListener tlist = online.getPacketListener();

            if (!(tlist instanceof AbstractClientListener)) {
                continue;
            }

            AbstractClientListener listener = (AbstractClientListener) tlist;

            if (hit != null) {
                return null;
            }

            hit = listener;
        }
        
        return hit;
    }
    
    private ArrayList<AbstractClientListener> findByUserId(int userId) {
        
        ArrayList<AbstractClientListener> ret = null;
        
        for (ShieldClient online : main.server.connections()) {

            if (!online.isClient()) {
                continue;
            }

            PacketListener tlist = online.getPacketListener();

            if (!(tlist instanceof NewClientListener)) {
                continue;
            }

            NewClientListener listener = (NewClientListener) tlist;

            if (listener.hasUserId(userId)) {
                
                if (ret == null) {
                    ret = new ArrayList<>();
                }

                ret.add(listener);
            }
        }
        
        return ret;
    }
    
    public void unlockPlayer(UUID uniqueId) {
        Shield64UnlockPlayer packet = new Shield64UnlockPlayer();
        packet.uniqueId = uniqueId;
        client.write(packet);
    }
    
    @Override
    public void scanData(Shield23ScanResult packet) {
       
        AbstractClientListener listener = AbstractClientListener.getScanning(packet.uniqueId);

        if (listener == null) {
            client.error("No scanning data for " + packet.uniqueId);
            return;
        }

        listener.authenticate(packet);
    }

    @Override
    public void invalidate(Shield24Invalidate packet) {
        invalidate(packet.uniqueId);
    }
    
    public void invalidate(UUID uniqueId) {
        
        AbstractClientListener listener = getClientFor(uniqueId);

        if (listener == null) {
            listener = getScanning(uniqueId);
        }
        
        if (listener == null) {
            return;
        }

        listener.userQuit();
    }

    @Override
    public void reqScreenShot(Shield71ScreenShot packet) {

        AbstractClientListener listener = AbstractClientListener.getClientFor(packet.targetuuid);

        if (listener == null) {
            baseShotResult(packet, "Este jogador aparenta não estar utilizando o SkyShield");
            return;
        }

        baseShotResult(packet, "Cliente encontrado, aguardando resultado...");
        listener.printRequest(packet, this);
    }
    
    private void baseShotResult(Shield71ScreenShot packet, String message) {
        
        if (packet.requester == null) {
            return;
        }
        
        Shield72ScreenResult result = new Shield72ScreenResult();
        
        result.requester = packet.requester;
        result.error = true;
        result.message = message;
        
        this.client.write(result);
    }
}