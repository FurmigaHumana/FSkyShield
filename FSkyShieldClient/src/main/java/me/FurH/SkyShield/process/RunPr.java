package me.FurH.SkyShield.process;

import java.io.File;

/**
/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class RunPr {
    
    public static String[] read(String[] command, File workdir) throws Exception {
        
        ProcessBuilder builder = new ProcessBuilder();
        
        builder
                .command(command)
                .directory(workdir);
        
        Process process = builder.start();

        StreamReader reader1 = new StreamReader(process.getInputStream());
        StreamReader reader2 = new StreamReader(process.getErrorStream());

        reader1.start();
        reader2.start();

        process.waitFor();
        
        reader1.join();
        reader2.join();
        
        return new String[] { reader1.getResult(), reader2.getResult() };
    }
}
