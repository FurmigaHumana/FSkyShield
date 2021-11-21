package me.FurH.SkyShield.handler;

import me.FurH.SkyShield.PacketListener;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Shield66PayloadCmd extends Shield65PayloadData {

    @Override
    public short getId() {
        return 66;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.payloadCmd(this);
    }
}