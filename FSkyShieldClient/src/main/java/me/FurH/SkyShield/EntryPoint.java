package me.FurH.SkyShield;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.util.Utils;
import me.FurH.JavaPacker.loader.ErrorGui;
import me.FurH.SkyShield.resources.Resources;
import net.bytebuddy.agent.VirtualMachine;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class EntryPoint {

    private static boolean is64;
    
    public static boolean is64() {
        return is64;
    }

    public static void main(String[] args) throws Exception {

        File path = new File("bin");
        
        System.setProperty("jna.boot.library.path", path.getAbsolutePath());
        System.setProperty("jna.platform.library.path", path.getAbsolutePath());

        System.setProperty("net.bytebuddy.library.name", "hotspot.dll");
        System.setProperty("jna.nounpack", "true");
        System.setProperty("jna.noclasspath", "true");
        
        try {

            ArrayList<File> files = new ArrayList<>();
            File dir = new File("client.jar").getAbsoluteFile().getParentFile();

            long lastmod = 0;
            File newest = null;

            for (File clfile : dir.listFiles()) {

                String name = clfile.getName();

                if (name.startsWith("client_") && name.endsWith(".jar")) {

                    if (newest == null || clfile.lastModified() > lastmod) {
                        newest = clfile;
                        lastmod = clfile.lastModified();
                    }

                    files.add(clfile);
                }
            }

            if (newest != null && files.size() > 1) {

                for (File file : files) {

                    if (file == newest) {
                        continue;
                    }

                    try {
                        file.delete();
                    } catch (Throwable ex) { }
                }
            }
            
            if (newest != null) {
                newest.deleteOnExit();
            }
            
        } catch (Throwable ex) {
            
            ex.printStackTrace();
            
        }
        
        File coords = new File("launcher.mf");
        
        if (coords.exists()) {

            byte[] data = FileUtils.getBytesFromFile(coords);

            if (!coords.delete()) {
                coords.deleteOnExit();
            }

            data = Encrypto.aes_decrypt(data, Resources.lwpw);

            String raw = new String(data, Utils.UTF8);
            String[] cmd = raw.split(",!\\|");

            int pid = Integer.parseInt(cmd[ 0 ]);
            int port = Integer.parseInt(cmd[ 1 ]);
            File agent = new File(cmd[ 2 ]);

            VirtualMachine vm = null;

            try {

                vm = VirtualMachine.ForHotSpot.attach(Integer.toString(pid));
                vm.loadAgent(agent.getAbsolutePath(), Integer.toString(port) + ":" + agent.getAbsolutePath());

            } catch (Throwable ex) {

                ErrorGui.error(ex, false, true, "Você precisa desinstalar o java e instalar a versão correta 64 bits");
                ex.printStackTrace();

            } finally {
                if (vm != null) {
                    try {
                        vm.detach();
                    } catch (Throwable ex) { }
                }
            }

            return;
        }
        
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

        is64 = (arch != null && arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64"));

        if (is64 && !System.getProperty("sun.arch.data.model").equals("64")) {
            JOptionPane.showMessageDialog(null, "Execute a versão 64 bits", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ShieldClient.startup(args);
        Thread.sleep(Integer.MAX_VALUE);
    }
}