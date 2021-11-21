package me.FurH.Setup.Native;

import java.io.File;
import java.io.InputStream;
import me.FurH.Core.close.Closer;
import me.FurH.Core.file.FileUtils;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class SetupNative {
    
    private static SetupNative instance;
    
    public static SetupNative getInstance() {
        return instance;
    }
    
    public static void load() {
        
        try {
            
            tryToLoad("x64", SetupNative.class.getResourceAsStream("/native64.dll"));
            instance = new SetupNative();
            
        } catch (Throwable ex) {
            
            ex.printStackTrace();
            
            try {
                
                tryToLoad("x32", SetupNative.class.getResourceAsStream("/native32.dll"));
                instance = new SetupNative();
                
            } catch (Throwable ex1) {

                ex1.printStackTrace();
                
            }
        }
    }
    
    private static void tryToLoad(String name, InputStream is) throws Throwable {
        
        try {
            
            File temp = File.createTempFile("nativesetup" + name, ".dll");
            temp.deleteOnExit();
            
            FileUtils.copyFile(is, temp);
            
            System.load(temp.getAbsolutePath());

        } finally {
            
            Closer.closeQuietly(is);
            
        }
    }
    
    public native String getFolder(int pathid);
    
}