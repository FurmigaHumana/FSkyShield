package a.protocol;

import static a.Agent.print;
import a.Handler;
import a.protocol.channel.AByteBuf;
import a.protocol.channel.AGenericFutureListener;
import a.protocol.channel.NettyChannel;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CustomPayload {

    private Constructor<?> byteConstruct; 
    private Field channelField;
    private Field byteField;

    private Constructor<?> nameClass;
    private Class<?> packetClass;
    
    boolean isCompatible(Class<?> type) {
        return this.packetClass.getSuperclass() == type;
    }
    
    private Class<?> matchPacket;
    
    boolean handlePacket(Handler handler, NettyChannel channel, Object packet) {
        
        try {

            if (matchPacket == null && packet.getClass().getName().equals(packetClass.getName())) {
                matchPacket = packet.getClass();
            }

            if (packet.getClass() == matchPacket) {

                String tag = channelField.get(packet).toString();                
                String compare;
                
                if (nameClass == null) {
                    compare = "shield";
                } else {
                    compare = "shield:shield";
                }
                                
                if (tag.contains(compare)) {
                    handler.payload(channel, tag, new AByteBuf(byteField.get(packet), false));
                    return true;
                }
            }

        } catch (Throwable ex) {

            print(ex);

        }
        
        return false;
    }
    
    void write(final NettyChannel channel, final String text, final byte[] data, final ProtocolOption protocol) throws Exception {
        
        channel.eventLoop().execute(new Runnable() {
           
            @Override
            public void run() {
                
                try {
                                        
                    Object textObj;
                    
                    if (nameClass == null) {
                        textObj = text;
                    } else {
                        textObj = nameClass.newInstance("shield", "shield");
                    }
                    
                    Object packet = packetClass.newInstance();

                    final AByteBuf buff = channel.alloc().buffer();
                    buff.writeBytes(data);

                    Object buffobj = byteConstruct.newInstance(buff.getHandle());
                    
                    channelField.set(packet, textObj);
                    byteField.set(packet, buffobj);

                    AGenericFutureListener task = new AGenericFutureListener() {
                        @Override
                        public void run() {
                           try {
                                buff.release();
                            } catch (Throwable ex) {
                                print(ex);
                            }
                        }
                    };

                    task.inject(channel.writeAndFlush(packet));
                    
                } catch (Throwable ex) {
                    
                    print(ex);
                    
                }
            }
        });
    }
    
    public static Class forName(String clsName, ClassLoader loader) throws ClassNotFoundException {
        if (loader == null) {
            return Class.forName(clsName);
        } else {
            return Class.forName(clsName, false, loader);
        }
    }
    
    boolean isSupported(ClassLoader loader, String clsname, String typeName) {

        try {

            packetClass = forName(clsname, loader);
            
            if (typeName != null) {
                Class<?> cls = forName(typeName, loader);
                nameClass = cls.getConstructor(String.class, String.class);
                nameClass.setAccessible(true);
            }
            
            for (Field field : packetClass.getDeclaredFields()) {

                field.setAccessible(true);
                
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                Class<?> type = field.getType();

                if ((typeName == null ? (type == String.class) : type.getName().equals(typeName))) {
                    
                    if (channelField != null) {
                        return false;
                    }
                    
                    channelField = field;
                    
                } else if (type.getSuperclass().getName().equals("io.netty.buffer.ByteBuf")) {
                    
                    if (byteField != null) {
                        return false;
                    }

                    Constructor<?>[] constructors = type.getConstructors();
                    
                    for (Constructor<?> constructor : constructors) {
                        
                        constructor.setAccessible(true);

                        if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].getName().equals("io.netty.buffer.ByteBuf")) {
                            
                            if (byteConstruct != null) {
                                return false;
                            }
                            
                            byteConstruct = constructor;
                        }
                    }

                    if (byteConstruct != null) {
                        byteField = field;
                    }
                }
            }
            
            return byteConstruct != null && channelField != null && byteField != null;
            
        } catch (Throwable ex) {

            print(ex);
            
        }
        
        return false;
    }
}