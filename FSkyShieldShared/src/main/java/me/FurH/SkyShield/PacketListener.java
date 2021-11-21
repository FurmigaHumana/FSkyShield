/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield;

import me.FurH.NIO.packet.IPacketListener;
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
import me.FurH.SkyShield.packets.scan.Packet43BigScanComplete;
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
 * @author lgpse
 */
public interface PacketListener extends IPacketListener {
    
    public void disconnected();

    public void handshake(Packet0Handshake packet);

    public void outdated(Packet1Outdated packet);

    public void ready(Packet2ClientReady packet);

    public void scan(Packet3ScanUser packet);

    public void ping(Packet16Ping packet);

    public void pong(Packet17Pong packet);

    public void checkSession(Packet18CheckSession packet);

    public void scanResult(Packet20ScanResult packet);

    public void scanRequest(Shield22RequestScan packet);

    public void scanData(Shield23ScanResult packet);

    public void invalidate(Shield24Invalidate packet);

    public void clientip(Packet30ClientAddress packet);

    public void printRequest(API31PrintScreen packet);

    public void printResult(API32PrintResult packet);

    public void requestResult(Shield33RequestResult packet);

    public void nameList(Packet34NameList packet);

    public void agentData(Packet35AgentData packet);

    public void scanHeader(Packet36ScanHeader packet);

    public void scanHashes(Packet37JarHash packet);
    
    public void scanCompleted(Packet38ScanComplete packet);

    public void agentHash(Packet39AgentHash packet);

    public void accessDenied(Packet40AccessDenied packet);

    public void classLoad(Packet41ClassLoad packet);

    public void pidOff(Packet42PidOff packet);

    public void bigComplete(Packet43BigScanComplete packet);

    public void scanHashes2(Packet45JarHash2 packet);

    public void scanHeader2(Packet46ScanHeader2 packet);

    public void offerBigData(Packet47OfferBigData packet);

    public void bigResult(Packet48BigResult packet);

    public void bigStore(Packet49BigStore packet);

    public void setCmdLine(Packet50ScanCmdLine packet);

    public void scanCompleted2(Packet51ScanComplete2 packet);

    public void classList(Packet52ClassList packet);

    public void offerFile(Packet53OfferFile packet);

    public void fileResult(Packet54FileResult packet);

    public void storeFile(Packet55StoreFile packet);

    public void requestSources(Packet56RequestSources packet);

    public void sourceResult(Packet57SourceResult packet);

    public void clientUpdate(Packet58ClientUpdate packet);
    
    public void namesDown(API59NamesDown packet);

    public void resultData(Packet60ResultData packet);

    public void resultError(Packet61ResultError packet);

    public void attachTo(Packet62AttachTo packet);

    public void resultCode(Packet63ResultCode packet);

    public void unlockPlayer(Shield64UnlockPlayer packet);

    public void payloadData(Shield65PayloadData packet);

    public void payloadCmd(Shield66PayloadCmd packet);

    public void fileList(Packet67FileList packet);

    public void messageUser(Shield68MessageUser packet);

    public void askCertificate(Packet69AskCertificate packet);

    public void fileCertificate(Packet70Certificate packet);

    public void reqScreenShot(Shield71ScreenShot packet);

    public void screenResult(Shield72ScreenResult packet);

    public void screenData(Packet72ScreenData packet);

    public void clientPayload(Packet73ClientPayload packet);

    public void postload(Packet74Postload packet);

    public void sleep(Packet75Sleep packet);

    public void nickOpen(Packet77NickOpen packet);

    public void addNick(Packet79AddNick packet);

    public void idList(Packet78IdList packet);
    
}