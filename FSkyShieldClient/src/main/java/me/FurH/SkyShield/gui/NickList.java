/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.util.Utils;
import me.FurH.SkyShield.nicks.NickEntry;
import me.FurH.SkyShield.nicks.NickManager;
import me.FurH.SkyShield.packets.nicks.Packet77NickOpen;
import me.FurH.SkyShield.packets.nicks.Packet78IdList;
import me.FurH.SkyShield.packets.nicks.Packet79AddNick;
import me.FurH.SkyShield.resources.Resources;

/**
 *
 * @author lgpse
 */
public class NickList extends javax.swing.JFrame {
    
    private final NickManager nickManager;
    private final JPopupMenu menu;
    private final MainGui maingui;
    
    private String namekey;

    public NickList() {
        this(null);
    }

    public NickList(MainGui maingui) {
        
        this.nickManager = new NickManager();
        this.maingui = maingui;
        
        initComponents();
        setLocationRelativeTo(maingui);
        setIconImage(Resources.getIcon());
    
        menu = new JPopupMenu();
        
        JMenuItem remove = new JMenuItem("Remover nick");

        remove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                for (String name : nameList.getSelectedValuesList()) {
                    nickManager.remove(name);
                }
                
                update(true);
            }
        });
        
        menu.add(remove);
    }
    
    public void nickOpen(Packet77NickOpen packet) {

        if (namekey == null) {
            
            progress.setString("Pronto!");
            toggleFields(true);

            setIndeterminate(false);
        }

        this.namekey = packet.key;
    }
    
    private void setIndeterminate(boolean value) {
        try {
            progress.setIndeterminate(value);
        } catch (Throwable ex) { }
    }
    
    @Override
    public void setVisible(boolean value) {

        if (value) {
            Packet77NickOpen open = new Packet77NickOpen();
            maingui.client.connection.write(open);
        }

        super.setVisible(value);
    }

    /*public ArrayList<NickEntry> getNames() {
        return names;
    }
    
    public boolean isName(String username) {

        for (String name : names) {
            if (name.equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }*/
    
    public boolean has(int i) {
        return nickManager.has(i);
    }
    
    public boolean load() {

        nickManager.loadAll();
                
        if (nickManager.isEmpty()) {
            return true;
        }
        
        update(false);

        return false;
    }

    private void update(boolean saveall) {

        NickEntry[] namearr = nickManager.getList();
        nameList.setListData(nickManager.getNameList());

        if (saveall) {
            nickManager.saveAll();
        }

        sendUpdate(namearr);
    }
    
    public void sendUpdate(NickEntry[] namearr) {

        HashSet<Integer> ids = new HashSet<>();

        if (namearr == null) {
            namearr = nickManager.getList();
        }

        for (NickEntry entry : namearr) {
            ids.addAll(Arrays.asList(entry.ids));
        }
        
        Packet78IdList namelist = new Packet78IdList();
        namelist.ids = ids.toArray(new Integer[0]);
        maingui.client.connection.write(namelist);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        nameList = new javax.swing.JList<>();
        namefield = new javax.swing.JTextField();
        addbtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        pwfield = new javax.swing.JPasswordField();
        progress = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SkyShield - Nicks");

        nameList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                nameListMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(nameList);

        namefield.setEnabled(false);
        namefield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                namefieldKeyReleased(evt);
            }
        });

        addbtn.setText("Adicionar");
        addbtn.setEnabled(false);
        addbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addbtnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Cadastro de nicks");

        jLabel2.setText("Adicione abaixo todos os nomes que pretende usar no servidor.");

        jLabel3.setText("Nick no servidor:");

        jLabel4.setText("Senha de login:");

        jLabel5.setText("Use a mesma senha de /login, se sua conta for original cadastre uma senha pelo comando /registrar");

        pwfield.setEnabled(false);
        pwfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pwfieldKeyReleased(evt);
            }
        });

        progress.setIndeterminate(true);
        progress.setString("Carregando...");
        progress.setStringPainted(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(namefield)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pwfield))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addbtn))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(1, 1, 1)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namefield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addbtn)
                    .addComponent(pwfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void toggleFields(boolean value) {
        addbtn      .setEnabled(value);
        pwfield     .setEnabled(value);
        namefield   .setEnabled(value);
    }
    
    private void addbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbtnActionPerformed

        try {
            
            if (namekey == null) {
                return;
            }
            
            toggleFields(false);
            
            setIndeterminate(true);
            progress.setString("Adicionando nick...");
            
            String name = namefield.getText();
            char[] pwarr = pwfield.getPassword();
            
            if (name.length() <= 2 || pwarr.length <= 2) {
                error("Nome ou senha muito curtas!");
                return;
            }

            byte[] pw = Encrypto.aes_encrypt(new String(pwarr).getBytes(Utils.UTF8), namekey + name);
            Arrays.fill(pwarr, (char) 0); // ha this is useless

            Packet79AddNick packet = new Packet79AddNick();
            
            packet.name = name;
            packet.pw = pw;
            
            maingui.client.connection.write(packet);

        } catch (Exception ex) {

            error("Erro " + ex.getMessage());
            ex.printStackTrace();

        }
    }//GEN-LAST:event_addbtnActionPerformed

    private void error(String reply) {
        JOptionPane.showMessageDialog(this, reply, "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
    public void addNick(Packet79AddNick packet) {

        toggleFields(true);

        setIndeterminate(false);
        progress.setString("-");

        if (packet.ids == null) {
            
            error(packet.reply);
            
        } else {

            namefield.setText("");
            pwfield.setText("");

            final boolean empty = nickManager.isEmpty();

            nickManager.add(new NickEntry(packet.name, packet.ids));
            update(true);

            if (empty) {
                JOptionPane.showMessageDialog(this, "Pode fechar a janela quando terminar de adicionar os nomes", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void nameListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nameListMouseReleased

        if (evt.isPopupTrigger()) {

            int selindex = nameList.locationToIndex(evt.getPoint());
            if (selindex != nameList.getSelectedIndex()) {
                nameList.setSelectedIndex(selindex);
            }

            menu.show(nameList, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_nameListMouseReleased

    private void namefieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_namefieldKeyReleased
        pwfieldKeyReleased(evt);
    }//GEN-LAST:event_namefieldKeyReleased

    private void pwfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pwfieldKeyReleased
        if (evt != null && evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addbtnActionPerformed(null);
        }
    }//GEN-LAST:event_pwfieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addbtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JList<String> nameList;
    private javax.swing.JTextField namefield;
    private javax.swing.JProgressBar progress;
    private javax.swing.JPasswordField pwfield;
    // End of variables declaration//GEN-END:variables

}
