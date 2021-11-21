package me.FurH.Setup.tasks;

import flzma.LzmaUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import me.FurH.Core.close.Closer;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.http.HttpUtils;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.util.Utils;
import me.FurH.Setup.Main;
import static me.FurH.Setup.Main.error;
import me.FurH.Setup.gui.MainGui;
import me.FurH.Setup.streams.TrackedInputStream;
import me.FurH.Setup.utils.PathFinder;
import me.FurH.Setup.utils.Shortcut;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class SetupTask extends Thread {

    private final File workdir;
    private final MainGui gui;

    public SetupTask(File workdir, MainGui gui) {
        this.workdir = workdir;
        this.gui = gui;
    }

    @Override
    public void run() {

        gui.bar("", -1);
        gui.label("Procurando lista de arquivos...");

        try {

            ArrayList<String> hashes = HttpUtils.read("@@REMOVED");
            gui.label("Iniciando download...");

            File common = download("common", hashes, 0);
            File x32 = download("x32", hashes, 1);
            File root = workdir.getParentFile();

            unzip(common, "", root);
            
            if (!Main.is64) {

                unzip(x32, "", root);

            } else {
                
                File x64 = download("x64", hashes, 2);
                
                unzip(x64, "", root);
             
                File x32folder = new File(root, "32-bit");
                x32folder.mkdirs();

                unzip(common, x32folder.getName() + "/", x32folder);
                unzip(x32, x32folder.getName() + "/", x32folder);

                File ready = new File(x32folder, "ready");
                
                if (!ready.exists()) {
                    ready.createNewFile();
                }
            }
            
            File client = new File(root, "SkyShield.exe");

            if (!client.exists()) {
                error("Não foi possível concluir a instalação");
                return;
            }

            createShortcut("desktop", client);
            createShortcut("startup", client);
            
            attemptPrograms(client);
            
            gui.bar("100%", 100);
            gui.label("Concluído!");
            
            JOptionPane.showMessageDialog(gui, "Instalação concluída com sucesso!\nUtilize o ícone criado na area de trabalho!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
            
            System.exit(0);
            
        } catch (Exception ex) {

            error("Não foi possível continuar, " + ex.getMessage());

        }
    }
    
    private void attemptPrograms(File client) {
        
        try {
            
            String programs = PathFinder.getSpecialFolder("programs");

            if (programs == null) {
                return;
            }

            File progfolder = new File(programs, "SkyShield");

            if (!progfolder.exists()) {
                progfolder.mkdirs();
            }

            Shortcut.createShortcut(client, new File(progfolder, "SkyShield.lnk"));
            
        } catch (Throwable ex) {
            
            ex.printStackTrace();
            
        }
    }
    
    private void createShortcut(String folder, File client) throws IOException {
        
        String shellfolder = PathFinder.getSpecialFolder(folder);

        if (shellfolder == null) {
            error("Não foi possível encontrar a pasta " + folder);
            return;
        }
        
        Shortcut.createShortcut(client, new File(shellfolder, "SkyShield.lnk"));
    }

    private void unzip(File file, String folder, File dest) throws IOException {

        gui.bar("", -1);
        gui.label("Copiando arquivos...");

        ZipInputStream zis = null;
        FileInputStream in = null;

        try {

            in = new TrackedInputStream(file, gui);
            zis = new ZipInputStream(in);

            ZipEntry entry = zis.getNextEntry();
            byte[] buffer = new byte[8192];

            while (entry != null) {

                FileOutputStream fos = null;

                try {

                    File eout = new File(dest, entry.getName());
                    FileUtils.createParentDir(eout);

                    if (entry.isDirectory()) {

                        eout.mkdirs();

                    } else {

                        gui.label("Copiando " + folder + entry.getName() + "...");

                        if (eout.exists() && !eout.delete()) {
                            error("Não foi possível copiar o arquivo " + entry.getName() + "\nVerifique se o seu SkyShield esta totalmente fechado e tente novamente");
                            throw new IOException("O arquivo " + entry.getName() + " esta em uso ou corrompido!");
                        }

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

    private File download(String fileName, ArrayList<String> hashes, int index) throws Exception {

        File zipfile = new File(workdir, fileName + ".zip");
        String ziphash = hashes.get(index);

        if (zipfile.exists()) {
            if (ziphash.equals(Encrypto.hash("MD5", zipfile))) {
                System.out.println(zipfile.getName() + " found, skip all.");
                return zipfile;
            }
        }

        File tz = new File(workdir, fileName + ".tz");
        String[] hash = hashes.get(index + 3).split(",");

        if (tz.exists()) {
            if (hash[0].equals(Encrypto.hash("MD5", tz))) {
                System.out.println(tz.getName() + " found, skip download.");
                decompress(tz, zipfile, ziphash);
                return zipfile;
            }
        }

        download(tz, Long.parseLong(hash[1]));

        if (!hash[0].equals(Encrypto.hash("MD5", tz))) {
            throw new Exception("Erro ao baixar arquivo " + tz.getName());
        }

        decompress(tz, zipfile, ziphash);

        return zipfile;
    }

    private void decompress(File input, File output, String ziphash) throws Exception {

        gui.bar("", -1);
        gui.label("Extraindo " + input.getName() + "... (pode levar alguns minutos)");

        new LzmaUtils().decompress(new TrackedInputStream(input, gui), output);

        if (!ziphash.equals(Encrypto.hash("MD5", output))) {
            throw new Exception("Erro ao descomprimir arquivo " + input.getName());
        }
    }

    private void download(File tz, long total) throws Exception {

        gui.bar("", -1);
        gui.label("Baixando " + tz.getName() + "...");

        HttpURLConnection conn = null;
        FileOutputStream out = null;
        InputStream is = null;

        try {

            URL url = new URL("@@REMOVED" + tz.getName());

            conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(false);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");

            conn.setUseCaches(false);

            try {
                is = conn.getInputStream();
            } catch (IOException ex) {
                is = conn.getErrorStream();
            }

            int code = conn.getResponseCode();

            if (code != 200) {
                throw new Exception(conn.getResponseMessage());
            }

            String totalstr = Utils.getFormatedBytes(total);
            int totaldone = 0;

            byte[] buffer = new byte[8192];
            int read;

            out = new FileOutputStream(tz);

            while ((read = is.read(buffer)) != -1) {

                totaldone += read;
                out.write(buffer, 0, read);

                int done = NumberUtils.getWorkDoneLong(totaldone, total);
                gui.bar(Utils.getFormatedBytes(totaldone) + "/" + totalstr + " - " + done + "%", done);
            }

        } finally {

            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Throwable ex) {
                }
            }

            Closer.closeQuietly(is);
            Closer.closeQuietly(out);
        }
    }
}
