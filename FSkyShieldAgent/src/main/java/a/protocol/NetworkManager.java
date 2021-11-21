package a.protocol;

import static a.Agent.print;
import static a.protocol.CustomPayload.forName;
import a.protocol.channel.NettyChannel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class NetworkManager {
    
    private Class<?> networkCls;
    private Field channelField;
    
    boolean isCompatible(Class<?> type) {
        return networkCls == type;
    }

    NettyChannel getChannel(Object networkman) throws Exception {
        return new NettyChannel(channelField.get(networkman));
    }

    boolean isSupported(ClassLoader loader, CustomPayload payload, String networkcls) {

        try {

            networkCls = forName(networkcls, loader);
            Class<?> supercls = networkCls.getSuperclass();

            if (!supercls.getName().contains("SimpleChannelInboundHandler")) {
                return false;
            }

            Field[] fields = networkCls.getDeclaredFields();
            
            for (Field field : fields) {
                
                field.setAccessible(true);
                
                if (field.getType().getName().contains("Channel")) {
                    
                    if (channelField != null) {
                        return false;
                    }
                    
                    channelField = field;
                }
            }
            
            boolean supportpacket = false;
            
            for (Method method : networkCls.getDeclaredMethods()) {
                
                method.setAccessible(true);
                
                if (method.getParameterCount() <= 0) {
                    continue;
                }
                
                for (Class<?> type : method.getParameterTypes()) {
                    if (payload.isCompatible(type)) {
                        supportpacket = true;
                        break;
                    }
                }
            }
            
            if (!supportpacket) {
                return false;
            }
            
            return channelField != null;
            
        } catch (Throwable ex) {

            print(ex);
            
        }
        
        return false;
    }
}