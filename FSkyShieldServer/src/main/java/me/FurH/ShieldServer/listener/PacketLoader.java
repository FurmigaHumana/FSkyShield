/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.listener;

import me.FurH.NIO.connection.SPacketMap;
import me.FurH.SkyShield.api.API31PrintScreen;
import me.FurH.SkyShield.api.API32PrintResult;
import me.FurH.SkyShield.api.API59NamesDown;
import me.FurH.SkyShield.handler.Shield22RequestScan;
import me.FurH.SkyShield.handler.Shield23ScanResult;
import me.FurH.SkyShield.handler.Shield24Invalidate;
import me.FurH.SkyShield.handler.Shield33RequestResult;
import me.FurH.SkyShield.handler.Shield64UnlockPlayer;
import me.FurH.SkyShield.handler.Shield65PayloadData;
import me.FurH.SkyShield.handler.Shield66PayloadCmd;
import me.FurH.SkyShield.handler.Shield68MessageUser;
import me.FurH.SkyShield.packets.Packet0Handshake;
import me.FurH.SkyShield.packets.Packet18CheckSession;
import me.FurH.SkyShield.packets.Packet1Outdated;
import me.FurH.SkyShield.packets.Packet21LoginDenied;
import me.FurH.SkyShield.packets.Packet2ClientReady;
import me.FurH.SkyShield.packets.Packet75Sleep;
import me.FurH.SkyShield.packets.big.Packet47OfferBigData;
import me.FurH.SkyShield.packets.big.Packet48BigResult;
import me.FurH.SkyShield.packets.big.Packet49BigStore;
import me.FurH.SkyShield.packets.cls.Packet56RequestSources;
import me.FurH.SkyShield.packets.cls.Packet57SourceResult;
import me.FurH.SkyShield.packets.newscan.Packet60ResultData;
import me.FurH.SkyShield.packets.newscan.Packet61ResultError;
import me.FurH.SkyShield.packets.newscan.Packet62AttachTo;
import me.FurH.SkyShield.packets.newscan.Packet63ResultCode;
import me.FurH.SkyShield.packets.newscan.Packet67FileList;
import me.FurH.SkyShield.packets.newscan.Packet69AskCertificate;
import me.FurH.SkyShield.packets.newscan.Packet70Certificate;
import me.FurH.SkyShield.packets.newscan.Packet73ClientPayload;
import me.FurH.SkyShield.packets.newscan.Packet74Postload;
import me.FurH.SkyShield.packets.nicks.Packet79AddNick;
import me.FurH.SkyShield.packets.nicks.Packet77NickOpen;
import me.FurH.SkyShield.packets.nicks.Packet78IdList;
import me.FurH.SkyShield.packets.ping.Packet16Ping;
import me.FurH.SkyShield.packets.ping.Packet17Pong;
import me.FurH.SkyShield.packets.ping.Packet19KeepAlive;
import me.FurH.SkyShield.packets.ping.Packet30ClientAddress;
import me.FurH.SkyShield.packets.scan.Packet20ScanResult;
import me.FurH.SkyShield.packets.scan.Packet36ScanHeader;
import me.FurH.SkyShield.packets.scan.Packet37JarHash;
import me.FurH.SkyShield.packets.scan.Packet38ScanComplete;
import me.FurH.SkyShield.packets.scan.Packet39AgentHash;
import me.FurH.SkyShield.packets.scan.Packet3ScanUser;
import me.FurH.SkyShield.packets.scan.Packet40AccessDenied;
import me.FurH.SkyShield.packets.scan.Packet41ClassLoad;
import me.FurH.SkyShield.packets.scan.Packet42PidOff;
import me.FurH.SkyShield.packets.scan.Packet45JarHash2;
import me.FurH.SkyShield.packets.scan2.Packet46ScanHeader2;
import me.FurH.SkyShield.packets.scan2.Packet50ScanCmdLine;
import me.FurH.SkyShield.packets.scan2.Packet51ScanComplete2;
import me.FurH.SkyShield.packets.scan2.Packet52ClassList;
import me.FurH.SkyShield.packets.scan2.Packet53OfferFile;
import me.FurH.SkyShield.packets.scan2.Packet54FileResult;
import me.FurH.SkyShield.packets.scan2.Packet55StoreFile;
import me.FurH.SkyShield.packets.setup.Packet34NameList;
import me.FurH.SkyShield.packets.setup.Packet35AgentData;
import me.FurH.SkyShield.packets.update.Packet58ClientUpdate;
import me.FurH.SkyShield.shot.Packet72ScreenData;
import me.FurH.SkyShield.shot.Shield71ScreenShot;
import me.FurH.SkyShield.shot.Shield72ScreenResult;

/**
 *
 * @author Luis
 */
public class PacketLoader {
    
    private static final SPacketMap packetmap;
    
    static {
        packetmap = new SPacketMap();
    }
    
    public static SPacketMap loadPackets() {

        packetmap.loadClassCache(Packet0Handshake::new,
                Packet1Outdated::new,
                Packet2ClientReady::new,
                Packet3ScanUser::new,
                Packet16Ping::new,
                Packet17Pong::new,
                Packet18CheckSession::new,
                Packet19KeepAlive::new,
                Packet20ScanResult::new,
                Packet21LoginDenied::new,
                Shield22RequestScan::new,
                Shield23ScanResult::new,
                Shield24Invalidate::new,
                Packet30ClientAddress::new,
                API31PrintScreen::new,
                API32PrintResult::new,
                Shield33RequestResult::new,
                Packet34NameList::new,
                Packet35AgentData::new,
                Packet36ScanHeader::new,
                Packet37JarHash::new,
                Packet38ScanComplete::new,
                Packet39AgentHash::new,
                Packet40AccessDenied::new,
                Packet41ClassLoad::new,
                Packet42PidOff::new,
                Packet45JarHash2::new,
                Packet46ScanHeader2::new,
                Packet47OfferBigData::new,
                Packet48BigResult::new,
                Packet49BigStore::new,
                Packet50ScanCmdLine::new,
                Packet51ScanComplete2::new,
                Packet52ClassList::new,
                Packet53OfferFile::new,
                Packet54FileResult::new,
                Packet55StoreFile::new,
                Packet56RequestSources::new,
                Packet57SourceResult::new,
                Packet58ClientUpdate::new,
                API59NamesDown::new,
                Packet60ResultData::new,
                Packet61ResultError::new,
                Packet62AttachTo::new,
                Packet63ResultCode::new,
                Shield64UnlockPlayer::new,
                Shield65PayloadData::new,
                Shield66PayloadCmd::new,
                Packet67FileList::new,
                Shield68MessageUser::new,
                Packet69AskCertificate::new,
                Packet70Certificate::new,
                Shield71ScreenShot::new,
                Shield72ScreenResult::new,
                Packet72ScreenData::new,
                Packet73ClientPayload::new,
                Packet74Postload::new,
                Packet75Sleep::new,
                Packet79AddNick::new,
                Packet77NickOpen::new,
                Packet78IdList::new
        );

        return packetmap;
    }
}