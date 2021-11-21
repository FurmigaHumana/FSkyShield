package me.FurH.SkyShield.newscan;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.file.FileUtils;
import me.FurH.JavaPacker.loader.ErrorGui;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_protocol;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_attacherror;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_attachreadywrongtime;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_attachrequestwrongtime;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_clientisbusy;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_errortoinstalldll;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_failedtoinstalldll;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_failedwriteprotocol;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_filelistwrongtime;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_localservererror;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_nativecmderror;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_protocolwrongtime;
import static me.FurH.SkyShield.Constants.ShieldConstants.serror_restarted;
import me.FurH.SkyShield.ShieldClient;
import me.FurH.SkyShield.attacher.AgentWritter;
import me.FurH.SkyShield.attacher.NewAgentClient;
import me.FurH.SkyShield.attacher.NewAgentServer;
import me.FurH.SkyShield.gui.DiagnosticGui;
import me.FurH.SkyShield.packets.newscan.Packet60ResultData;
import me.FurH.SkyShield.packets.newscan.Packet61ResultError;
import me.FurH.SkyShield.packets.newscan.Packet62AttachTo;
import me.FurH.SkyShield.packets.newscan.Packet63ResultCode;
import me.FurH.SkyShield.packets.newscan.Packet67FileList;
import me.FurH.SkyShield.packets.newscan.Packet69AskCertificate;
import me.FurH.SkyShield.packets.newscan.Packet70Certificate;
import me.FurH.SkyShield.packets.scan.Packet3ScanUser;
import me.FurH.SkyShield.scanner.JarSigner;
import me.FurH.SkyShield.win32.NativeShield;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class NewGameScanner {

    private final ShieldClient client;
    private boolean scanning = false;

    private Packet3ScanUser request;
    private ScanStage stage;

    private boolean is64;

    public NewGameScanner(ShieldClient client) {
        this.client = client;
    }
    
    public void scanAllProcess(Packet3ScanUser request) {

        if (scanning) {
            writeError("Uma verificação já esta sendo feita!", error_clientisbusy);
            return;
        }

        scanning = true;
        
        this.request = request;
        client.tray_gui.neutral("O SkyShield esta verificando o seu jogo, aguarde...");

        try {

            stage = ScanStage.CMDLINE;

            byte[] cmdline = NativeShield.readCmdLines(switchBytes(request.remortport[0]), request.key);
            writeResult(cmdline);

        } catch (Throwable ex) {

            catchError("Stage " + stage + " error " + ex.getMessage(), ex, error_nativecmderror);

        }
    }
    
    private int switchBytes(int value) {
        
        int b1 = (value & 0x0000FF00) >> 8;
        int b2 = value & 0x000000FF;
        int port = (b2 << 8) + b1;
        
        return port;
    }
    
    public void attachTo(Packet62AttachTo packet) {

        if (stage != ScanStage.CMDLINE) {
            writeError("Pedido de anexo recebido no momento errado", error_attachrequestwrongtime);
            return;
        }
        
        stage = ScanStage.ATTACH;
        NewAgentServer server;

        try {
            server = client.getNewAgentServer();
        } catch (Throwable ex) {
            catchError("Falha ao criar servidor local " + ex.getMessage(), ex, error_localservererror);
            return;
        }

        try {

            NewAgentClient agent = server.connect(packet.pid);

            if (agent != null) {
                
                agent.cleanup();
                
                is64 = agent.is64;
                sendProtocol(agent, packet);
            }

        } catch (Throwable ex) {
            
            catchError("Falha ao anexar maquina " + ex.getMessage(), ex, error_attacherror);
            
        }        
    }
    
    private void sendProtocol(NewAgentClient client, Packet62AttachTo packet) {

        if (stage != ScanStage.ATTACH) {
            writeError("Pedido de protocolo recebido no momento errado", error_protocolwrongtime);
            return;
            
        }
        
        try {

            client.write(cmd_protocol, new AgentWritter() {
                @Override
                public void write(DataOutputStream dos) throws IOException {
                    dos.writeUTF(packet.protocol);
                    dos.writeUTF(packet.key);
                }
            });

        } catch (IOException ex) {
            
            catchError("Falha ao enviar protocolo " + ex.getMessage(), ex, error_failedwriteprotocol);
            
        }
    }
    
    public void attachIsReady(int protocolindex, String librarypath, UUID sessionId) {

        if (stage != ScanStage.ATTACH) {
            writeError("Anexado no momento errado", error_attachreadywrongtime);
            return;
        }

        stage = ScanStage.ATTACHED;

        if (!installDll(librarypath)) {
            return;
        }

        Packet63ResultCode packet = new Packet63ResultCode();
        
        packet.code = Packet63ResultCode.attached;
        packet.protocolindex = protocolindex;
        packet.sessionId = sessionId;
        
        client.connection.write(packet);
    }
    
    private boolean installDll(String librarypath) {

        try {
            
            File source;

            if (is64) {
                source = new File("bin", "shield.dll");
            } else {
                source = new File("32-bit", "bin" + File.separator + "shield.dll");
            }
            
            String md5 = Encrypto.hash("MD5", source);
            String[] paths = librarypath.split("\\;");

            for (int j1 = paths.length; j1 > 0; j1--) {

                String path = paths[ j1 - 1 ];
                File dll = new File(path, "shield.dll");

                try {

                    if (!dll.exists()) {
                        FileUtils.copyFromTo(source, dll);
                    }

                    if (dll.exists() && Encrypto.hash("MD5", dll).equals(md5)) {
                        return true;
                    }
                    
                } catch (Throwable ex) {
                
                    DiagnosticGui.log("Install error: " + ex.getMessage());
                    
                }
            }

            writeError("Não foi possível instalar a biblioteca", error_failedtoinstalldll);
            return false;
            
        } catch (Throwable ex) {

            ex.printStackTrace();
            this.catchError("Falha ao instalar a biblioteca", ex, error_errortoinstalldll);

        }

        return false;
    }
    
    public void filesResult(String javaver, String javadir, String[] paths) {

        if (stage != ScanStage.ATTACHED) {
            writeError("Resultado de arquivos recebido no momento errado", error_filelistwrongtime);
            return;
        }
        
        stage = ScanStage.FILELISTSENT;

        Packet67FileList packet = new Packet67FileList();
        
        packet.javaver = javaver;
        packet.javadir = javadir;
        packet.files = paths;
        
        client.connection.write(packet);
    }

    private void catchError(String message, Throwable ex, int errorcode) {

        writeError(message, errorcode);

        ErrorGui.error("Error on game data scan", true, false, ex);
    }

    public void writeError(String message, int errorcode) {

        Packet61ResultError packet = new Packet61ResultError();

        packet.errorid = errorcode;
        packet.message = message;

        client.connection.write(packet);

        resultError(packet);
    }

    private void writeResult(byte[] data) {
        
        Packet60ResultData packet = new Packet60ResultData();
        
        packet.uniqueId = request.uniqueId;
        packet.data = data;
        
        client.connection.write(packet);
    }

    public void resultError(Packet61ResultError packet) {

        if (packet.errorid == serror_restarted) {

            DiagnosticGui.log("restarted");
            
        } else {
            
            terminateScan();
        
            if (!packet.message.isEmpty()) {
                client.tray_gui.error(packet.message);
            }
        }
    }

    public void terminateScan() {
        scanning = false;
        request = null;
        stage = null;
    }

    public void askCertificate(Packet69AskCertificate packet) {

        Packet70Certificate result = new Packet70Certificate();
        result.path = packet.path;

        File file = new File(packet.path);
        
        if (!file.exists()) {

            result.signed = false;
            result.sigdata = "filenotfound";

        } else {
            
            try {

                JarSigner signer = new JarSigner();

                result.sigdata = signer.verify(file);
                result.signed = signer.isSigned();

            } catch (Throwable ex) {

                result.signed = false;
                result.sigdata = "error: " + ex.getMessage();
                
                ex.printStackTrace();
            }
        }

        client.connection.write(result);
    }
}