/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import me.FurH.Core.close.Closer;
import me.FurH.Core.encript.Encrypto;

/**
 *
 * @author Luis
 */
public class CmUtils {
    
    public static JarEntry setEntryData(JarEntry entry, byte[] data) {
        
        entry.setCompressedSize(data.length);
        entry.setSize(data.length);
        entry.setCrc(Encrypto.getCRC32(data));
        entry.setMethod(ZipEntry.STORED);
        entry.setTime(System.currentTimeMillis());
        
        return entry;
    }

    public static File selectDir(File current) {
        return save(current, null, JFileChooser.DIRECTORIES_ONLY, null);
    }
    
    public static File selectJar(File current, File root) {
        return save(current, root, JFileChooser.FILES_ONLY, new FileNameExtensionFilter("Arquivo jar", "jar"));
    }
    
    public static File selectAny(File current, File root) {
        return save(current, root, JFileChooser.FILES_ONLY, null);
    }
    
    private static File save(File current, File root, int mode, FileNameExtensionFilter filter) {

        JFileChooser chooser = new JFileChooser();
        
        if (current != null && current.exists()) {
            
            if (current.isFile()) {
                chooser.setSelectedFile(current);
            } else
            if (current.isDirectory()) {
                chooser.setCurrentDirectory(current);
            }
            
        } else if (root != null && root.exists()) {
            
            if (root.isFile()) {
                chooser.setSelectedFile(root);
            } else
            if (root.isDirectory()) {
                chooser.setCurrentDirectory(root);
            }
        }

        if (filter != null) {
            chooser.setFileFilter(filter);
        }

        chooser.setFileSelectionMode(mode);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }

        return null;
    }
    
    public static int exec(String[] command) {
        
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        Process p = null;

        try {

            ProcessBuilder builder = new ProcessBuilder(command);

            builder.redirectErrorStream(true);

            p = builder.start();

            is = p.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line;

            System.out.println("[================== >>>> ==================]");

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            System.out.println("[================== <<<< ==================]");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {

            Closer.closeQuietly(is);
            Closer.closeQuietly(isr);
            Closer.closeQuietly(br);

            if (p != null) {
                p.destroy();
            }
        }
        
        return p.exitValue();
    }

    public static String getFileName(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }
}
