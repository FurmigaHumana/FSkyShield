package a.protocol.channel;

import java.lang.reflect.Method;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class AByteBufAllocator {
    
    private static Method buffer;
    
    private final Object allocator;

    AByteBufAllocator(Object allocator) {
        this.allocator = allocator;
    }

    public AByteBuf buffer() throws Exception {

        if (buffer == null) {
            buffer = NettyChannel.getMethod(allocator, "buffer");
        }
        
        return new AByteBuf(buffer.invoke(allocator), true);
    }
}