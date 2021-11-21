package me.FurH.SkyShield.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import me.FurH.Core.util.Utils;

/**
 * /**
 *
 * @author FurmigaHumana All Rights Reserved unless otherwise explicitly stated.
 */
public class StreamReader extends Thread {
    
    private final InputStreamReader isr;
    private final StringBuilder sb;
    private final char[] buffer;

    public StreamReader(InputStream is) {
        this.isr    = new InputStreamReader(is, Utils.UTF8);
        this.sb     = new StringBuilder();
        this.buffer = new char[ 8192 ];
    }

    @Override
    public void run() {

        for (;;) {
            
            try {

                int rsz = isr.read(buffer, 0, buffer.length);
                if (rsz < 0) {
                    break;
                }

                sb.append(buffer, 0, rsz);
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getResult() {
        return sb.toString();
    }
}