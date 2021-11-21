package me.FurH.SkyShield.connect;

import me.FurH.NIO.connection.Connection;
import me.FurH.SkyShield.PacketListener;
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

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ExcludeListener implements PacketListener {

    @Override
    public void disconnected() {
    }

    @Override
    public void handshake(Packet0Handshake ph) {
    }

    @Override
    public void outdated(Packet1Outdated po) {
    }

    @Override
    public void ready(Packet2ClientReady pcr) {
    }

    @Override
    public void scan(Packet3ScanUser psu) {
    }

    @Override
    public void ping(Packet16Ping pp) {
    }

    @Override
    public void pong(Packet17Pong pp) {
    }

    @Override
    public void checkSession(Packet18CheckSession pcs) {
    }

    @Override
    public void scanResult(Packet20ScanResult psr) {
    }

    @Override
    public void scanRequest(Shield22RequestScan srs) {
    }

    @Override
    public void scanData(Shield23ScanResult ssr) {
    }

    @Override
    public void invalidate(Shield24Invalidate si) {
    }
    
    @Override
    public void clientip(Packet30ClientAddress pca) {
    }

    @Override
    public void printRequest(API31PrintScreen apips) {
    }

    @Override
    public void printResult(API32PrintResult apipr) {
    }

    @Override
    public void requestResult(Shield33RequestResult srr) {
    }

    @Override
    public void nameList(Packet34NameList pnl) {
    }

    @Override
    public void agentData(Packet35AgentData pad) {
    }

    @Override
    public void scanHeader(Packet36ScanHeader psh) {
    }

    @Override
    public void scanHashes(Packet37JarHash pjh) {
    }

    @Override
    public void scanCompleted(Packet38ScanComplete psc) {
    }

    @Override
    public void agentHash(Packet39AgentHash pah) {
    }

    @Override
    public void accessDenied(Packet40AccessDenied pad) {
    }

    @Override
    public void classLoad(Packet41ClassLoad pcl) {
    }

    @Override
    public void pidOff(Packet42PidOff ppo) {
    }

    @Override
    public void bigComplete(Packet43BigScanComplete pbsc) {
    }

    @Override
    public void scanHashes2(Packet45JarHash2 pjh) {
    }

    @Override
    public void scanHeader2(Packet46ScanHeader2 psh) {
    }

    @Override
    public void offerBigData(Packet47OfferBigData pobd) {
    }

    @Override
    public void bigResult(Packet48BigResult pbr) {
    }

    @Override
    public void bigStore(Packet49BigStore pbs) {
    }

    @Override
    public void setCmdLine(Packet50ScanCmdLine pscl) {
    }

    @Override
    public void scanCompleted2(Packet51ScanComplete2 psc) {
    }

    @Override
    public void classList(Packet52ClassList pcl) {
    }

    @Override
    public void offerFile(Packet53OfferFile pof) {
    }

    @Override
    public void fileResult(Packet54FileResult pfr) {
    }

    @Override
    public void storeFile(Packet55StoreFile psf) {
    }

    @Override
    public void requestSources(Packet56RequestSources prs) {
    }

    @Override
    public void sourceResult(Packet57SourceResult psr) {
    }

    @Override
    public void clientUpdate(Packet58ClientUpdate pcu) {
    }

    @Override
    public void namesDown(API59NamesDown apind) {
    }

    @Override
    public void resultData(Packet60ResultData prd) {
    }

    @Override
    public void resultError(Packet61ResultError pre) {
    }

    @Override
    public void attachTo(Packet62AttachTo pat) {
    }

    @Override
    public void resultCode(Packet63ResultCode prc) {
    }

    @Override
    public void unlockPlayer(Shield64UnlockPlayer sup) {
    }

    @Override
    public void payloadData(Shield65PayloadData spd) {
    }

    @Override
    public void payloadCmd(Shield66PayloadCmd spc) {
    }

    @Override
    public void fileList(Packet67FileList pfl) {
    }

    @Override
    public void messageUser(Shield68MessageUser smu) {
    }

    @Override
    public void askCertificate(Packet69AskCertificate pac) {
    }

    @Override
    public void fileCertificate(Packet70Certificate pc) {
    }

    @Override
    public void reqScreenShot(Shield71ScreenShot sss) {
    }

    @Override
    public void screenResult(Shield72ScreenResult ssr) {
    }

    @Override
    public void screenData(Packet72ScreenData psd) {
    }

    @Override
    public <T extends Connection> T getOwner() {
        return null;
    }

    @Override
    public void clientPayload(Packet73ClientPayload pcp) {
    }

    @Override
    public void postload(Packet74Postload pp) {
    }

    @Override
    public void sleep(Packet75Sleep ps) {
    }

    @Override
    public void nickOpen(Packet77NickOpen pno) {
    }

    @Override
    public void addNick(Packet79AddNick pan) {
    }

    @Override
    public void idList(Packet78IdList pil) {
    }
}