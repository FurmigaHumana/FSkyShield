/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.attacher;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.executors.TimerExecutor;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.util.Utils;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_clientterminated;
import me.FurH.SkyShield.EntryPoint;
import me.FurH.SkyShield.ShieldClient;
import me.FurH.SkyShield.gui.DiagnosticGui;
import me.FurH.SkyShield.packets.scan.Packet42PidOff;
import me.FurH.SkyShield.process.RunPr;
import me.FurH.SkyShield.resources.Resources;
import me.FurH.SkyShield.win32.NativeShield;
import net.bytebuddy.agent.VirtualMachine;

/**
 *
 * @author lgpse
 */
public class NewAgentServer {

    final ShieldClient shield;
    
    private NewAgentClient client;
    private ServerSocket server;
    private File agent;

    public NewAgentServer(ShieldClient shield) {
        this.shield = shield;
    }

    public boolean isIdle() {
        return client == null || !client.isConnected();
    }

    void disconnected(NewAgentClient client) {

        this.client = null;

        DiagnosticGui.log("Pid off: " + client.pid);
        shield.game_scanner.writeError("Cliente terminado", error_clientterminated);

        Packet42PidOff packet = new Packet42PidOff();
        packet.pid = client.pid;
        shield.connection.write(packet);
        
        closeServer();
        
        if (agent != null) {
            agent.delete();
            agent = null;
        }
        
        shield.game_scanner.terminateScan();

        TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                System.gc();
            }
        }, 30, TimeUnit.SECONDS);
    }
    
    private void closeServer() {
        
        if (server != null) {
            
            try {
                server.close();
            } catch (Throwable ex) { }
            
            server = null;
        }
    }

    public NewAgentClient connect(int pid) throws Exception {

        if (client != null && client.isConnected()) {
            return client;
        }

        boolean target64 = NativeShield.isWow64(pid);
        boolean shield64 = EntryPoint.is64();

        if (target64 && !shield64) {
            throw new IOException("Você deve utilizar a versão 64 bits do SkyShield!");
        }
        
        agent = Resources.getNewAgent();
        server = new ServerSocket(0);

        if (target64 && shield64) {
            
            DiagnosticGui.log("Both 64 bits");
 
            VirtualMachine vm = null;

            try {

                vm = VirtualMachine.ForHotSpot.attach(Integer.toString(pid));
                vm.loadAgent(agent.getAbsolutePath(), Integer.toString(server.getLocalPort()) + ":" + agent.getAbsolutePath());

            } finally {
                if (vm != null) {
                    try {
                        vm.detach();
                    } catch (Throwable ex) { }
                }
            }

        } else {

            DiagnosticGui.log("32 bit mode");
            
            File x32 = new File("32-bit");
            String[] ret = null;

            if (x32.exists()) {

                FileUtils.copyFromTo(new File("client.jar"), new File(x32, "client.jar"));

                shield.tray_gui.neutral("Verificação lenta! Instale o java 64 bits!");

                File coords = new File(x32, "launcher.mf");
                String data = pid + ",!|" + server.getLocalPort() + ",!|" + agent.getAbsolutePath();

                FileUtils.setBytesOfFile(coords, Encrypto.aes_encrypt(data.getBytes(Utils.UTF8), Resources.lwpw));

                ret = RunPr.read(new String[] { new File(x32, "SkyShield.exe").getAbsolutePath() }, x32);

                DiagnosticGui.log("X32[ 0 ]: " + ret[ 0 ]);
                DiagnosticGui.log("X32[ 1 ]: " + ret[ 1 ]);
            }

            if (ret == null || !ret[ 0 ].isEmpty() || !ret[ 1 ].isEmpty()) {
                throw new Exception("Você precisa desinstalar o java e instalar a versão correta 64 bits");
            }
        }

        ScheduledFuture<?> timeout = TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                closeServer();
            }
        }, 30, TimeUnit.SECONDS);

        Socket socket = server.accept();
        timeout.cancel(true);

        client = new NewAgentClient(this, socket);
        client.is64 = target64;
        
        client.start();
        client.pid = pid;
        
        return client;
    }
}