package me.FurH.ShieldServer.newlistener;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public enum FileType {
    JAVALIB, AGENT, GAMEFILE, UNTESTED;

    boolean needCert() {
        return this == JAVALIB || this == AGENT;
    }
}