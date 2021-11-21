/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import me.FurH.Core.close.Closer;
import me.FurH.Core.config.FProperties;
import me.FurH.Core.encript.Base64;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.file.SimpleVisitor;
import me.FurH.Core.object.ObjectHolder;
import me.FurH.Core.zip.ZipUtils;
import static me.FurH.SkyShield.EncryptPacker.newFile;

/**
 *
 * @author lgpse
 */
public class Packer {
    
    private static void unzipLib(File client_lib, File client_dir, File client_int) throws IOException {
        
        if (client_lib.exists()) {
            
            List<File> libraries = FileUtils.getAllFilesAt(client_lib);

            for (File file : libraries) {
                
                if (file.getName().equals("tools.jar")) {
                    continue;
                }
                
                ZipUtils.unzip(file, client_dir);
                file.delete();
            }

            FileUtils.deleteDirectory(client_lib);

            for (File file : client_dir.listFiles()) {
                String name = file.getName();
                if (!name.equals("@@REMOVED") && !name.equals("@@REMOVED") && !name.equals("@@REMOVED") && !name.equals(client_int.getName())) {
                    FileUtils.deleteDirectory(file);
                }
            }
        }
        
        File coredir = new File(client_dir, "@@REMOVED");
        if (coredir.exists()) {
            for (File file : coredir.listFiles()) {
                if (file.getName().startsWith("Main")) {
                    file.delete();
                }
            }
        }
    }
    
    private static void zipClient(File client_dir, File client_int) throws IOException {
       
        ZipUtils.zipDir(client_dir, client_int);

        for (File file : client_dir.listFiles()) {
            String name = file.getName();
            if (!name.equals(client_int.getName())) {
                FileUtils.deleteDirectory(file);
            }
        }
    }
    
    private static void packInstaller(File output_dir, File client_pro, File client_xml) throws Exception {
        
        File setup_dir = new File("@@REMOVED");
        
        File setup_init = new File(setup_dir, "FSkyShieldSetup.jar");
        File setup_out = new File(setup_dir, "FSkyShieldSetup-out.jar");
        File setup_ou2 = new File(setup_dir, "FSkyShieldSetup-out2.jar");
        File setup_lib = new File(setup_dir, "lib");

        unzipLib(setup_lib, setup_dir, setup_init);

        if (setup_init.exists()) {
            ZipUtils.unzip(setup_init, setup_dir);
            setup_init.delete();
        }
        
        zipClient(setup_dir, setup_init);

        Obfuscate.proguard(setup_init, setup_out, client_pro, new String[] {
            "-repackageclasses",
            "setup",
            "-flattenpackagehierarchy",
            "setup"
        });

        Obfuscate.allatori(setup_out, setup_ou2, client_xml, "setup");

        File dest = new File(output_dir, "skyshield_setup.jar");
        File temp = new File(setup_dir, "temp");
        
        unzipEncoded(new FileInputStream(setup_ou2), temp);
        
        File natives = new File("@@REMOVED");
        
        File n32 = new File(natives, "setupnative32.dll");
        File n64 = new File(natives, "setupnative64.dll");
        
        FileUtils.copyFromTo(n32, new File(temp, Base64.encodeToString("native32.dll", false)));
        FileUtils.copyFromTo(n64, new File(temp, Base64.encodeToString("native64.dll", false)));
        
        zipEncodedDir(temp, dest);

        FileUtils.deleteDirectory(temp);
    }
    
    private static void copyLauncher(File output_dir) throws IOException {
        
        File dir = new File("@@REMOVED");
        
        File n32 = new File(dir, "SkyShield_x32.exe");
        File n64 = new File(dir, "SkyShield_x64.exe");
        
        System.out.println("Copy " + n32.getName());
        FileUtils.copyFromTo(n32, new File(output_dir, "x32\\SkyShield.exe"));
        
        System.out.println("Copy " + n64.getName());
        FileUtils.copyFromTo(n64, new File(output_dir, "x64\\SkyShield.exe"));
    }
    
