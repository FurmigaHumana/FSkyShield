/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.gui;

import java.net.InetAddress;
import me.FurH.Core.executors.TaskExecutor;
import me.FurH.SkyShield.resources.Resources;

/**
 *
 * @author lgpse
 */
public class DnsResolve extends javax.swing.JFrame {

    private boolean connecting = false;
    private final MainGui main;
    
    public DnsResolve() {
        this(null);
    }
    
    public DnsResolve(MainGui main) {
        
        this.main = main;
        initComponents();
        
        setLocationRelativeTo(main);
        setIconImage(Resources.getIcon());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        trylab = new javax.swing.JLabel();
        errorlab = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SkyShield - Conectando");

        jLabel1.setText("Procurando servidor...");

        jProgressBar1.setIndeterminate(true);

        jLabel2.setText("Tentativa:");

        trylab.setText("10 / 10");

        errorlab.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        errorlab.setForeground(new java.awt.Color(204, 0, 0));
        errorlab.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        errorlab.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jButton1.setText("Tentar novamente");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                        .addGap(41, 41, 41)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trylab))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(errorlab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(trylab))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorlab, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        TaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                tryToConnect();
            }
        });
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel errorlab;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel trylab;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setVisible(boolean b) {
        
        if (b && !connecting) {
            return;
        }

        super.setVisible(b);
        this.setLocationRelativeTo(main);
    }

    void tryToConnect() {
        
        if (connecting) {
            return;
        }
        
        connecting = true;
        jButton1.setEnabled(false);

        jProgressBar1.setIndeterminate(true);
        jLabel1.setText("Procurando servidor...");
        
        this.setVisible(main.isVisible());

        try {
            
            int max = 3;

            for (int j1 = 0; j1 < max; j1++) {

                trylab.setText((j1 + 1) + " / " + max);

                try {

                    InetAddress address = InetAddress.getByName("@@REMOVED");                
                    String url = address.getHostAddress();

                    dispose();

                    main.connect(url);

                    return;

                } catch (Throwable ex) {

                    ex.printStackTrace();
                    errorlab.setText(ex.getMessage());

                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) { }
            }

            jButton1.setEnabled(true);

            jProgressBar1.setIndeterminate(false);
            jLabel1.setText("Não foi possível conectar");
            errorlab.setText("Verifique sua internet / ou Skyshield em Manutenção");
        
        } finally {
            
            connecting = false;
            
        }
    }
}
