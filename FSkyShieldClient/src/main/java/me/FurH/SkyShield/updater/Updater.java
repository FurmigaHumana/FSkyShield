package me.FurH.SkyShield.updater;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import me.FurH.Core.http.HttpUtils;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.util.Utils;
import me.FurH.SkyShield.ShieldClient;
import me.FurH.SkyShield.gui.IProgressUpdate;
import me.FurH.SkyShield.resources.Resources;

/**
 *
 * @author lgpse
 */
public class Updater extends javax.swing.JFrame {

    private boolean checking = false;
    private final ShieldClient client;
    private int totalsize = 0;
    private int totaldown = 0;
    private File output;
    
    public Updater() {
        this(null);
    }

    public Updater(ShieldClient client) {
        this.client = client;
        initComponents();
        setLocationRelativeTo(null);
        setIconImage(Resources.getIcon());
        setVisible(false);
    }

    public void checkUpdates(boolean silent) {

        if (checking) {
            return;
        }
        
        checking = true;

        if (!silent) {
            setLocationRelativeTo(client.main_gui);
            setVisible(true);
            
        }

        try {

            checkUpdate();

        } finally {

            checking = false;
            dispose();
            
        }
    }
    
    private void checkUpdate() {
        
        FileOutputStream fos = null;
        
        try {

            int installed = client.main_gui.buildnumber;
            String read = HttpUtils.readString("@@REMOVED");

            if (read == null || read.isEmpty()) {
                lastver.setText("O servidor não respondeu ao pedido");                
                return;
            }

            int latest = Integer.parseInt(read);

            curver.setText ("Instalado:  #" + installed);
            lastver.setText("Atualizado: #" + latest);

            if (installed >= latest) {

                progress.setIndeterminate(false);
                progress.setValue(100);
                progress.setString("Atualizado");

                Thread.sleep(2000);

                return;
            }

            output = new File("atualizacao", "execute_para_atualizar.exe");
            
            if (output.exists()) {
                output.delete();
            }

            File dir = output.getAbsoluteFile().getParentFile();
            
            if (dir != null) {
                dir.mkdirs();
            }

            fos = new FileOutputStream(output);

            read("@@REMOVED", fos, new IProgressUpdate() {
                @Override
                public void update(int read) {
                    totaldown += read;
                    if (totalsize > 0) {
                        calculate();
                    }
                }
            });

            fos.flush();
            fos.close();

            Desktop.getDesktop().open(dir);

            Thread.sleep(2000);
            Desktop.getDesktop().open(output);
            
            System.exit(0);

        } catch (Throwable ex) {

            curver.setText("Erro ao procurar versão");
            lastver.setText(ex.getMessage());

            ex.printStackTrace();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex1) { }

        } finally {
                        
            if (fos != null) {
                try {
                    fos.close();
                } catch (Throwable ex) { }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        curver = new javax.swing.JLabel();
        lastver = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SkyShield - Atualizador");
        setResizable(false);

        curver.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        curver.setText(" ");

        lastver.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        lastver.setText("Procurando nova versão...");

        progress.setIndeterminate(true);
        progress.setStringPainted(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(curver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lastver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(curver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void read(String address, OutputStream os, IProgressUpdate handler) throws Exception {

        HttpURLConnection conn = null;
        InputStream is = null;
        
        try {
            
            URL url = new URL(address);

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
                throw new Exception("[ " + code + "]: " + conn.getResponseMessage());
            }
            
            totalsize = conn.getContentLength();
            totaldown = 0;
            
            byte[] buffer = new byte[ 8192 ];
            int read;
            
            while ((read = is.read(buffer)) != -1) {
                
                os.write(buffer, 0, read);
                
                if (handler != null) {
                    handler.update(read);
                }
            }

        } finally {
           
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Throwable ex) { }
            }
            
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable ex) { }
            }
        }
    }

    private void calculate() {
        
        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                
                try {
                    
                    if (progress.isIndeterminate()) {
                        progress.setIndeterminate(false);
                    }
                    
                    int value = (int) NumberUtils.getWorkDoneDouble(totaldown, totalsize);
                    progress.setString(Utils.getFormatedBytes(totaldown) + " / " + Utils.getFormatedBytes(totalsize) + " - " + value + "%");
                    progress.setValue(value);
                    
                } catch (Throwable ex) { }
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel curver;
    private javax.swing.JLabel lastver;
    private javax.swing.JProgressBar progress;
    // End of variables declaration//GEN-END:variables
}
