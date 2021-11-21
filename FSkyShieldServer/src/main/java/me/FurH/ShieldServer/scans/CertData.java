package me.FurH.ShieldServer.scans;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CertData {

    final boolean signed;
    final String sigdata;

    public CertData(boolean signed, String sigdata) {
        this.signed = signed;
        this.sigdata = sigdata;
    }
    
}