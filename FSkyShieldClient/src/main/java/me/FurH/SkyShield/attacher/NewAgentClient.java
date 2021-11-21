/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.attacher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import me.FurH.Core.close.Closer;
import me.FurH.Core.executors.TimerExecutor;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_error;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_filetransport;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_postloaded;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_ready;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_sendfilelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_sendmodulelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_sendproceslist;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_shotdata;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_shoterror;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_channellost;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_disconnected;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_errfilelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_failedinject;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_invalidcmd;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_noprotocol;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_payloadread;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askfilelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_modulelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_processlist;
import me.FurH.SkyShield.packets.newscan.Packet73ClientPayload;
import me.FurH.SkyShield.packets.newscan.Packet74Postload;

/**
 *
 * @author lgpse
 */
public class NewAgentClient extends Thread {

    private final NewAgentServer server;
    private final Socket socket;

    private DataOutputStream dos;
    private DataInputStream dis;
    
    private HashMap<String, HashSet<String>> postloaded;
    private ScheduledFuture<?> postupdate;

    private boolean connected = false;
    
    int pid;
    public boolean is64;

    NewAgentClient(NewAgentServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }
    
    public void cleanup() {
     
        if (postupdate != null) {
            postupdate.cancel(true);
        }
        
        if (postloaded != null) {
            postloaded = null;
        }
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    public void write(int cmd, AgentWritter handler) throws IOException {
        dos.writeInt(cmd);
        handler.write(dos);
        dos.flush();
    }
    
    public void sendRawCommand(int cmd) throws IOException {
        dos.writeInt(cmd);
        dos.flush();
    }
    
    private void dispose() {

        Closer.closeQuietly(dos);
        Closer.closeQuietly(dis);

        try {
            socket.close();
        } catch (Throwable ex) { }
    }
    
    @Override
    public void start() {
        
        connected = true;

        try {
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setDaemon(true);
        setName("Agent Client Input");
        
        super.start();
    }
    
    private void handleError(int code) {
        
        switch (code) {
            case error_noprotocol: {
                server.shield.game_scanner.writeError("Nenhum protocolo disponível", code);
                return;
            }
            case error_failedinject: {
                server.shield.game_scanner.writeError("Falha ao injetar pacote", code);
                return;
            }
            case error_channellost: {
                server.shield.game_scanner.writeError("Não foi possível injetar a tempo", code);
                return;
            }
            case error_disconnected: {
                server.shield.game_scanner.writeError("Desconectado do servidor", code);
                return;
            }
            case error_payloadread: {
                server.shield.game_scanner.writeError("Não foi possível ler o pacote", code);
                return;
            }
            case error_errfilelist: {
                server.shield.game_scanner.writeError("Falha processar lista de classes", code);
                return;
            }
            case error_invalidcmd: {
                server.shield.game_scanner.writeError("O comando recebido é desconhecido", code);
                return;
            }
            default: {
                server.shield.game_scanner.writeError("Erro desconhecido", code);
                break;
            }
        }
    }
    
    @Override
    public void run() {

        try {
            
            dis = new DataInputStream(socket.getInputStream());
            
            while (!socket.isClosed()) {

                int action = dis.readInt();

                switch (action) {
                    case cmd_error: {
                        
                        int error_code = dis.readInt();
                        handleError(error_code);
                        
                        break;
                    }
                    case cmd_ready: {

                        int protocolindex = dis.readInt();
                        String librarypath = dis.readUTF();
                        UUID sessionId = UUID.fromString(dis.readUTF());

                        server.shield.game_scanner.attachIsReady(protocolindex, librarypath, sessionId);
                        
                        break;
                    }
                    case cmd_filetransport: {
                        
                        String javadir = dis.readUTF();
                        String javaver = dis.readUTF();
                        
                        int size = dis.readInt();
                        String[] paths = new String[ size ];

                        for (int j1 = 0; j1 < size; j1++) {
                            paths[j1] = dis.readUTF();
                        }

                        server.shield.game_scanner.filesResult(javaver, javadir, paths);
                        
                        break;
                    }
                    case cmd_shoterror: {
                        
                        String error = dis.readUTF();
                        
                        long bits1 = dis.readLong();
                        long bits2 = dis.readLong();
                        
                        UUID uuid = new UUID(bits1, bits2);
                        
                        server.shield.screenshot.shotError(pid, error, uuid);
                        
                        break;
                    }
                    case cmd_shotdata: {

                        int size = dis.readInt();

                        byte[] data = new byte[ size ];
                        dis.readFully(data);
                       
                        long bits1 = dis.readLong();
                        long bits2 = dis.readLong();
                        
                        UUID uuid = new UUID(bits1, bits2);
                        
                        server.shield.screenshot.uploadShot(pid, data, uuid);
                        
                        break;
                    }
                    case cmd_sendproceslist: {
                        
                        int size = dis.readInt();

                        byte[] data = new byte[ size ];
                        dis.readFully(data);
                        
                        writeClientPayload(payload_processlist, data);
                        
                        break;
                    }
                    case cmd_sendmodulelist: {
                        
                        int size = dis.readInt();

                        byte[] data = new byte[ size ];
                        dis.readFully(data);
                        
                        writeClientPayload(payload_modulelist, data);
                        
                        break;
                    }
                    case cmd_sendfilelist: {
                       
                        int size = dis.readInt();

                        byte[] data = new byte[ size ];
                        dis.readFully(data);
                        
                        writeClientPayload(payload_askfilelist, data);
                        
                        break;
                    }
                    case cmd_postloaded: {

                        String className = "";
                        String path = "";
                        String path2 = "";

                        if (dis.readBoolean()) {
                            className = dis.readUTF();
                        }
                        
                        if (dis.readBoolean()) {
                            path = dis.readUTF();
                        }
                        
                        if (dis.readBoolean()) {
                            path2 = dis.readUTF();
                        }
                        
                        appendPostload(path, className);
                        appendPostload(path2, className);
                        
                        break;
                    }
                }
            }

        } catch (Throwable ex) {
            
            ex.printStackTrace();
            
        } finally {

            server.disconnected(this);
            
            connected = false;
            dispose();
        }
    }
    
    private void appendPostload(String path, String className) {
        
        if (postloaded == null) {
            postloaded = new HashMap<>();
        }

        HashSet<String> list = postloaded.get(path);
        
        if (list == null) {
            list = new HashSet<>();
            postloaded.put(path, list);
        }
        
        if (list.add(className)) {

            if (postupdate != null) {
                postupdate.cancel(true);
            }

            postupdate = TimerExecutor.schedule(new Runnable() {
               
                @Override
                public void run() {
                    
                    Packet74Postload packet = new Packet74Postload();
                    packet.postloaded = postloaded;
                    postloaded = null;
                    
                    server.shield.connection.write(packet);
                }
            }, 5, TimeUnit.SECONDS);
        }
    }
    
    private void writeClientPayload(int cmd, byte[] data) {
        
        Packet73ClientPayload result = new Packet73ClientPayload();
        
        result.action = cmd;
        result.data = data;
        
        server.shield.connection.write(result);
    }
}