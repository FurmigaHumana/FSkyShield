/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield;

import java.awt.Desktop;
import java.awt.SystemTray;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import me.FurH.Async.AsyncExecutor;
import me.FurH.Core.config.FProperties;
import me.FurH.Core.executors.TaskExecutor;
import me.FurH.Core.executors.TimerExecutor;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.os.OperatingSystem;
import me.FurH.Core.util.Callback;
import me.FurH.JavaPacker.loader.AClassLoader;
import me.FurH.JavaPacker.loader.ErrorGui;
import me.FurH.Logger.LogFactory;
import me.FurH.SkyShield.attacher.NewAgentServer;
import me.FurH.SkyShield.connect.ConnectToServer;
import me.FurH.SkyShield.gui.MainGui;
import me.FurH.SkyShield.gui.SetupGui;
import me.FurH.SkyShield.idle.IdleDetector;
import me.FurH.SkyShield.newscan.NewGameScanner;
import me.FurH.SkyShield.process.AdminCheck;
import me.FurH.SkyShield.resources.Resources;
import me.FurH.SkyShield.screens.Screenshots;
import me.FurH.SkyShield.tray.TrayGui;
import me.FurH.SkyShield.updater.Updater;

/**
 *
 * @author Luis
 */
public class ShieldClient {
    
    private static boolean skipsession = false;
    private static final UUID session;
    public static final int port = -1; // @@REMOVED

    public static long startuptime;
    public static UUID uniqueId;
    public static String clienthash;

    static {
        startuptime = System.currentTimeMillis();
        session = UUID.randomUUID();
    }
    
    public static ShieldClient shield;

    public final TrayGui tray_gui;
    public final MainGui main_gui;
    public final Updater up_gui;

    public final NewGameScanner game_scanner;
    public final Screenshots screenshot;
    
    public ConnectToServer connection;
    public  NewAgentServer newserver;
    private FProperties props;
    private String mainfile;
    
    public ShieldClient() throws Exception {
        this.game_scanner   = new NewGameScanner(this);
        this.tray_gui       = new TrayGui(this);
        this.main_gui       = new MainGui(this);
        this.up_gui         = new Updater(this);
        this.screenshot     = new Screenshots(this);
    }

    public NewAgentServer getNewAgentServer() {

        if (newserver == null) {
            newserver = new NewAgentServer(this);
        }

        return newserver;
    }
    
    public FProperties getFileProps() throws IOException {

        if (props == null) {
            props = new FProperties(new File("settings.properties"));
            props.load();
        }

        return props;
    }
    
    public void shutdown() {
        System.exit(0);
    }

    public void connect(String url) throws Exception {
        this.connection = new ConnectToServer(url, this);
        this.connection.initialize();
    }

    public void initialize() throws Exception {
                
        tray_gui.display();

        tray_gui.setToolTip("Conectando...");
        
        IdleDetector idle = new IdleDetector(this);
        idle.schedule();

        TaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                main_gui.connect();
            }
        });

        TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                if (main_gui.isAutoUpdate()) {
                    up_gui.checkUpdates(true);
                }
            }
        }, 1, 12, TimeUnit.HOURS);
        
        TimerExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                System.gc();
            }
        }, 10, TimeUnit.SECONDS);
    }
    
    private static UUID session(File idfile) throws Throwable {

        if (idfile.exists() && idfile.isFile()) {
            return UUID.fromString(FileUtils.getLineFromFile(idfile));
        }

        UUID id = UUID.randomUUID();
        FileUtils.setLineOfFile(idfile, id.toString());

        return id;
    }

    public static void startup(String[] args) throws Exception {

        if (args.length <= 0) {
            JOptionPane.showMessageDialog(null, "Não execute o arquivo diretamente");
            return;
        }

        clienthash = args[ 0 ];
        AsyncExecutor.setLowMode();
        
        ErrorGui.supurl = "@@REMOVED";
        ErrorGui.supemail = "@@REMOVED";

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { }

        if (!SystemTray.isSupported() || !OperatingSystem.getOS().isWindows() || !Desktop.isDesktopSupported()) {
            ErrorGui.errorlines("Sistema operacional não suportado", false, true, OperatingSystem.getOS().name(), "SystemTray: " + SystemTray.isSupported(), "Desktop: " + Desktop.isDesktopSupported());
            return;
        }

        System.setProperty("java.net.preferIPv4Stack", "true");

        String folder;
        if (OperatingSystem.getOS() == OperatingSystem.WINDOWS_XP) {
            folder = System.getenv("APPDATA");
        } else {
            folder = System.getenv("LOCALAPPDATA");
        }

        if (folder == null || folder.isEmpty()) {
            folder = new File("local.txt").getAbsoluteFile().getParent();
        }

        File local  = new File(folder, "Microsoft/Security/");        
        File idfile = new File(local, "version.id");

        try {
            
            uniqueId = session(idfile);
            
        } catch (Throwable ex) {

            ex.printStackTrace();

            try {
                uniqueId = session(new File("version.id"));
            } catch (Throwable ex1) {
                ex1.printStackTrace();
            }
        }

        Resources.initialize(folder);
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (!skipsession) {
                    new File("session.id").delete();
                }
            }
        }));

        File lock = new File("session.id");

        try {
            Thread.sleep(NumberUtils.nextInt(1, 1000));
            FileUtils.setLineOfFile(lock, session.toString());
        } catch (Throwable ex) {
            ex.printStackTrace();
            System.exit(0);
            return;
        }

        File logs = new File("logs");

        ErrorGui.logsdir = logs;
        LogFactory.initialize(logs);

        ErrorGui.append("admin.level: " + AdminCheck.isRunningAdmin());

        File cache = new File("cache");

        if (cache.exists()) {
            try {
                FileUtils.deleteDirectory(cache);
            } catch (Throwable ex) { }
        }

        File bit32 = new File("32-bit");

        if (bit32.exists()) {
            
            File checked = new File(bit32, "ready");
            
            if (!checked.exists()) {
                
                SetupGui.finishSetup(bit32, checked, new Callback<Boolean>() {
                    @Override
                    public void invoke(Boolean v) {
                        try {
                            finishInit();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            System.exit(0);
                        }
                    }
                });
                
                return;
            }
        }

        finishInit();
    }
    
    private static void finishInit() throws Exception {

        try {
            
            shield = new ShieldClient();
            shield.initialize();
            
        } catch (Throwable ex) {

            ErrorGui.error(ex, true, false, "Verifique sua conexão com a internet ou aguarde e tente novamente mais tarde!");
            ex.printStackTrace();

        }
    }

    public static void checkSession() {

        try {
            
            File lock = new File("session.id");
            
            if (!lock.exists()) {
                
                skipsession = true;
                System.exit(0);
                
            } else if (!FileUtils.getLineFromFile(lock).equals(session.toString())) {
                
                skipsession = true;
                System.exit(0);
                
            }
            
        } catch (Throwable ex) {
            
            skipsession = true;
            System.exit(0);
            
        }
    }
    
    public String getMainFile() {

        if (mainfile == null) {
            
            try {

                String path = findPatch(AClassLoader.class);
                
                File file = new File(path);
                file.deleteOnExit();
                
                mainfile = file.getName();

            } catch (Throwable ex) {

                mainfile = ex.getMessage();
                ex.printStackTrace();
                
            }
        }

        return mainfile;
    }

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
}