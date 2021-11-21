/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.encoder;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 *
 * @author lgpse
 */
public class StrBuffers {

    public final ByteArrayOutputStream baos;
    public final Deflater def;
    
    final byte[] buffer;
    Inflater inf;

    StrBuffers() {

        this.baos   = new ByteArrayOutputStream() {
            @Override
            public synchronized void reset() {
                super.reset();
                if (this.buf.length > 1048576) {
                    this.buf = new byte[ 1024 ];
                }
            }
        };

        this.def    = new Deflater(9);
        this.buffer = new byte[ 8192 ];
    }

    public void reset() {
        baos.reset();
        def.reset();
        
        if (inf != null) {
            inf.reset();
        }
    }
    
}
