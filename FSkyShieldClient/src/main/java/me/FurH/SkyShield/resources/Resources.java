/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.resources;

import java.awt.Image;
import java.io.File;
import java.security.SecureRandom;
import javax.swing.ImageIcon;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.file.FileUtils;
import me.FurH.SkyShield.packets.setup.Packet35AgentData;

/**
 *
 * @author lgpse
 */
public class Resources {
    
    public static final String lwpw = "@@REMOVED";
    private static final SecureRandom rnd;
    private static final Image icon;

    private static Packet35AgentData agent;
    private static File temp;

    static {
        icon = new ImageIcon(Resources.class.getResource("/splash.png")).getImage();
        rnd = new SecureRandom();
    }
    
    public static Image getIcon() {
        return icon;
    }

    public static void agentData(Packet35AgentData packet) {

        if (agent == null) {

            agent = packet;
            
        } else if (packet.data.length > 0) {
            
            agent.data = packet.data;
            agent.md5 = packet.md5;
                        
        }
    }

    public static String agentHash() {

        if (agent == null) {
            return "NO";
        }

        return agent.md5;
    }
    
    public static void initialize(String path) {

        temp = new File(path, "@@REMOVED");

        if (temp.exists()) {
            try {
                FileUtils.deleteDirectory(temp);
            } catch (Throwable ex) { }
        }

        temp.mkdirs();
    }

    public static File getNewAgent() throws Exception {

        File output = new File(temp, "@@REMOVED");
        output.deleteOnExit();
        
        FileUtils.setBytesOfFile(output, agent.data);

        if (!Encrypto.hash("MD5", output).equals(agent.md5)) {
            throw new Exception("Agente corrompido");
        }

        return output;
    }

    public static String newFileName() {
        return Math.abs(rnd.nextInt()) + "-" + Math.abs(rnd.nextInt()) + "-" + System.nanoTime();
    }
}