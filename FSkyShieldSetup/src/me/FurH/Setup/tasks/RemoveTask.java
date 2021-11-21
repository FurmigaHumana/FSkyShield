package me.FurH.Setup.tasks;

import java.io.File;
import javax.swing.JOptionPane;
import me.FurH.Core.file.FileUtils;
import static me.FurH.Setup.Main.error;
import me.FurH.Setup.gui.MainGui;
import me.FurH.Setup.utils.PathFinder;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class RemoveTask extends Thread {

    private final File workdir;
    private final MainGui gui;

    public RemoveTask(File workdir, MainGui gui) {
        this.workdir = workdir;
        this.gui = gui;
    }

    @Override
    public void run() {
        
        gui.bar("", -1);
        gui.label("Excluindo arquivos...");
        
        try {

            File path = workdir.getParentFile();
            File client = null;
            
            boolean errors = false;

            for (File file : path.listFiles()) {
                
                gui.label("Excluindo "+ file.getName() + "...");
                
                if (!file.exists()) {
                    continue;
                }
                
                if (file.getName().equals("client.jar")) {
                    client = file;
                    continue;
                }
                
                if (file.isDirectory()) {
                    
                    if (file.getName().equals("setup")) {
                        continue;
                    }

                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (Throwable ex) {
                        errors = true;
                    }
                    
                } else {

                    if (!file.delete()) {
                        errors = true;
                    }
                }
            }
            
            tryDeleteShortcut("desktop");
            tryDeleteShortcut("startup");
            
            tryDeletePrograms();
            
            if (errors) {

                error("Não foi possível excluir todos os arquivos\nVerifique se o SkyShield esta totalmente fechado\ne tente novamente.");

            } else {

                if (client != null) {
                    client.delete();
                }
                
                gui.bar("100%", 100);
                gui.label("Concluído!");

                JOptionPane.showMessageDialog(gui, "Desinstalação concluída com sucesso!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
            
                System.exit(0);
            }
            
        } catch (Throwable ex) {
            
            error("Não foi possível concluír, " + ex.getMessage());
            
        }
    }
    
    private void tryDeletePrograms() {

        try {
            
            String programs = PathFinder.getSpecialFolder("programs");

            if (programs == null) {
                return;
            }

            File progfolder = new File(programs, "SkyShield");

            if (progfolder.exists()) {
                FileUtils.deleteDirectory(progfolder);
            }
            
        } catch (Throwable ex) { }
    }

    private void tryDeleteShortcut(String folder) {
       
        try {
            
            String shellfolder = PathFinder.getSpecialFolder(folder);
            if (shellfolder != null) {
                new File(shellfolder, "SkyShield.lnk").delete();
            }
            
        } catch (Throwable ex) { }
    }
}