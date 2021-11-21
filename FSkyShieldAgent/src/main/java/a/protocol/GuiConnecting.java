package a.protocol;

import static a.Agent.print;
import static a.protocol.CustomPayload.forName;
import java.lang.reflect.Field;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class GuiConnecting {

    private Field networkField;
    private Class<?> guicls;
    
    boolean isCompatible(Class<?> type) {
        return guicls == type || guicls.getSuperclass() == type;
    }
    
    boolean isExact(Object currentScreen) {
        return currentScreen.getClass() == guicls;
    }

    Object getNetwork(Object currentScreen) throws Exception {
        return networkField.get(currentScreen);
    }

    boolean isSupported(ClassLoader loader, NetworkManager network, String guiconncls) {

        try {
            
            guicls = forName(guiconncls, loader);
            
            for (Field field : guicls.getDeclaredFields()) {
                
                field.setAccessible(true);
                
                if (network.isCompatible(field.getType())) {
                    
                    if (networkField != null) {
                        return false;
                    }
                    
                    networkField = field;
                }
            }
            
            return networkField != null;
            
        } catch (Throwable ex) {
            
            print(ex);
            
        }
        
        return false;
    }
}