/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.connect;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import java.util.concurrent.TimeUnit;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.executors.TimerExecutor;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.time.TimeUtils;
import me.FurH.Core.util.Utils;
import me.FurH.JavaPacker.loader.ErrorGui;
import me.FurH.NIO.connection.IPacketMap;
import me.FurH.NIO.connection.SPacketMap;
import me.FurH.SkyShield.ShieldClient;
import me.FurH.SkyShield.gui.DiagnosticGui;
import me.FurH.SkyShield.idle.IdleDetector;
import me.FurH.SkyShield.packets.Packet0Handshake;
import me.FurH.SkyShield.packets.Packet18CheckSession;
import me.FurH.SkyShield.packets.Packet1Outdated;
import me.FurH.SkyShield.packets.Packet2ClientReady;
import me.FurH.SkyShield.packets.newscan.Packet61ResultError;
import me.FurH.SkyShield.packets.newscan.Packet62AttachTo;
import me.FurH.SkyShield.packets.newscan.Packet69AskCertificate;
import me.FurH.SkyShield.packets.nicks.Packet77NickOpen;
import me.FurH.SkyShield.packets.nicks.Packet79AddNick;
import me.FurH.SkyShield.packets.ping.Packet16Ping;
import me.FurH.SkyShield.packets.ping.Packet17Pong;
import me.FurH.SkyShield.packets.ping.Packet30ClientAddress;
import me.FurH.SkyShield.packets.scan.Packet3ScanUser;
import me.FurH.SkyShield.packets.setup.Packet35AgentData;
import me.FurH.SkyShield.packets.update.Packet58ClientUpdate;
import me.FurH.SkyShield.resources.Resources;

/**
 *
 * @author lgpse
 */
public class DefaultListener extends ExcludeListener {
    
    private final ShieldClient shield;
    private long lastgc = 0;

    public DefaultListener(ShieldClient shield) {
        this.shield = shield;
    }
    
    IPacketMap loadPackets() {
       
        SPacketMap map = new SPacketMap();
       
        map.loadClassCache(Packet1Outdated::new,
                Packet2ClientReady::new,
                Packet3ScanUser::new,
                Packet16Ping::new,
                Packet17Pong::new,
                Packet18CheckSession::new,
                Packet30ClientAddress::new,
                Packet35AgentData::new,
                Packet58ClientUpdate::new,
                Packet61ResultError::new,
                Packet62AttachTo::new,
                Packet69AskCertificate::new,
                Packet77NickOpen::new,
                Packet79AddNick::new
        );
        
        return map;
    }
    
    @Override
    public ConnectToServer getOwner() {
        return shield.connection;
    }

    public void connected() throws Exception {
                    
        shield.tray_gui.setToolTip("Identificando...");

        File client = new File("client.jar");

        Packet0Handshake packet = new Packet0Handshake();

        packet.handler = false;
        packet.version = shield.main_gui.version;
        packet.buildnumber = shield.main_gui.buildnumber;
        packet.uniqueId = ShieldClient.uniqueId;
        packet.clienthash = Encrypto.hash("MD5", client);
        packet.agenthash = Resources.agentHash();
        packet.fullhash = ShieldClient.clienthash;
        packet.filename = shield.getMainFile();

        shield.connection.write(packet);

        shield.main_gui.nickList.sendUpdate(null);
        
        TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                if (!IdleDetector.isIdle() && TimeUtils.isExpired(lastgc, 1, TimeUnit.MINUTES)) {
                    lastgc = Utils.currentTimeMillis();
                    System.gc();
                }
            }
        }, 10, TimeUnit.SECONDS);
        
        shield.main_gui.connected();
    }

    @Override
    public void outdated(Packet1Outdated packet) {
        if (!shield.main_gui.nickList.has(1)) {
            ErrorGui.errorlines("Seu SkyShield esta desatualizado!", false, true);
        }
    }

    @Override
    public void ready(Packet2ClientReady packet) {
        shield.tray_gui.setToolTip("Conectado");
        shield.main_gui.readOnlineMethod(false);
    }
    
    @Override
    public void disconnected() {
        shield.tray_gui.setToolTip("Conectando...");
        shield.main_gui.readOnlineMethod(false);
    }

    @Override
    public void scan(Packet3ScanUser request) {
        shield.game_scanner.scanAllProcess(request);
    }

    @Override
    public void ping(Packet16Ping packet) {
        Packet17Pong pong = new Packet17Pong();
        pong.created = packet.created;
        shield.connection.write(pong);
    }

    @Override
    public void pong(Packet17Pong packet) {
        DiagnosticGui.log("pong is " + (System.currentTimeMillis() - packet.created) + " ms");
    }

    @Override
    public void checkSession(Packet18CheckSession packet) {
        ShieldClient.checkSession();
    }

    @Override
    public void clientip(Packet30ClientAddress packet) {
    }

    @Override
    public void agentData(Packet35AgentData packet) {
        Resources.agentData(packet);
    }

    @Override
    public void clientUpdate(Packet58ClientUpdate packet) {

        File dest = new File("client.jar");
        
        try {
            
            Path path = dest.toPath();
            
            Files.readAttributes(path, DosFileAttributes.class);
            Files.setAttribute(path, "dos:hidden", false);
            
            FileUtils.setBytesOfFile(dest, packet.data);

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
        try {
            FileUtils.setBytesOfFile(dest, packet.data);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
        System.exit(0);
    }

    @Override
    public void resultError(Packet61ResultError packet) {
        shield.game_scanner.resultError(packet);
    }

    @Override
    public void attachTo(Packet62AttachTo packet) {
        shield.game_scanner.attachTo(packet);
    }

    @Override
    public void askCertificate(Packet69AskCertificate packet) {
        shield.game_scanner.askCertificate(packet);
    }
    
    @Override
    public void nickOpen(Packet77NickOpen packet) {
        shield.main_gui.nickList.nickOpen(packet);
    }
    
    @Override
    public void addNick(Packet79AddNick packet) {
        shield.main_gui.nickList.addNick(packet);
    }
}