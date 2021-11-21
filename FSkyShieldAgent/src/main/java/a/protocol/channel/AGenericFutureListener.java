package a.protocol.channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public abstract class AGenericFutureListener implements Runnable {

    private static Method addListener;
    
    private Method findMethod(Method[] methods) {
        
        for (Method m : methods) {
            m.setAccessible(true);
            if ("addListener".equals(m.getName())) {
                return m;
            }
        }
        
        return null;
    }
    
    public void inject(AChannelFuture writeAndFlush) throws Exception {

        Object handle = writeAndFlush.getHandle();

        if (addListener == null) {
            addListener = findMethod(handle.getClass().getDeclaredMethods());
            if (addListener == null) {
                addListener = findMethod(handle.getClass().getMethods());
            }
        }

        ClassLoader loader = handle.getClass().getClassLoader();
        Class<?> cls = addListener.getParameterTypes()[0];

        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                run();
                return null;
            }
        };

        Object instance = Proxy.newProxyInstance(loader, new Class<?>[] { cls }, handler);
        addListener.invoke(handle, instance);
    }

}