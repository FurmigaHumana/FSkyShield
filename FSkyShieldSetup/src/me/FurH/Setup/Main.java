package me.FurH.Setup;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import me.FurH.Setup.Native.SetupNative;
import me.FurH.Setup.gui.MainGui;
import me.FurH.Setup.utils.PathFinder;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Main {
    
    public static boolean is64;
    
    public static void error(String message) {
        JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

        is64 = (arch != null && arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64"));

        SetupNative.load();
        
        String workpath = PathFinder.getSpecialFolder("Local AppData");
        
        if (workpath == null) {
            error("Não foi possível encontrar o local de instalação");
            return;
        }
        
        File workdir = new File(workpath, "SkyShield" + File.separator + "setup");
        
        if (!workdir.exists()) {
            workdir.mkdirs();
        }
        
        MainGui gui = new MainGui();
        gui.init(workdir);
    }
}