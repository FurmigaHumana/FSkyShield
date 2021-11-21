/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.database;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.yaml.ConfigLoader;
import me.FurH.Core.yaml.ConfigManager;
import me.FurH.ShieldServer.MainServer;

/**
 *
 * @author lgpse
 */
public class VersionManager {
    
    public  final LinkedHashMap<String, VersionInfo> versions;
    private final HashSet<String> blacklist;
    private final HashSet<String> names;
    private final HashSet<String> libs;
    private final MainServer server;
   
    public int minhashes = 0;
    
    public VersionManager(MainServer server) {
        this.versions = new LinkedHashMap<>();
        this.blacklist = new HashSet<>();
        this.names = new HashSet<>();
        this.libs = new HashSet<>();
        this.server = server;
    }
    
    public boolean isBlacklisted(String key) {
        return blacklist.contains(key);
    }
    
    public boolean isInDb(String cls) {
        return names.contains(cls);
    }
    
    public boolean isLibrary(String hex) {
        return libs.contains(hex);
    }

    public boolean isKnown(String cls) {

        int j1 = cls.lastIndexOf('.');
        if (j1 >= 0) {
            cls = cls.substring(j1 + 1);
        }

        j1 = cls.lastIndexOf('/');
        if (j1 >= 0) {
            cls = cls.substring(j1 + 1);
        }

        j1 = cls.indexOf('$');
        if (j1 >= 0) {
            cls = cls.substring(0, j1);
        }
        
        j1 = cls.indexOf('[');
        if (j1 >= 0) {
            cls = cls.substring(j1 + 2);
        }

        j1 = cls.indexOf(';');
        if (j1 >= 0) {
            cls = cls.substring(0, j1);
        }

        if (cls.isEmpty()) {
            return true;
        }

        return names.contains(cls);
    }

    public void loadAll() throws IOException {
        
        minhashes = 0;
        versions.clear();
        libs.clear();
        blacklist.clear();
        
        File blackfile = new File("database", "blacklist.yml");
        
        if (blackfile.exists()) {

            ConfigLoader config = ConfigManager.getConfigLoader(blackfile, null, true, false);

            blacklist.addAll(config.getLst("entries"));
            blacklist.addAll(config.getLst("names"));
        }

        File libdir = new File("database", "libraries");
        
        for (File file : libdir.listFiles()) {

            if (!file.getName().endsWith("-.yml")) {
                continue;
            }

            ConfigLoader config = ConfigManager.getConfigLoader(file, null, true, false);
            
            libs.addAll(config.getLst("entries"));
            names.addAll(config.getLst("names"));
        }

        System.err.println("Loaded " + libs.size() + " library entries");
        
        File vlist = new File("database", "vlist.txt");
        File verdir = new File("database", "versions");
        
        List<String> lines = FileUtils.getLinesFromFile(vlist);

        for (File file : verdir.listFiles()) {

            String name = file.getName();

            if (!name.endsWith("-.yml")) {
                continue;
            }

            if (!lines.contains(name)) {
                lines.add(name);
            }
        }
        
        for (String line : lines) {
        
            File input = new File(verdir, line);

            ConfigLoader config = ConfigManager.getConfigLoader(input, null, true, false);
            names.addAll(config.getLst("names"));

            VersionInfo version = new VersionInfo(config);
            
            if (minhashes == 0 || minhashes > version.entries.size()) {
                minhashes = version.entries.size();
            }
            
            versions.put(input.getName(), version);
        }
        
        System.err.println("Loaded " + versions.size() + " versions, min hashes: " + minhashes);
    }
}
