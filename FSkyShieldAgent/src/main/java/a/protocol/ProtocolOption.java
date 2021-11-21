package a.protocol;

import static a.Agent.print;
import a.Handler;
import a.protocol.channel.NettyChannel;
import java.util.concurrent.TimeUnit;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ProtocolOption {
   
    private CustomPayload send_payload;
    private CustomPayload recieve_payload;
    private NetworkManager network;
    private Minecraft minecraft;
    private GuiConnecting guiconnecting;

    private final String send_payloadcls;
    private final String networkcls;
    private final String minecraftcls;
    private final String guiconncls;
    private final String recieve_payloadcls;
    private final String payloadnamecls;

    private NettyChannel channel;

    public ProtocolOption(String input) {

        String[] split = input.split("\\,");

        send_payloadcls     = split[0];
        networkcls          = split[1];
        minecraftcls        = split[2];
        guiconncls          = split[3];
        recieve_payloadcls  = split[4];

        if (split.length == 5) {
            payloadnamecls = null;
        } else {
            payloadnamecls = split[5];
        }
    }
    
    public void write(String channe, byte[] data) throws Exception {

        if (!channel.isActive()) {
            return;
        }

        send_payload.write(channel, channe, data, this);
    }

    public void injectChannel(final Handler handler) throws Exception {

        Object currentScreen = minecraft.getCurrentScreen();
        
        if (currentScreen == null) {
            throw new Exception("Failed to fetch current screen");
        }
        
        if (!guiconnecting.isExact(currentScreen)) {
            throw new Exception("Login screen is not on");
        }

        Object networkman = guiconnecting.getNetwork(currentScreen);

        if (networkman == null) {
            throw new Exception("No network manager on gui");
        }

        this.channel = network.getChannel(networkman);

        channel.eventLoop().execute(new Runnable() {
            
            @Override
            public void run() {

                try {

                    PacketInbound inbound = new PacketInbound(channel, handler, recieve_payload);
                    tryInject(channel, inbound);

                    try {
                        channel.pipeline().addBefore("packet_handler", "shieldin", inbound.getNative());
                    } catch (Exception ex1) {
                        print(ex1);
                        channel.pipeline().addBefore("packet_handler", "shieldin", inbound.getProxy());
                    }

                } catch (Exception ex) {

                    print(ex);
                    
                }
            }
        });

        channel.closeFuture().syncUninterruptibly();
        handler.disconnected();
    }

    private void tryInject(final NettyChannel channel, final PacketInbound inbound) throws Exception {
        
        channel.eventLoop().schedule(new Runnable() {
            
            @Override
            public void run() {
                
                try {
                    
                    if (!channel.isActive()) {
                        inbound.handler.failed();
                        return;
                    }
                    
                    if (channel.pipeline().get("decompress") != null) {
                        inbound.handler.ready();
                        return;
                    }
                    
                    tryInject(channel, inbound);
                    
                } catch (Exception ex) {

                    print(ex);
                    
                }
            }
        }, 10, TimeUnit.MILLISECONDS);
    }
    
    public boolean isSupported(ClassLoader loader) {

        this.send_payload = new CustomPayload();
        
        if (!send_payload.isSupported(loader, send_payloadcls, payloadnamecls)) {
            return false;
        }
        
        this.recieve_payload = new CustomPayload();
        
        if (!recieve_payload.isSupported(loader, recieve_payloadcls, payloadnamecls)) {
            return false;
        }
        
        this.network = new NetworkManager();
        
        if (!network.isSupported(loader, send_payload, networkcls)) {
            return false;
        }
        
        this.minecraft = new Minecraft();
        
        if (!minecraft.isSupported(loader, network, minecraftcls)) {
            return false;
        }

        this.guiconnecting = new GuiConnecting();
        
        if (!guiconnecting.isSupported(loader, network, guiconncls)) {
            return false;
        }
        
        if (!minecraft.isCompatible(guiconnecting)) {
            return false;
        }
        
        return true;
    }
}