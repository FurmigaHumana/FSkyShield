package me.FurH.SkyShield;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import me.FurH.Core.file.FileUtils;
import static me.FurH.SkyShield.Packer.read;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Obfuscate {
    
    public static void proguard(File in, File out, File config, String...extra) throws Exception {
        
        ArrayList<String> cmd = new ArrayList<>();

        cmd.add("java");
        cmd.add("-jar");

        cmd.add("@@REMOVEDproguard.jar");
        
        cmd.add("@" + config.getAbsolutePath());
        cmd.add("-injars");
        cmd.add(in.getAbsolutePath());
        cmd.add("-outjars");
        cmd.add(out.getAbsolutePath());
        
        if (extra != null) {
            cmd.addAll(Arrays.asList(extra));
        }
        
        read(cmd.toArray(new String[ 0 ]));
    }
    
    public static void allatori(File in, File out, File settings, String version) throws Exception {
        
        String line = FileUtils.getLineFromFile(settings);
        
        line = line.replace("{#injar}", in.getAbsolutePath());
        line = line.replace("{#outjar}", out.getAbsolutePath());
        
        if (version != null) {
            line = line.replace("{#version}", version);
        }

        String synth;
        if (JOptionPane.showConfirmDialog(null, "SHOULD WE SYNTHETIZE ALL METHODS?") == JOptionPane.OK_OPTION) {
            synth = "all";
        } else {
            synth = "none";
        }
        
        line = line.replace("{#synth}", synth);
        line = line.replace("{#synth}", synth);
        
        File config = new File(settings.getParent(), "tmp-" + settings.getName());
        
        FileUtils.setLineOfFile(config, line);
        
        ArrayList<String> cmd = new ArrayList<>();

        cmd.add("java");
        cmd.add("-jar");

        cmd.add("@@REMOVEDallatori.jar");

        cmd.add(config.getAbsolutePath());

        read(cmd.toArray(new String[ 0 ]));
    }
}