/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.encoder;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import me.FurH.Core.util.Utils;

/**
 *
 * @author lgpse
 */
public class Compressor {
    
    private static final ThreadLocal<StrBuffers> cache;

    static {
        cache = new ThreadLocal<StrBuffers>() {
            @Override
            public StrBuffers initialValue() {
                return new StrBuffers();
            }
        };
    }
    
    public static StrBuffers getLocal() {
        return cache.get();
    }
    
    public static String toString(byte[] input) throws DataFormatException {

        StrBuffers decompressor = cache.get();
        decompressor.reset();

        ByteArrayOutputStream baos = decompressor.baos;
        Inflater inf = decompressor.inf;
        
        if (inf == null) {
            inf = new Inflater();
            decompressor.inf = inf;
        }
        
        try {
            
            inf.setInput(input);
            byte[] buffer = decompressor.buffer;

            while (!inf.finished()) {
                int count = inf.inflate(buffer);
                baos.write(buffer, 0, count);
            }
            
            return new String(baos.toByteArray(), Utils.UTF8);
            
        } finally {

            inf.reset();

        }
    }
    
    public static byte[] encode(String input) {
        
        StrBuffers compressor = cache.get();
        compressor.reset();

        try {

            Deflater def = compressor.def;        
            byte[] buffer = compressor.buffer;

            def.setInput(input.getBytes(Utils.UTF8));
            def.finish();

            while (!def.finished()) {
                compressor.baos.write(buffer, 0, def.deflate(buffer));
            }

            return compressor.baos.toByteArray();

        } finally {

            compressor.reset();

        }
    }
}
