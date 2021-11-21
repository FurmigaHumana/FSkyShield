package a.protocol;

import static a.Agent.print;
import static a.protocol.CustomPayload.forName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Minecraft {

    private Method minecraftInstance;
    private Field currentScreen;
    private Class<?> minecraft;

    public Object getCurrentScreen() throws Exception {
        return currentScreen.get(minecraftInstance.invoke(null));
    }

    boolean isCompatible(GuiConnecting guiconnecting) {

        for (Field field : minecraft.getDeclaredFields()) {
            
            field.setAccessible(true);
            
            if (guiconnecting.isCompatible(field.getType())) {
                
                if (currentScreen != null) {
                    return false;
                }
                
                currentScreen = field;
            }
        }
        
        return currentScreen != null;
    }
    
    boolean isSupported(ClassLoader loader, NetworkManager manager, String minecraftcls) {

        try {
            
            minecraft = forName(minecraftcls, loader);

            for (Method method : minecraft.getDeclaredMethods()) {

                method.setAccessible(true);

                if (method.getParameterCount() > 0 || !Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                
                if (method.getReturnType() == minecraft) {

                    if (minecraftInstance != null) {
                        return false;
                    }

                    minecraftInstance = method;
                }
            }
            
            boolean networkcompat = false;
            
            for (Field field : minecraft.getDeclaredFields()) {
                
                field.setAccessible(true);
                
                if (manager.isCompatible(field.getType())) {
                    networkcompat = true;
                    break;
                }
            }
            
            if (!networkcompat) {
                return false;
            }
            
            return minecraftInstance != null;
            
        } catch (Throwable ex) {
            
            print(ex);
            
        }
        
        return false;
    }
}