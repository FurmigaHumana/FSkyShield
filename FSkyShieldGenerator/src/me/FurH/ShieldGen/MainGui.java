/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldGen;

import java.io.File;
import me.FurH.Core.encript.Encrypto;

/**
 *
 * @author lgpse
 */
public class MainGui extends javax.swing.JFrame {
    
    //private final Scanner scanner;
    private final int protocol = 1;
    
    public MainGui() {
        
        initComponents();
        //scanner = new Scanner();

        new FileDrop(this.jLabel1, new FileDrop.Listener() {
            @Override
            public void  filesDropped(java.io.File[] files) {
                
                if (files.length == 1 && files[ 0 ].getName().endsWith(".exe")) {
                    
                    try {
                        System.out.println(Encrypto.hash("MD5", files[ 0 ]));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    return;
                }
                
                for (File dir : files) {
                    if (dir.isDirectory()) {
                        try {
                            generateQuery(dir);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                
                System.out.println("Done");
            }
        });
    }

    private void generateQuery(final File dir) throws Exception {
        
        /*File outdir = new File("@@REMOVED" + dir.getName());
        outdir.mkdirs();
                    
        FileUtils.visitAllFilesAt(new SimpleVisitor() {

            @Override
            public void visit(File file) {

                if (!file.getName().endsWith(".jar")) {
                    return;
                }

                try {


                    String md5 = Encrypto.hash("MD5", file);
                    File output = new File(outdir, file.getName() + "-" + md5 + "-.yml");
                    
                    if (output.exists()) {
                        System.out.println("database is already generated");
                        return;
                    }

                    ArrayList<ZipHash> entries = FileHash.splitHash(file, md5);

                    ConfigLoader config = ConfigManager.getConfigLoader(output, null, true, false);

                    String name = file.getName();
                    name = name.substring(0, name.length() - 4);
                    
                    config.set("Name", name);
                    config.set("Hash", md5);

                    ArrayList<String> strings = new ArrayList<>();
                    for (ZipHash hash : entries) {
                        strings.add(Encrypto.hex(hash.md5));
                    }
                    
                    ArrayList<String> names = new ArrayList<>();
                    for (ZipHash hash : entries) {
                        names.add(hash.name);
                    }
                    
                    config.set("entries", strings);
                    config.set("names", names);
                    config.save();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        }, dir);*/
    }

    public static void main0(String[] args) throws Exception {

        new MainGui().setVisible(true);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        scanBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Drop directory");

        scanBtn.setText("Scan");
        scanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(scanBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scanBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void scanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanBtnActionPerformed
        //scanner.scan();
    }//GEN-LAST:event_scanBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton scanBtn;
    // End of variables declaration//GEN-END:variables

}