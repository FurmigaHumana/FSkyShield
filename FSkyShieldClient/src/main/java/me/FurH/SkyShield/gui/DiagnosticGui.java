/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import me.FurH.Core.executors.TaskExecutor;
import me.FurH.Core.http.HttpUtils;
import me.FurH.Core.time.TimeUtils;
import me.FurH.JavaPacker.loader.ErrorGui;
import me.FurH.SkyShield.ShieldClient;
import me.FurH.SkyShield.connect.ConnectToServer;
import me.FurH.SkyShield.resources.Resources;

/**
 *
 * @author lgpse
 */
public class DiagnosticGui extends javax.swing.JFrame {

    public static final DiagnosticGui logs;
    private boolean testrunning = false;

    private final BrokenGui visgui;
    
    static {
        logs = new DiagnosticGui();
    }

    static void startNow(JFrame relative) {
       
        if (logs.isVisible()) {
            return;
        }
        
        logs.setLocationRelativeTo(relative);
        logs.setVisible(true);
        logs.start();
    }

    public DiagnosticGui() {
        initComponents();
        
        this.visgui = new BrokenGui();
        setIconImage(Resources.getIcon());
    }

    private void ok(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private void start() {
        
        if (!testrunning) {

            ok("Feche qualquer minecraft que esteja aberto e clique em OK");
            jTextArea1.setText("");

            log(ErrorGui.footer.toString());
            log("iniciando...");

            MainGui gui = ShieldClient.shield.main_gui;
            log("SkyShield v" + gui.version + " #" + gui.buildnumber);
            log("Uptime: " + TimeUtils.getTimeDuration(System.currentTimeMillis() - ShieldClient.startuptime));
        }
        
        TaskExecutor.execute(new Runnable() {

            @Override
            public void run() {

                try {

                    TestResult result = testCompatibility();

                    if (result == TestResult.RUNNING) {
                     
                        log("Teste já em execução");
                        
                        return;
                    }

                    if (result == TestResult.FULLY_OFFLINE) {

                        finish(
                                "Sem Conexão",
                                "Você aparenta estar sem internet ou com a conexão muito ruim. Verifique a sua conexão com a internet!",
                                false
                        );

                        return;
                    }

                    if (result == TestResult.SERVER_OFFLINE) {

                        finish(
                                "Servidor Offline",
                                "O servidor do SkyShield aparenta estar offline ou em manutenção, *isso não é um problema no seu computador*, aguarde até que a situação seja normalizada.",
                                false
                        );

                        return;
                    }

                    if (result == TestResult.BROKEN_CHECK) {

                        finish(
                                "Falha na conexão",
                                "Não foi possível se conectar ao servidor do SkyShield, tente desativar temporariamente qualquer antivirus ou firewall instalado e repita o teste, se mesmo assim não funcionar é possível que o sistema esteja em manutenção.",
                                false
                        );

                        return;
                    }
                    
                    if (result == TestResult.DISCONNECTED) {

                        finish(
                                "Desconectado",
                                "O sistema não esta conectado ao servidor, tente reiniciar o computador e executar o teste novamente, se o problem persistir tente desativar temporariamente qualquer antivirus ou firewall instalado, se mesmo assim não funcionar entre em contato com nossa equipe.",
                                false
                        );

                        return;
                    }

                    if (result == TestResult.NONETWORK) {

                        finish(
                                "Erro ao pesquisar processos",
                                "Sistema operacional incompativel, verifique se você esta utilizando um usuario com permissões de administrador e que não seja limitado",
                                false
                        );
                        
                        return;
                    }
                    
                    if (result == TestResult.NOPIDS) {
  
                        finish(
                                "Erro ao encontrar processos",
                                "O sistema não pode encontrar nenhum processo conectado ao servidor, tenha certeza que esta conectado ao servidor e verifique se o minecraft esta no modo administrador, se estiver, desative e execute o teste novamente, se não der certo é provavel que o seu antivirus/firewall esteja bloqueando o acesso, tente desativa-lo.",
                                false
                        );

                        return;
                    }
                    
                    if (result == TestResult.BAD_SYSTEM) {
  
                        finish(
                                "Sistema incompatível",
                                "Seu sistema operacional não atende aos requisitos minimos ou esta corrompido.",
                                false
                        );

                        return;
                    }
                    
                    if (result == TestResult.ANTIVIRUS) {
  
                        finish(
                                "Bloqueado",
                                "O seu antivirus esta impedindo o funcionamento do sistema, informamos também que este meio de bloqueio é antigo e ultrapassado, abrindo brechas para invasão. Recomendamos atualizar o seu antivirus ou substitui-lo por um melhor e mais moderno!",
                                false
                        );

                        return;
                    }

                    if (result == TestResult.COMPATIBLE) {
                        
                        finish("OK", null, true);
                        
                        return;
                    }

                    finish(
                            "Sistema incompativel",
                            "Sistema operacional incompativel, usuário não é administrador ou é limitado, proxy ou vpn ativo, vbs e wmic desativado",
                            false
                    );

                } catch (Throwable ex) {

                    StringWriter writer = new StringWriter();
                    PrintWriter pwriter = new PrintWriter(writer);

                    ex.printStackTrace();
                    ex.printStackTrace(pwriter);

                    log("ERRO:");
                    log(writer.toString());

                    finish(
                            "Erro",
                            "Ocorreu um erro ao concluír o processo: " + ex.getMessage(),
                            false
                    );
                }
            }

        });
    }

    private boolean isOnlineByPing(String host) {
        
        Process p = null;
        
        try {

            String cmd[];
            if (System.getProperty("os.name").startsWith("Windows")) {
                cmd = new String[] { "ping", "-n", "1", host };
            } else {
                cmd = new String[] { "ping", "-c", "1", host };
            }

            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

            return p.exitValue() == 0;

        } catch (Throwable ex) {

            ex.printStackTrace();
            return false;

        } finally {
            
            if (p != null) {
                try {
                    p.destroy();
                } catch (Throwable ex) { }
            }
        }
    }
        
    public TestResult testCompatibility() throws Throwable {

        if (testrunning) {
            return TestResult.RUNNING;
        }
        
        testrunning = true;
        try {
            return testCompatibility0();
        } finally {
            testrunning = false;
        }
    }
    
    private boolean ask(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Diagnóstico", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private TestResult testCompatibility0() throws Throwable {

        ConnectToServer conn = ShieldClient.shield.connection;

        log("Adaptador: " + (conn.socket != null ? conn.socket.name() : "null"));

        if (!conn.connected) {

            log("Verificando estado da conexão...");

            boolean cangoogle = isOnlineByPing("google.com.br");
            log("Acesso ao google.com.br: " + (cangoogle ? "sim" : "não"));

            if (!cangoogle && !ask("Tem certeza que a sua internet esta funcionando e em boas condições?")) {
                return TestResult.FULLY_OFFLINE;
            }

            String svstatus;

            try {
                svstatus = HttpUtils.readString("@@REMOVED");
            } catch (Throwable ex) {
                ex.printStackTrace();
                svstatus = "Erro: " + ex.getMessage();
            }

            log("Acesso ao sc.skycraft.com.br: " + svstatus);

            if (svstatus.equals("Offline")) {

                return TestResult.SERVER_OFFLINE;

            } else {

                try {
                    Desktop.getDesktop().browse(new URI("@@REMOVED"));
                } catch (Throwable ex) { }

                if (!ask("Abrimos o site do skyshield no seu navegador, o site esta carregando normalmente?\n\nCaso o navegador não abrir sozinho, digite este endereço no seu navegador:\nsc.skycraft.com.br\ne verifique se o site abre normalmente.\n\nO site carregou corretamente?")) {
                    return TestResult.BROKEN_CHECK;
                }
            }
        }

        log("Conectado ao servidor: " + (conn.connected ? "sim" : "não"));

        if (!conn.connected) {
            return TestResult.DISCONNECTED;
        }
        
        log("Verificando funcionalidades do sistema...");
        
        ok("Abra o minecraft, entre no servidor e clique em OK");
        
        return TestResult.COMPATIBLE;
    }
    
    public static void goBroken() {
        logs.visgui.setLocationRelativeTo(ShieldClient.shield.main_gui);
        logs.visgui.setVisible(true);
    }

    private TestResult parseResult(List<String> result) {

        for (String line : result) {

            line = line.trim().toLowerCase();
            DiagnosticGui.log("    - " + line);
            
            if (line.contains("skyshield") || line.contains("java")) {
                return TestResult.COMPATIBLE;
            } else {
                return TestResult.ANTIVIRUS;
            }
        }
        
        return TestResult.INVALID;
    }

    private void finish(String reason, String causes, boolean result) {

        log("-------------------");

        log("Diagnostico concluído");
        log("Resultado: " + reason);
        
        if (!result) {
            log("Possiveis causas: " + causes);
            log("!! SISTEMA INOPERANTE !!");
        } else {
            log("!! SISTEMA ATIVO E FUNCIONANDO !!");
        }
        
        log("-------------------");

        if (result) {
            
            JOptionPane.showMessageDialog(logs, "O sistema aparenta estar funcionando corretamente.", "Resultado do diagnóstico", JOptionPane.INFORMATION_MESSAGE);
        
        } else {
            
            JLabel label = new JLabel("@@REMOVED");
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            label.addMouseListener(new MouseListener() {
                
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI("@@REMOVED"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
            
            JOptionPane.showMessageDialog(logs, label, "Resultado do diagnóstico", JOptionPane.ERROR_MESSAGE);
        }

        jButton1.setEnabled(true);
        jButton2.setEnabled(true);
    }

    public static void log(final String str) {
       
        System.out.println(str);

        if (!logs.isVisible()) {
            return;
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                logs.jTextArea1.append(str + "\n");
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Disgnostico do sistema");

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextArea1);

        jButton1.setText("Fechar");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Clique em gerar links e os envie em nosso fórum");

        jButton2.setText("Gerar Links");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Diagnóstico automático");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        PasteGui.generate(this, jTextArea1.getText());
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}