/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a;

import a.protocol.ProtocolOption;
import a.protocol.channel.AByteBuf;
import a.protocol.channel.NettyChannel;
import a.shot.IShotResult;
import a.shot.Screenshot;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import me.FurH.SkyShield.Constants.FileHash;
import me.FurH.SkyShield.Constants.HashProvider;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_error;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_filetransport;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_postloaded;
import static me.FurH.SkyShield.Constants.ShieldConstants.cmd_protocol;
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
import static me.FurH.SkyShield.Constants.ShieldConstants.error_hashfiled;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_invalidcmd;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_noprotocol;
import static me.FurH.SkyShield.Constants.ShieldConstants.error_payloadread;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askfilelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askhash;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askid;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_askziphash;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_fileinfo;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_filelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_handshake;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_modulelist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_processlist;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_takeshot;
import static me.FurH.SkyShield.Constants.ShieldConstants.payload_ziphash;
import me.FurH.SkyShield.win32.NativeShield;

/**
 *
 * @author lgpse
 */
public class Agent implements ClassFileTransformer, Runnable, Handler, IShotResult {
    
    private static final String sessionid = UUID.randomUUID().toString();

    public static final ExecutorService executor0 = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });
    
    private static final Agent instance;
    private static boolean initall = false;
    private static Instrumentation instr;

    static {
        instance = new Agent();
    }

    private boolean connected = false;
    private DataOutputStream dos;
    private DataInputStream dis;
    private Socket socket;
    
    private ByteArrayOutputStream baos;
    private ProtocolOption result;
    
    private String currentKey;
    
    private void connect(int port) throws IOException {
        
        if (connected) {
            return;
        }

        dispose();
       
        socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", port));
        
        connected = true;
        
        Thread t1 = new Thread(this);
        t1.setDaemon(true);
        t1.start();

        dos = new DataOutputStream(socket.getOutputStream());
    }
    
    private void dispose() {
        
        connected = false;
        result = null;
                
        try {
            dos.close();
            dos = null;
        } catch (Throwable ex) { }
        
        try {
            dis.close();
            dis = null;
        } catch (Throwable ex) { }
        
        try {
            socket.close();
            socket = null;
        } catch (Throwable ex) { }
    }
    
    @Override
    public void run() {
        
        try {

            dis = new DataInputStream(socket.getInputStream());

            while (!socket.isClosed()) {

                int cmd = dis.readInt();
                
                switch (cmd) {
                    
                    case cmd_protocol: {

                        String protocol = dis.readUTF();
                        currentKey = dis.readUTF();
                        
                        inject(protocol);

                        break;
                    }
                }
            }

        } catch (Throwable ex) {

            print(ex);

        } finally {

            connected = false;
            dispose();
        }
    }
    
    public static void print(Throwable ex) {
//        ex.printStackTrace();
    }
    
    private void writeId(final String key) {

        this.writePayload(new WriteCommand() {
            
            @Override
            public void write(DataOutputStream dos) throws Throwable {
                
                byte[] data = NativeShield.hardwareId(key);
                
                dos.writeInt(payload_askid);
                dos.writeInt(data.length);
                dos.write(data);
            }
        });
    }
    
    private ClassLoader getClassLoader() {
        
        try {

            Class<?> cls = Class.forName("@@REMOVED");

            Field f = cls.getDeclaredField("classLoader");
            f.setAccessible(true);

            return (ClassLoader) f.get(null);
            
        } catch (Throwable ex) {
            print(ex);
        }

        return null;
    }
    
    private void inject(String protocol) {

        try {
                        
            String[] inputs = protocol.split("\n");
            ArrayList<ProtocolOption> options = new ArrayList<ProtocolOption>();
            
            for (String input : inputs) {
                options.add(new ProtocolOption(input));
            }
            
            this.result = null;
            int j1 = 0;

            ClassLoader loader = getClassLoader();
            
            for (; j1 < options.size(); j1++) {
                ProtocolOption test = options.get(j1);
                if (test.isSupported(loader)) {
                    result = test;
                    break;
                }
            }
            
            if (result == null) {
                writeErrorCode(error_noprotocol);
                return;
            }
            
            final int protindex = j1;

            writeCommand(cmd_ready, new WriteCommand() {
                @Override
                public void write(DataOutputStream dos) throws Throwable {
                    dos.writeInt(protindex);
                    dos.writeUTF(System.getProperty("@@REMOVED"));
                    dos.writeUTF(sessionid);
                }
            });
            
            result.injectChannel(this);
            
        } catch (Exception ex) {
            
            writeErrorCode(error_failedinject);
            print(ex);
            
        }
    }

    @Override
    public void payload(NettyChannel channel, String tag, AByteBuf byteBuf) {

        try {

            BuffStream stream = new BuffStream(byteBuf);
            DataInputStream badis = new DataInputStream(stream);

            int cmd = badis.readInt();

            switch (cmd) {
                case payload_filelist: {
                    sendFileList();
                    return;
                }
                case payload_askhash: {
                    
                    String key = badis.readUTF();
                    int size = badis.readInt();
                    
                    byte[] data = new byte[ size ];
                    badis.readFully(data);

                    writeHash(key, data);

                    return;
                }
                case payload_askid: {

                    String key = badis.readUTF();
                    writeId(key);

                    return;
                }
                case payload_takeshot: {
                    
                    long bits1 = badis.readLong();
                    long bits2 = badis.readLong();

                    Screenshot.screenshot(instr, bits1, bits2, this);

                    return;
                }
                case payload_processlist: {

                    String key = badis.readUTF();
                    final byte[] data = NativeShield.readProcessList(key);

                    writeCommand(cmd_sendproceslist, new WriteCommand() {
                        @Override
                        public void write(DataOutputStream dos) throws Throwable {
                            dos.writeInt(data.length);
                            dos.write(data);
                        }
                    });
                    
                    return;
                }
                case payload_modulelist: {

                    String key = badis.readUTF();
                    final byte[] data = NativeShield.listModules(key);

                    writeCommand(cmd_sendmodulelist, new WriteCommand() {
                        @Override
                        public void write(DataOutputStream dos) throws Throwable {
                            dos.writeInt(data.length);
                            dos.write(data);
                        }
                    });
                    
                    return;
                }
                case payload_askfilelist: {
                    
                    String key = badis.readUTF();
                    int size = badis.readInt();
                    
                    byte[] path = new byte[ size ];
                    badis.readFully(path);

                    final byte[] encrypted = NativeShield.fileList(key, path);

                    writeCommand(cmd_sendfilelist, new WriteCommand() {
                        @Override
                        public void write(DataOutputStream dos) throws Throwable {
                            dos.writeInt(encrypted.length);
                            dos.write(encrypted);
                        }
                    });
                    
                    return;
                }
                case payload_askziphash: {

                    final String path   = badis.readUTF();
                    final String key1   = badis.readUTF();
                    final String key2   = badis.readUTF();
                    
                    int size = badis.readInt();
                    
                    final byte[] enc = new byte[ size ];
                    badis.readFully(enc);

                    writePayload(new WriteCommand() {
                       
                        @Override
                        public void write(DataOutputStream dos) throws Throwable {

                            Throwable exc = null;
                            
                            for (int j1 = 0; j1 < 3; j1++) {
                               
                                try {
                                    
                                    File file = new File(path);

                                    byte[] hash = FileHash.zipHash(file, new HashProvider() {
                                        @Override
                                        public byte[] hash(byte[] data) throws Exception {
                                            return NativeShield.md5(data, key1);
                                        }
                                    });

                                    byte[] encrypted = NativeShield.encryptResult(enc, hash, key2);

                                    dos.writeInt(payload_ziphash);
                                    dos.writeInt(encrypted.length);
                                    dos.write(encrypted);
                                    
                                    break;
                                    
                                } catch (Throwable ex) {

                                    if (exc == null) {
                                        exc = ex;
                                    }

                                    print(ex);
                                    Thread.sleep(2000);
                                }
                            }
                            
                            if (exc != null) {
                                throw exc;
                            }
                        }
                    });
                    
                    return;
                }
                default: {
                    
                    writeErrorCode(error_invalidcmd);
                    break;
                }
            }

        } catch (Throwable ex) {

            writeErrorCode(error_payloadread);
            print(ex);

        }
    }

    private void writeHash(final String key, final byte[] path) {

        writePayload(new WriteCommand() {
            
            @Override
            public void write(DataOutputStream dos) throws Throwable {

                byte[] hash = NativeShield.fileInfo(key, path);

                dos.writeInt(payload_fileinfo);
                dos.writeInt(hash.length);
                dos.write(hash);
            }
        });
    }

    @Override
    public void ready() {
        handshakePayload();
    }

    @Override
    public void failed() {
        writeErrorCode(error_channellost);
    }

    @Override
    public void disconnected() {
        writeErrorCode(error_disconnected);
        result = null;
    }

    private void handshakePayload() {
        
        writePayload(new WriteCommand() {
           
            @Override
            public void write(DataOutputStream dos) throws Throwable {
                
                byte[] pid = NativeShield.currentPid(currentKey);
                currentKey = null;

                dos.writeInt(payload_handshake);
                dos.writeInt(pid.length);
                dos.write(pid);
            }
        });
    }
    
    private void execute(final Runnable runnable) {
        
        if (!connected) {
            return;
        }
        
        executor0.execute(new Runnable() {
            @Override
            public void run() {
                if (connected) {
                    runnable.run();
                }
            }
        });
    }
    
    private void writePayload(final WriteCommand writter) {

        execute(new Runnable() {
           
            @Override
            public void run() {
                                
                try {
                    
                    if (result == null) {
                        return;
                    }
                    
                    if (baos == null) {
                        baos = new ByteArrayOutputStream();
                    }
                    
                    baos.reset();
                    
                    DataOutputStream dos = new DataOutputStream(baos);
                    writter.write(dos);
                    dos.flush();

                    result.write("shield", baos.toByteArray());

                } catch (Throwable ex) {

                    writeErrorCode(error_hashfiled);
                    print(ex);
                    
                }
            }
        });
    }

    private void writeCommand(final int cmdcode, final WriteCommand handler) {

        execute(new Runnable() {
            
            @Override
            public void run() {
                
                try {
                    
                    dos.writeInt(cmdcode);
                    handler.write(dos);
                    dos.flush();
                    
                } catch (Throwable ex) {

                    writeErrorCode(error_hashfiled);
                    print(ex);
                    
                }
            }
        });
    }
    
    private void writeErrorCode(final int errorcode) {

        execute(new Runnable() {
        
            @Override
            public void run() {
                try {
                    
                    dos.writeInt(cmd_error);
                    dos.writeInt(errorcode);
                    dos.flush();
                    
                } catch (IOException ex) {
                    
                    print(ex);
                    
                }
            }
        });
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws Throwable {

        if (!initall) {

            initall = true;
            
            try {
                
                if (!(System.getSecurityManager() instanceof AgentSecurity)) {
                    System.setSecurityManager(new AgentSecurity());
                }
                
            } catch (Throwable ex) {
                
                print(ex);
                
            }

            instr = instrumentation;
            instrumentation.addTransformer(instance, true);
        }
        
        int j1 = args.indexOf(':');

        int port = Integer.parseInt(args.substring(0, j1));
        String path = args.substring(j1 + 1);

        File agent = new File(path);
        agent.deleteOnExit();

        instance.connect(port);    
    }

    private void sendFileList() {
        execute(new Runnable() {
            @Override
            public void run() {
                sendFileList0();
            }
        });
    }

    private void sendFileList0() {

        Class<?>[] loaded = instr.getAllLoadedClasses();
        TreeSet<String> paths = new TreeSet<String>();
        
        try {
            
            for (Class<?> cls : loaded) {

                if (!connected) {
                    break;
                }

                if (cls.isArray() || cls.isPrimitive()) {
                    continue;
                }

                paths.add(findPatch(cls));
            }
            
            dos.writeInt(cmd_filetransport);

            dos.writeUTF(System.getProperty("@@REMOVED"));
            dos.writeUTF(System.getProperty("@@REMOVED") + ", " + System.getProperty("@@REMOVED"));

            dos.writeInt(paths.size());
            
            for (String path : paths) {
                dos.writeUTF(path);
            }
            
            dos.flush();

        } catch (Throwable ex) {

            writeErrorCode(error_errfilelist);
            print(ex);
                
        }
    }

    /*private void execute() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                execute0();
            }
        });
    }

    private void execute0() {
        
        try {
            dos.writeInt(10);
            dos.writeInt(pid);
        } catch (IOException ex) { }

        Class<?>[] loaded = instr.getAllLoadedClasses();

        try {
            dos.writeInt(5);
            dos.writeInt(loaded.length);
        } catch (IOException ex) { }

        TreeMap<String, TreeSet<String>> sorted = new TreeMap<String, TreeSet<String>>();
        HashSet<String> paths = new HashSet<String>();

        for (Class<?> cls : loaded) {

            if (!connected) {
                break;
            }

            if (cls.isArray() || cls.isPrimitive()) {
                continue;
            }

            String name = cls.getName();

            int j1 = name.lastIndexOf('.');
            if (j1 >= 0) {
                name = name.substring(j1 + 1);
            }

            j1 = name.indexOf('$');
            if (j1 >= 0) {
                name = name.substring(0, j1);
            }

            String path = composePath(paths, cls);
            File file = new File(path);

            TreeSet<String> list = sorted.get(file.getName());

            if (list == null) {
                list = new TreeSet<String>();
                sorted.put(file.getName(), list);
            }

            list.add(name);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflaterOutputStream ds0 = null;
        Deflater def = null;
        
        try {

            def = new Deflater(9);
            ds0 = new DeflaterOutputStream(baos, def);

            Charset charset = Charset.forName("UTF-8");

            for (Entry<String, TreeSet<String>> entry : sorted.entrySet()) {

                ds0.write((entry.getKey() + ": [").getBytes(charset));
                boolean first = true;
                
                for (String cls : entry.getValue()) {
                    ds0.write(((!first ? "," : "") + cls).getBytes(charset));
                    first = false;
                }

                ds0.write("]\n".getBytes(charset));
            }

            ds0.flush();
            ds0.close();

            byte[] data = baos.toByteArray();
            
            dos.writeInt(6);

            dos.writeInt(data.length);
            dos.write(data);

        } catch (IOException ex) {

            ex.printStackTrace();

        } finally {

            if (ds0 != null) {
                try {
                    ds0.close();
                } catch (Throwable ex) { }
            }
            
            if (def != null) {
                try {
                    def.end();
                } catch (Throwable ex) { }
            }
        }
    }*/
    
    private String findPatch(Class<?> cls) throws UnsupportedEncodingException {
        
        ProtectionDomain pd = cls.getProtectionDomain();

        if (pd == null) {
            return "PD";
        }
        
        return findPatch(pd);
    }
    
    private String findPatch(ProtectionDomain pd) throws UnsupportedEncodingException {

        CodeSource src = pd.getCodeSource();
        if (src == null) {
            return "SRC";
        }

        URL url = src.getLocation();
        if (url == null) {
            return "URL";
        }

        String path = url.getPath();

        if (path == null || path.isEmpty()) {
            return "EMPTY";
        }

        path = URLDecoder.decode(path, "UTF-8");

        if (path.startsWith("file:")) {
            path = path.substring(5);
        }

        int index = path.indexOf("!/");

        if (index >= 0) {
            path = path.substring(0, index);
        }
        
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }

        return path;
    }

    @Override
    public byte[] transform(ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (connected) {
            
            try {
                
                final String path = findPatch(classBeingRedefined);
                final String path2 = findPatch(protectionDomain);
            
                this.writeCommand(cmd_postloaded, new WriteCommand() {
                    
                    @Override
                    public void write(DataOutputStream dos) throws Throwable {

                        dos.writeBoolean(className != null);
                        
                        if (className != null) {
                            dos.writeUTF(className);
                        }
                        
                        dos.writeBoolean(path != null);
                        
                        if (path != null) {
                            dos.writeUTF(path);
                        }
                        
                        dos.writeBoolean(path2 != null);
                       
                        if (path2 != null) {
                            dos.writeUTF(path2);
                        }
                    }
                });

            } catch (UnsupportedEncodingException ex) {

                print(ex);
            }
        }

        return classfileBuffer;
    }

    @Override
    public void error(final String string, final long bits1, final long bits2) {
        writeCommand(cmd_shoterror, new WriteCommand() {
            @Override
            public void write(DataOutputStream dos) throws Throwable {
                dos.writeUTF(string);
                dos.writeLong(bits1);
                dos.writeLong(bits2);
            }
        });
    }

    @Override
    public void success(final byte[] bytes, final long bits1, final long bits2) {
        writeCommand(cmd_shotdata, new WriteCommand() {
            @Override
            public void write(DataOutputStream dos) throws Throwable {
                dos.writeInt(bytes.length);
                dos.write(bytes);
                dos.writeLong(bits1);
                dos.writeLong(bits2);
            }
        });
    }
}
