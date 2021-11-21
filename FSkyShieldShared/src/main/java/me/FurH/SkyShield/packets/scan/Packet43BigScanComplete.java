/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.packets.scan;

import me.FurH.SkyShield.PacketListener;

/**
 *
 * @author lgpse
 */
public class Packet43BigScanComplete extends Packet38ScanComplete {

    @Override
    public short getId() {
        return 43;
    }

    @Override
    public boolean bigBuffer() {
        return true;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.bigComplete(this);
    }
}
