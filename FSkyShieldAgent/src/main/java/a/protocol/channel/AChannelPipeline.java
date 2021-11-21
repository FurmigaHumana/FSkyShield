package a.protocol.channel;

import static a.protocol.channel.NettyChannel.getMethod;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class AChannelPipeline {

    private static Method addBefore;
    private static Method get;
    
    private final Object pipeline;

    AChannelPipeline(Object pipeline) {
        this.pipeline = pipeline;
    }

    public Object get(String decompress) throws Exception {
        
        if (get == null) {
            get = getMethod(pipeline, "get", String.class);
        }

        return get.invoke(pipeline, decompress);
    }

    public void addBefore(String packet_handler, String shieldin, Object inbound) throws Exception {

        if (addBefore == null) {
            addBefore = findAddMethod(pipeline.getClass().getDeclaredMethods());
            if (addBefore == null) {
                addBefore = findAddMethod(pipeline.getClass().getMethods());
            }
        }

        addBefore.invoke(pipeline, packet_handler, shieldin, inbound);
    }

    private Method findAddMethod(Method[] methods) {
        
        for (Method m : methods) {
            
            m.setAccessible(true);
            
            if (m.getName().equals("addBefore") && m.getParameterCount() == 3) {
                if (m.getParameterTypes()[0] == String.class && m.getParameterTypes()[1] == String.class) {
                    if (Modifier.isPublic(m.getModifiers())) {
                        return m;
                    }
                }
            }
        }
        
        return null;
    }
}