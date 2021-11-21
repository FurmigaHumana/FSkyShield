package me.FurH.Setup.streams;

import java.io.IOException;
import java.io.OutputStream;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class PositionOutputStream extends OutputStream {
    
    private final OutputStream out;
    private int position = 0;

    public PositionOutputStream(OutputStream out) {
        this.out = out;
    }

    public int getPosition() {
        return position;
    }
    
    @Override
    public void write(int b) throws IOException {
        position++;
        out.write(b);
    }
    
    @Override
    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        position += (len - off);
        out.write(b, off, len);
    }
}