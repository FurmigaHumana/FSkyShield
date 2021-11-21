/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.connect;

/**
 *
 * @author lgpse
 */
public enum SocketType {
    
    ASYNC,
    SYNC,
    LOCKASYNC,
    LOCKSYNC;
    
    public boolean isAsync() {
        return this == ASYNC || this == LOCKASYNC;
    }

    SocketType lock() {

        if (this == ASYNC) {
            System.err.println("Async socket adapter is fully operational");
            return LOCKASYNC;
        }

        if (this == SYNC) {
            System.err.println("Lock on sync legacy socket adapter");
            return LOCKSYNC;
        }

        return this;
    }
}
