package me.FurH.ShieldServer.newlistener;

import java.io.DataOutputStream;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface CallbackWritter {

    public void write(DataOutputStream dos) throws Throwable;
    

}