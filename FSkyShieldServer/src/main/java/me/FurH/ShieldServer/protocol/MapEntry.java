package me.FurH.ShieldServer.protocol;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class MapEntry {

    public final String label;
    public final MapList list;

    MapEntry(String label, MapList list) {
        this.label = label;
        this.list = list;
    }

}