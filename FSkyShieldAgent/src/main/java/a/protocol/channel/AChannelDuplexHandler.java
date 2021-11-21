package a.protocol.channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public abstract class AChannelDuplexHandler {

    public abstract boolean channelRead(Object packet);
    
    // never do this \/
    private static final int channelRegistered = 1268666341;//"channelRegistered".hashCode();
    private static final int channelUnregistered = 1376577150;//"channelUnregistered".hashCode();
    private static final int channelActive = 906025449;//"channelActive".hashCode();
    private static final int channelInactive = 492735118;//"channelInactive".hashCode();
    private static final int channelRead = 274075961;//"channelRead".hashCode();
    private static final int channelReadComplete = -921608814;//"channelReadComplete".hashCode();
    private static final int userEventTriggered = -338524280;//"userEventTriggered".hashCode();
    private static final int channelWritabilityChanged = -723763373;//"channelWritabilityChanged".hashCode();
    private static final int exceptionCaught = 2082632107;//"exceptionCaught".hashCode();
    
    public Object inject(Object handle) throws Exception {
        
        ClassLoader loader = handle.getClass().getClassLoader();
        Class<?> cls = Class.forName("io.netty.channel.ChannelInboundHandler", true, loader);

        final BypassInboundHandler bypass = new BypassInboundHandler();
        
        InvocationHandler handler = new InvocationHandler() {
            
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                int code = method.getName().hashCode();

                switch (code) {
                    case channelRead:
                        
                        if (!channelRead(args[1])) {
                            bypass.channelRead(args[0], args[1]);
                        }
                        
                        break;
                    case channelReadComplete:
                        bypass.channelReadComplete(args[0]);
                        break;
                    case channelRegistered:
                        bypass.channelRegistered(args[0]);
                        break;
                    case channelUnregistered:
                        bypass.channelUnregistered(args[0]);
                        break;
                    case channelActive:
                        bypass.channelActive(args[0]);
                        break;
                    case channelInactive:
                        bypass.channelInactive(args[0]);
                        break;
                    case userEventTriggered:
                        bypass.userEventTriggered(args[0], args[1]);
                        break;
                    case channelWritabilityChanged:
                        bypass.channelWritabilityChanged(args[0]);
                        break;
                    case exceptionCaught:
                        bypass.exceptionCaught(args[0], args[1]);
                        break;
                    default:
                        break;
                }

                return null;
            }
        };

        return Proxy.newProxyInstance(loader, new Class<?>[] { cls }, handler);
    }
}