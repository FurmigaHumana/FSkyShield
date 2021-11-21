package me.FurH.Setup.streams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.util.Utils;
import me.FurH.Setup.gui.MainGui;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class TrackedInputStream extends FileInputStream {

    private int perc = 0;
    private int read = 0;

    private final String totalstr;
    private final long total;
    private final MainGui gui;

    public TrackedInputStream(File file, MainGui gui) throws FileNotFoundException {
        super(file);

        this.gui = gui;
        this.total = file.length();
        this.totalstr = Utils.getFormatedBytes(total);
    }
    
    public void update() {
                
        int newperc = NumberUtils.getWorkDoneLong(read, total);
        
        if (newperc == perc) {
            return;
        }

        perc = newperc;
        gui.bar(Utils.getFormatedBytes(read) + " / " + totalstr + " - " + newperc + "%", perc);
    }

    @Override
    public int read() throws IOException {
       
        read++;
        update();
        
        return super.read();
    }
    
    @Override
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        
        int r = super.read(b, off, len);
        read += r;
        
        update();
        
        return r;
    }
}