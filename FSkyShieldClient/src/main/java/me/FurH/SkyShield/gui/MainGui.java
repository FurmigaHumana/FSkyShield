/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.gui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import me.FurH.Core.config.FProperties;
import me.FurH.Core.executors.TaskExecutor;
import me.FurH.Core.executors.TimerExecutor;
import me.FurH.Core.http.HttpUtils;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.time.TimeUtils;
import me.FurH.JavaPacker.loader.ErrorGui;
import me.FurH.SkyShield.ShieldClient;
import me.FurH.SkyShield.resources.Resources;

/**
 *
 * @author lgpse
 */
public class MainGui extends javax.swing.JFrame {

    public final NickList nickList;
    final ShieldClient client;
    
    public final double version;
    public final int buildnumber;
    private boolean reading = false;
    
    private DnsResolve dnsgui;
    private boolean callconn = false;
    
    public MainGui() throws IOException {
        this(null);
    }

    public MainGui(ShieldClient client) throws IOException {

        this.client = client;
        
        setLocationRelativeTo(null);
        Properties properties = new Properties();
        
        try {
            properties.load(MainGui.class.getResourceAsStream("/version.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        version     = Double.parseDouble(properties.getProperty("version"));
        buildnumber = NumberUtils.toInteger(properties.getProperty("buildnumber"));

        initComponents();
        
        jLabel1.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(225,225,225)));
        jPanel4.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(225,225,225)));

        setIconImage(Resources.getIcon());
        String label = "SkyShield v" + version + " #" + buildnumber;

        System.err.println(label);
        this.title.setText(label);
        
        FProperties props = client.getFileProps();

        accept.setSelected(Boolean.parseBoolean(props.getProperty("autoupload", "true")));
        notifications.setSelected(Boolean.parseBoolean(props.getProperty("notifications", "true")));
        updateCheck.setSelected(Boolean.parseBoolean(props.getProperty("updateCheck", "true")));
        
        StyledDocument doc = subLabel.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        
        jScrollPane1.setBorder(null);

