package a;

import a.protocol.channel.AByteBuf;
import java.io.IOException;
import java.io.InputStream;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class BuffStream extends InputStream {

    private final AByteBuf buf;

    public BuffStream(AByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public int read() throws IOException {
        
        if (buf.readableBytes() <= 0) {
            return -1;
        }
        
        return buf.readByte() & 0xFF;
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        
        if (buf.readableBytes() <= 0) {
            return -1;
        }

        len = Math.min(len, buf.readableBytes());
        buf.readBytes(bytes, off, len);
        
        return len;
    }
}