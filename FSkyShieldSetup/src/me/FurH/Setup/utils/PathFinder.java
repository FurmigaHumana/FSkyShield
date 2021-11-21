package me.FurH.Setup.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import me.FurH.Core.close.Closer;
import me.FurH.Core.file.FileUtils;
import me.FurH.Setup.Native.SetupNative;
import me.FurH.Setup.registry.WinRegistry;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PathFinder {

    public static final HashMap<String, Integer> codes;
    
    static {
        codes = new HashMap<String, Integer>();
        codes.put("desktop", 0x0000);
        codes.put("startup", 0x0007);
        codes.put("Local AppData", 0x001c);
        codes.put("programs", 0x0002);
    }
    
    public static String getSpecialFolder(String folder) {
        
        String ret = getSpecialFolder0(folder);
        System.out.println(folder + " -> " + ret);
        
        return ret;
    }
    
    private static String getSpecialFolder0(String folder) {

        try {
            
            System.out.println("Trying native path...");

            int code = codes.get(folder);
            SetupNative nat = SetupNative.getInstance();

            if (nat != null) {
                return nat.getFolder(code);
            }
            
        } catch (Throwable ex) {
            
            ex.printStackTrace();
            
        }
        
        System.out.println("Trying registry...");
        
        try {
            return WinRegistry.valueForKey(WinRegistry.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", folder);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
        System.out.println("Trying vbs...");
        
        try {
            return getVBSFolder(folder);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    private static String getVBSFolder(String folder) throws IOException {
       
        InputStreamReader isr = null;
        BufferedReader input = null;
        InputStream is = null;
        String result = null;
        File file = null;
        
        try {
            
            file = File.createTempFile("tempinstall", ".vbs");

            String vbs = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n"
                    + "wscript.echo WshShell.SpecialFolders(\"" + folder + "\")";

            FileUtils.setLineOfFile(file, vbs);

            Process p = Runtime.getRuntime().exec("cscript //NoLogo \"" + file.getAbsolutePath() + "\"");

            is = p.getInputStream();
            isr = new InputStreamReader(is);
            input = new BufferedReader(isr);
            
            result = input.readLine();
            input.close();
                        
        } finally {

            Closer.closeQuietly(input);
            Closer.closeQuietly(isr);
            Closer.closeQuietly(is);

            if (file != null && !file.delete()) {
                file.deleteOnExit();
            }
        }
        
        return result;
    }
}