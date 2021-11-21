/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.scans;

/**
 *
 * @author lgpse
 */
public class ScanPid {

    public final int pid;
    public final int scan;
        
    public ScanPid(int pid, int scan) {
        this.pid = pid;
        this.scan = scan;
    }
}