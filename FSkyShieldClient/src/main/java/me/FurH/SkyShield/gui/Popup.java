/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.gui;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import me.FurH.Core.executors.TimerExecutor;

/**
 *
 * @author lgpse
 */
public class Popup extends javax.swing.JFrame {

    private static final ArrayDeque<Popup> queue;
    private static ScheduledFuture<?> task;
    private static Popup current;

    static {
        queue = new ArrayDeque<>();
    }
    private long time = 5;
    
    public Popup() {
        
        initComponents();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
       
        int x = (int) rect.getMaxX() - getWidth();
        int y = (int) rect.getMaxY() - getHeight();
        
        setLocation(x, y - 30);
        
        jPanel2.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(130, 130, 130)));
    }
    
    private static Popup newPopup(String titlstr, String msg) {
        
        Popup popup = new Popup();

        popup.title.setText(titlstr);
        popup.text.setText(msg);
        
        queue.add(popup);
        
        return popup;
    }

    public static void success(String titlstr, String msg) {

        if (titlstr == null) {
            titlstr = "Sucesso";
        }
        
        Popup popup = newPopup(titlstr, msg);
        
        popup.jPanel1   .setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(69, 122, 26)));
        popup.jPanel1   .setBackground(new Color(93, 164, 36));
        popup.title     .setForeground(new Color(0, 51, 0));
        
        displayNext();
    }

    public static void error(String titlstr, String msg) {

        if (titlstr == null) {
            titlstr = "Erro";
        }

        Popup popup = newPopup(titlstr, msg);
        popup.time  = 30;

        popup.jPanel1   .setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(159, 12, 12)));
        popup.jPanel1   .setBackground(new Color(203, 17, 17));
        popup.title     .setForeground(new Color(51, 0, 0));
        
        displayNext();
    }
    
    public static void neutral(String titlstr, String msg) {

        if (titlstr == null) {
            titlstr = "Aviso";
        }
        
        Popup popup = newPopup(titlstr, msg);

        popup.jPanel1   .setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(34, 132, 161)));
        popup.jPanel1   .setBackground(new Color(43, 166, 203));
        popup.title     .setForeground(new Color(0, 0, 51));

        displayNext();
    }

    private static synchronized void displayNext() {

        if (current != null && current.isVisible()) {
            return;
        }
        
        if (task != null) {
            task.cancel(true);
        }
        
        Popup next = queue.pollFirst();
        if (next == null) {
            return;
        }
        
        task = TimerExecutor.schedule(new Runnable() {
           
            @Override
            public void run() {

                try {
                    
                    current.dispose();
                    current = null;
                    
                } catch (Throwable ex) {
                    
                    ex.printStackTrace();
                    
                } finally {
                    
                    displayNext();
                    
                }
            }
        }, next.time, TimeUnit.SECONDS);
        
        current = next;
        current.setVisible(true);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        title = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        text = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(255, 255, 255));
        setFocusCycleRoot(false);
        setFocusable(false);
        setFocusableWindowState(false);
        setForeground(java.awt.Color.black);
        setUndecorated(true);
        setType(java.awt.Window.Type.POPUP);

        jPanel1.setBackground(new java.awt.Color(53, 170, 71));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 27, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton1.setText("x");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        title.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        title.setForeground(new java.awt.Color(51, 0, 0));
        title.setText("...");

        text.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        text.setText("...");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jSeparator1)
                    .addComponent(text, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
        displayNext();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel text;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}