        nickList = new NickList(this);
    }
    
    public void connected() throws Exception {
        if (nickList.load()) {
            setVisible(true);
            nickList.setLocationRelativeTo(this);
            nickList.setVisible(true);
        }
    }
    
    public void connect(String url) throws Exception {
        
        if (callconn) {
            return;
        }
        
        callconn = true;
        
        dnsgui = null;
        client.connect(url);
    }
    
    public void connect() {

        if (dnsgui == null) {
            dnsgui = new DnsResolve(this);
        }

        dnsgui.tryToConnect();
    }
    
    public boolean isAutoAccept() {
        return accept.isSelected();
    }
    
    public boolean isNotificationSet() {
        return notifications.isSelected();
    }
    
    public boolean isAutoUpdate() {
        return updateCheck.isSelected();
    }
    
    @Override
    public void toFront() {
        
        super.toFront();
        
        if (dnsgui != null) {
            dnsgui.toFront();
        }
    }
    
    @Override
    public void setVisible(boolean b) {

        if (b) {

            titleLabel  .setText("...");
            titleLabel  .setForeground(Color.black);
            subLabel    .setText("");

            readOnlineMethod(true);
        }

        super.setVisible(b);
        
        if (dnsgui != null) {
            dnsgui.setVisible(b);
        }
    }
    
    public void readOnlineMethod(boolean force) {

        if (!force && !isVisible()) {
            return;
        }

        if (reading) {
            return;
        }

        reading = true;

        TaskExecutor.execute(new Runnable() {
            
            @Override
            public void run() {

                try {

                    List<String> input = HttpUtils.read("@@REMOVED");

                    if (input.size() == 1 && input.get(0).equals("Offline")) {

                        errorLabel("Sistema Offline", "estamos em manutenção, voltaremos online em breve");

                        TimerExecutor.schedule(new Runnable() {
                            @Override
                            public void run() {
                                readOnlineMethod(false);
                            }
                        }, 1, TimeUnit.MINUTES);

                    } else {

                        boolean connected = client.connection.connected;

                        titleLabel.setText(connected ? input.get(0) : "Desconectado");

                        if (connected) {
                            titleLabel.setForeground(new Color(0,153,0));
                        } else {
                            titleLabel.setForeground(new Color(204,0,0));
                        }

                        subLabel.setText(input.get(1).replaceAll(";n;", "\n"));

                        TimerExecutor.schedule(new Runnable() {
                            @Override
                            public void run() {
                                readOnlineMethod(false);
                            }
                        }, 10, TimeUnit.MINUTES);
                    }

                } catch (Throwable ex) {

                    ex.printStackTrace();
                    errorLabel("ERRO", ex.getMessage());

                } finally {
                    
                    reading = false;
                    
                }
            }
        });
    }

    private void errorLabel(final String msg, final String subtitle) {

        subLabel.setText(subtitle == null ? msg : subtitle);

        titleLabel.setText(msg);
        titleLabel.setForeground(new Color(204, 0, 0));
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        subLabel = new javax.swing.JTextPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        genrel = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        notifications = new javax.swing.JCheckBoxMenuItem();
        accept = new javax.swing.JCheckBoxMenuItem();
        updateCheck = new javax.swing.JCheckBoxMenuItem();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SkyShield");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui.png"))); // NOI18N
        jLabel1.setOpaque(true);

        jLabel4.setForeground(new java.awt.Color(124, 124, 124));
        jLabel4.setText("Copyright © 2019 SkyCraft. Todos os direitos reservados. ");

        jButton3.setText("Minimizar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jButton3))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(250, 250, 250));

        title.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("SkyShield V2.0 #500");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Desenvolvido por FurmigaHumana para a rede SkyCraft");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titleLabel.setForeground(new java.awt.Color(204, 0, 0));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("...");
        titleLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        subLabel.setEditable(false);
        subLabel.setBorder(null);
        subLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(subLabel);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenu2.setText("Ações");

        jMenuItem4.setText("Diagnosticar");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        genrel.setText("Gerar relatório");
        genrel.setEnabled(false);
        genrel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genrelActionPerformed(evt);
            }
        });
        jMenu2.add(genrel);
        jMenu2.add(jSeparator3);

        jMenuItem3.setText("Atualizar");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);
        jMenu2.add(jSeparator2);

        jMenuItem1.setText("Fechar totalmente");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        jMenu.setText("Opções");
        jMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuActionPerformed(evt);
            }
        });

        jMenuItem5.setText("Gerenciar nicks");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu.add(jMenuItem5);

        notifications.setSelected(true);
        notifications.setText("Exibir notificações do sistema");
        jMenu.add(notifications);

        accept.setSelected(true);
        accept.setText("Sempre aceitar envio de amostras");
        accept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptActionPerformed(evt);
            }
        });
        jMenu.add(accept);

        updateCheck.setSelected(true);
        updateCheck.setText("Verificar atualizações automáticamente");
        updateCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateCheckActionPerformed(evt);
            }
        });
        jMenu.add(updateCheck);

        jMenuBar1.add(jMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuActionPerformed
        saveAll();
    }//GEN-LAST:event_jMenuActionPerformed

    private void acceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptActionPerformed
        saveAll();
    }//GEN-LAST:event_acceptActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        new File("host.dat").delete();

        TaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                client.up_gui.checkUpdates(false);
            }
        });
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

        if (!TimeUtils.isExpired(ShieldClient.startuptime, 2, TimeUnit.MINUTES)) {
            JOptionPane.showMessageDialog(this, "Aguarde ao menos 2 minutos para realizar este teste", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (TimeUtils.isExpired(ShieldClient.startuptime, 3, TimeUnit.HOURS)) {
            JOptionPane.showMessageDialog(this, "Aplicativo aberto a mais de 3 horas, re-inicie e tente novamente", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        genrel.setEnabled(true);
        DiagnosticGui.startNow(this);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void updateCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateCheckActionPerformed
        saveAll();
    }//GEN-LAST:event_updateCheckActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        nickList.setLocationRelativeTo(this);
        nickList.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void genrelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genrelActionPerformed
        ErrorGui.errorlines("Relatório de funcionamento", true, false, "Clique no botão 'Gerar relatório' e envie o conteudo para o administrador!");
    }//GEN-LAST:event_genrelActionPerformed

    public void saveAll() {
        
        try {
            
            FProperties props = client.getFileProps();
            
            props.setProperty("autoupload", Boolean.toString(accept.isSelected()));
            props.setProperty("notifications", Boolean.toString(notifications.isSelected()));
            props.setProperty("updateCheck", Boolean.toString(updateCheck.isSelected()));
            
            props.store("Não edite este arquivo manualmente!");
            
        } catch (IOException ex) {
            
            ErrorGui.error("Error on properties file update", true, false, ex);
            
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem accept;
    private javax.swing.JMenuItem genrel;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JCheckBoxMenuItem notifications;
    private javax.swing.JTextPane subLabel;
    private javax.swing.JLabel title;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JCheckBoxMenuItem updateCheck;
    // End of variables declaration//GEN-END:variables

}
