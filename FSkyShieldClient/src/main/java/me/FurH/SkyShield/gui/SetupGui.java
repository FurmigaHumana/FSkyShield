/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.gui;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import me.FurH.Core.close.Closer;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.file.SimpleVisitor;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.util.Callback;
import me.FurH.Core.util.Utils;
import me.FurH.SkyShield.resources.Resources;

/**
 *
 * @author lgpse
 */
public class SetupGui extends javax.swing.JFrame {

    private long totalbytes = 0;
    private long bytesdone = 0;
    private boolean cancel = false;
    
    public static void finishSetup(File x32, File check, Callback<Boolean> cb) {

        SetupGui gui = new SetupGui();
        gui.setVisible(true);

        try {

            Thread t =  new Thread() {
                
                @Override
                public void run() {
                    
                    try {
                        
                        gui.finishSetup0(x32, check);
                        
                        Thread.sleep(5000);
                        cb.invoke(true);
                        
                    } catch (InterruptedException ex) {
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        gui.error("Não foi possível concluir: " + ex.getMessage());
                    }
                }
            };

            t.start();

        } catch (Throwable ex) {

            ex.printStackTrace();
            gui.error("Não foi possível concluir: " + ex.getMessage());
            
        }
    }

    private void error(String string) {
        glabel.setText("ERRO!");
        desc(string, 0);
    }

    private void finishSetup0(File x32, File check) throws InterruptedException, IOException {

        File x64 = x32.getAbsoluteFile().getParentFile();

        File lib1 = new File(x32, "lib");
        File lib2 = new File(x64, "lib");

        File client1 = new File(x32, "client.jar");
        File client2 = new File(x64, "client.jar");

        countBytes(client2, lib2);

        bar.setIndeterminate(false);
        bar.setValue(0);

        copyFromTo(client2, client1);

        if (!lib1.exists()) {

            int base = lib2.getAbsolutePath().length();

            FileUtils.visitAllFilesAt(new SimpleVisitor() {
                
                @Override
                public void visit(File file) {
                    try {
                        copyFromTo(file, new File(lib1, file.getAbsolutePath().substring(base)));
                    } catch (Exception ex) {
                        cancel = true;
                        ex.printStackTrace();
                        error("Não foi possível concluir: " + ex.getMessage());
                    }
                }

                @Override
                public boolean isCancelled() {
                    return cancel;
                }
            }, lib2);
        }
        
        if (cancel) {
            return;
        }

        check.createNewFile();
        dispose();
    }

    private void countBytes(File client2, File lib2) throws IOException {
        
        totalbytes = client2.length();
        
        FileUtils.visitAllFilesAt(new SimpleVisitor() {
           
            @Override
            public void visit(File file) {
                totalbytes += file.length();
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        }, lib2);
    }

    private void desc(String text, int percent) {
       
        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                
                desclabel.setText(text);
                
                if (percent >= 0) {
                    bar.setValue(percent);
                }
            }
        });
    }
    
    private void copyFromTo(File in, File to) throws IOException, InterruptedException {

        String bytes = Utils.getFormatedBytes(in.length());
        desc(in.getName() + "...", -1);

        FileUtils.createParentDir(to);
        
        if (to.exists()) {
            if (!to.delete()) {
                throw new IOException("Cant delete output file");
            }
        }

        FileInputStream is = null;
        FileOutputStream os = null;
        
        try {
            
            is = new FileInputStream(in);
            os = new FileOutputStream(to);

            byte[] buffer = new byte[ 65535 ];
            int thisdone = 0;

            int read;
            
            while ((read = is.read(buffer)) != -1) {
                
                os.write(buffer, 0, read);
                
                thisdone += read;
                bytesdone += read;

                int done = (int) NumberUtils.getWorkDoneDouble(bytesdone, totalbytes);

                desc(in.getName() + ": " + Utils.getFormatedBytes(thisdone) + " / " + bytes, done);
            }

            os.flush();

        } finally {

            Closer.closeQuietly(is);
            Closer.closeQuietly(os);

        }
    }
    
    /**
     * Creates new form SetupGui
     */
    public SetupGui() {
        
        initComponents();
        
        setLocationRelativeTo(null);
        setIconImage(Resources.getIcon());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bar = new javax.swing.JProgressBar();
        desclabel = new javax.swing.JLabel();
        glabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SkyShield - Instalação");

        bar.setIndeterminate(true);
        bar.setStringPainted(true);

        desclabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        desclabel.setText("--/--");

        glabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        glabel.setText("Aguarde");

        jLabel2.setText("O SkyShield esta concluindo a instalação, isso pode levar alguns minutos.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(desclabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(glabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 162, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(glabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(desclabel)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar bar;
    private javax.swing.JLabel desclabel;
    private javax.swing.JLabel glabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables

}
