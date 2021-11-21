package me.FurH.ShieldServer.newlistener;

import me.FurH.ShieldServer.listener.AbstractClientListener;
import me.FurH.ShieldServer.listener.HandshakeListener;
import me.FurH.SkyShield.packets.big.Packet47OfferBigData;
import me.FurH.SkyShield.packets.big.Packet49BigStore;
import me.FurH.SkyShield.packets.cls.Packet57SourceResult;
import me.FurH.SkyShield.packets.scan.Packet36ScanHeader;
import me.FurH.SkyShield.packets.scan.Packet37JarHash;
import me.FurH.SkyShield.packets.scan.Packet38ScanComplete;
import me.FurH.SkyShield.packets.scan.Packet39AgentHash;
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
import me.FurH.SkyShield.packets.scan2.Packet55StoreFile;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public abstract class ExcludeClientlistener extends AbstractClientListener {
    
    public ExcludeClientlistener(HandshakeListener base) {
        super(base);
    }
        
    @Override
    public void scanHeader(Packet36ScanHeader packet) {
    }
    
    @Override
    public void scanHashes(Packet37JarHash packet) {
    }
    
    @Override
    public void scanHashes2(Packet45JarHash2 packet) {
    }
    
    @Override
    public void bigComplete(Packet43BigScanComplete packet) {
    }

    @Override
    public void scanCompleted(Packet38ScanComplete packet) {
    }
    
    @Override
    public void agentHash(Packet39AgentHash packet) {
    }
    
    @Override
    public void accessDenied(Packet40AccessDenied packet) {
    }
    
    @Override
    public void classLoad(Packet41ClassLoad packet) {
    }
    
    @Override
    public void pidOff(Packet42PidOff packet) {
    }
    
    @Override
    public void scanHeader2(Packet46ScanHeader2 packet) {
    }
    
    @Override
    public void offerBigData(Packet47OfferBigData packet) {
    }
    
    @Override
    public void bigStore(Packet49BigStore packet) {
    }
    
    @Override
    public void setCmdLine(Packet50ScanCmdLine packet) {
    }
    
    @Override
    public void scanCompleted2(Packet51ScanComplete2 packet) {
    }
    
    @Override
    public void classList(Packet52ClassList packet) {
    }
    
    @Override
    public void offerFile(Packet53OfferFile packet) {
    }
    
    @Override
    public void storeFile(Packet55StoreFile packet) {
    }
    
    @Override
    public void sourceResult(Packet57SourceResult packet) {
    }
}