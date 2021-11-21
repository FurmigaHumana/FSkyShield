/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.process;

import java.io.PrintStream;
import java.util.prefs.Preferences;

/**
 *
 * @author lgpse
 */
public class AdminCheck {
   
    public static boolean isRunningAdmin() {

        Preferences prefs = Preferences.systemRoot();
        PrintStream systemErr = System.err;

        synchronized (systemErr) {
            
            System.setErr(null);
            try {
                
                prefs.put("foo", "bar");
                prefs.remove("foo");
                prefs.flush();

                return true;
                
            } catch (Exception e) {
                
                return false;
                
            } finally {
                
                System.setErr(systemErr);
                
            }
        }
    }
}