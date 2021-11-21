package a.protocol.channel;

import java.io.IOException;
import java.lang.reflect.Method;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class AByteBuf {
    
    private static Method writeBytes;
    private static Method release;
    private static Method readableBytes;
    private static Method readByte;
    private static Method readBytes;
    
    private static Class<?> lastcls;
    private final Object bytebuf;

    public AByteBuf(Object bytebuf, boolean checkchange) {

        this.bytebuf = bytebuf;

        if (checkchange && (lastcls == null || lastcls != bytebuf.getClass())) {

            writeBytes      = null;
            release         = null;
            readableBytes   = null;
            readByte        = null;
            readBytes       = null;

            lastcls         = bytebuf.getClass();
        }
    }

    public void writeBytes(byte[] data) throws Exception {
        
        if (writeBytes == null) {
            writeBytes = NettyChannel.getMethod(bytebuf, "writeBytes", byte[].class);
        }

        writeBytes.invoke(bytebuf, (Object) data);
    }

    public void release() throws Exception {
        
        if (release == null) {
            release = NettyChannel.getMethod(bytebuf, "release");
        }
        
        release.invoke(bytebuf);
    }

    public Object getHandle() {
        return bytebuf;
    }

    public int readableBytes() throws IOException {
        
        try {
            
            if (readableBytes == null) {
                readableBytes = NettyChannel.getMethod(bytebuf, "readableBytes");
            }
            
            return (Integer) readableBytes.invoke(bytebuf);
            
        } catch (Exception ex) {
            
            throw new IOException("IO", ex);
            
        }
    }

    public int readByte() throws IOException {
        
        try {
            
            if (readByte == null) {
                readByte = NettyChannel.getMethod(bytebuf, "readByte");
            }
            
            return (Byte) readByte.invoke(bytebuf);
            
        } catch (Exception ex) {
            
            throw new IOException("IO", ex);
            
        }
    }

    public void readBytes(byte[] bytes, int off, int len) throws IOException {
        
        try {
            
            if (readBytes == null) {
                readBytes = NettyChannel.getMethod(bytebuf, "readBytes", byte[].class, Integer.TYPE, Integer.TYPE);
            }
            
            readBytes.invoke(bytebuf, bytes, off, len);
            
        } catch (Exception ex) {
            
            throw new IOException("IO", ex);
            
        }
    }
}