package me.FurH.ShieldServer.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import me.FurH.Core.file.FileUtils;
import me.FurH.ShieldServer.MainServer;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class DatabaseManager {
    
    private static final Executor executor;
    
    static {
        executor = Executors.newSingleThreadExecutor();
    }
    
    private LinkedHashMap<String, String> versions;
    private HashSet<String> libraries;

    public DatabaseManager(MainServer server) {
    }
    
    public static void heavy(Runnable task) {
        executor.execute(task);
    }
    
    public String getHexInfo(String hex) {
        return versions.get(hex);
    }

    public boolean isLibrary(String hex) {
        return libraries.contains(hex);
    }

    public boolean isBlacklisted(String hexOrName) {
        return false;
    }
    
    public void loadAll() throws IOException {

        File dbdir = new File("database2");

        versions = load(dbdir);
        
        LinkedHashMap<String, String> libtemp = load(new File(dbdir, "libraries"));

        libraries = new HashSet<>();
        libraries.addAll(libtemp.keySet());
    }

    public LinkedHashMap<String, String> load(File dir) throws IOException {
        
        LinkedHashMap<String, String> ret = new LinkedHashMap<>();
        ArrayList<String> files = new ArrayList<>();
        
        for (File file : dir.listFiles()) {

            String name = file.getName();

            if (!name.endsWith("-.txt")) {
                continue;
            }
            
            files.add(name);
        }
        
        Collections.sort(files);
        
        for (String name : files) {

            int j1 = name.indexOf('-');
            String mod = name.substring(j1 + 1, name.length() - 38);

            File file = new File(dir, name);
            ArrayList<String> lines = FileUtils.getLinesFromFile(file);

            for (String line : lines) {
                ret.putIfAbsent(line.substring(0, 32), mod);
            }
        }
        
        return ret;
    }
}