    public static void main(String[] args) throws Throwable {
                
        File output_dir = new File("@@REMOVED");
        output_dir.mkdirs();
        
        if (JOptionPane.showConfirmDialog(null, "Copy launcher files?") == JOptionPane.OK_OPTION) {
            copyLauncher(output_dir);
        }
        
        if (JOptionPane.showConfirmDialog(null, "Build cert library?") == JOptionPane.OK_OPTION) {

            // process agent
            System.out.println("Building certificates...");
            File cert_dir = new File("@@REMOVED");

            File cert_input = new File(cert_dir, "FSkyShieldCert-1.0-SNAPSHOT.jar");
            File cert_proguard = newFile(cert_input, "proguard");
            File cert_proconfig = new File(cert_dir.getParent(), "cert.pro");

            Obfuscate.proguard(cert_input, cert_proguard, cert_proconfig);
        }
        
        if (JOptionPane.showConfirmDialog(null, "Build injection agente?") == JOptionPane.OK_OPTION) {

            // process agent
            System.out.println("Building agent...");
            File agent_dir = new File("@@REMOVED");

            File agent_int = new File(agent_dir, "FSkyShieldAgent-1.0-SNAPSHOT.jar");
            File agent_pro = new File(agent_dir.getParent(), "agent.pro");
            File agent_xml = new File(agent_dir.getParent(), "agent.xml");

            EncryptPacker.encryptJar(agent_int, agent_pro, null, agent_xml, "a", true);
        }

        File client_dir = new File("@@REMOVED");
        File client_pro = new File(client_dir.getParent(), "client.pro");
        File client_xml = new File(client_dir.getParent(), "client.xml");

        if (JOptionPane.showConfirmDialog(null, "Construir cliente?") == JOptionPane.OK_OPTION) {
            
            // process main client
            System.out.println("Building client...");
            File client_jar = new File(client_dir, "FSkyShieldClient-1.0-SNAPSHOT.jar");

            File client_prop = new File("@@REMOVED", "version.properties");
            FProperties props = new FProperties(client_prop);
            props.load();

            String build = props.getProperty("buildnumber").replaceAll("[^0-9]", "");

            String[] proguardextras = new String[] {
                "-repackageclasses",
                "v" + build,
                "-flattenpackagehierarchy",
                "v" + build
            };

            File output = EncryptPacker.encryptJar(client_jar, client_pro, proguardextras, client_xml, "v" + build, false);
            
            FileUtils.copyFromTo(output, new File("@@REMOVED"));
        }
        // pack for java installer

        if (JOptionPane.showConfirmDialog(null, "Build launcher?") == JOptionPane.OK_OPTION) {
            
            System.out.println("Creating setup.jar");

            packInstaller(output_dir, client_pro, client_xml);
        }
        
        if (JOptionPane.showConfirmDialog(null, "Create java setup packages?") == JOptionPane.OK_OPTION) {
            System.out.println("Creating packs...");
            SetupBuilder.packFiles();
        }
        
        System.out.println("Done.");
    }

    public static void read(String[] command) throws Exception {
        
        ProcessBuilder builder = new ProcessBuilder();

        builder.command(command);
        
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        
        Process process = builder.start();
        process.waitFor();
    }
    
//    private static void saveKey(String sha, String[] plugin) throws Exception {
//        File output = new File(gui.getRsaOutput().getParent(), "zpb_"+sha+".key");
//
//        FileUtils.setBytesOfFile(output, getKeyByte());
//
//        upload(output, plugin);
//    }


    
    public static String scape(String input) {
        return "\"" + input + "\"";
    }
    
    public static String nextPass() {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.#@$%^&*()_+}{[]\\|;:',.<>/?~`S";
        String pass = "";
        
        Random rnd = new Random();

        while (pass.length() < 16) {
            pass += chars.charAt( rnd.nextInt(chars.length() - 1) );
        }
        
        return pass;
    }
    
    public static void unzipEncoded(InputStream in, File output) throws IOException {

        FileUtils.createParentDir(output);
        
        ZipInputStream  zis = null;

        try {

            zis = new ZipInputStream(in);

            ZipEntry entry = zis.getNextEntry();
            byte[] buffer = new byte[ 8192 ];
            
            while (entry != null) {

                FileOutputStream fos = null;
                
                try {
                    
                    File eout = new File(output, Base64.encodeToString(entry.getName(), false));
                    FileUtils.createParentDir(eout);
                    
                    if (!entry.isDirectory()) {

                        fos = new FileOutputStream(eout);
                        int read;

                        while ((read = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }

                        fos.flush();
                    }

                } finally {

                    Closer.closeQuietly(fos);

                }

                entry = zis.getNextEntry();
            }

        } finally {
            Closer.closeQuietly(in);
            Closer.closeQuietly(zis);
        }
    }
    
    public static void zipEncodedDir(File dir, File output) throws IOException {

        int sub = dir.getAbsolutePath().length();
        
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        
        try {

            fos = new FileOutputStream(output);
            zos = new ZipOutputStream(fos);

            final byte[] buffer = new byte[ 4096 ];

            final ObjectHolder<ZipOutputStream> zosh = new ObjectHolder<>();
            zosh.set(zos);

            FileUtils.visitAllFilesAt(new SimpleVisitor() {

                @Override
                public void visit(File next) {
                    
                    String name = next.getName();

                    if (!next.isFile() || next.getAbsolutePath().equals(output.getAbsolutePath())) {
                        return;
                    }

                    String key = next.getAbsolutePath().substring(sub + 1)
                                    .replace(File.separatorChar, '/');
                    
                    key = key.replace(name, Base64.decodeToString(name));
                    
                    ZipEntry entry = new ZipEntry(
                            key);

                    try {

                        entry.setMethod(ZipEntry.DEFLATED);

                        ZipOutputStream zos = zosh.get();
                        zos.putNextEntry(entry);

                        FileInputStream fis = null;

                        try {

                            fis = new FileInputStream(next);

                            int read;
                            while ((read = fis.read(buffer)) != -1) {
                                zos.write(buffer, 0, read);
                            }

                        } finally {
                            
                            Closer.closeQuietly(fis);
                            
                        }

                        zos.flush();
                        
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

            }, dir, false);

            zosh.set(null);

            zos.closeEntry();
            zos.finish();
            
            zos.flush();
            zos.close();

        } finally {
            Closer.closeQuietly(zos);
            Closer.closeQuietly(fos);
        }
    }
}