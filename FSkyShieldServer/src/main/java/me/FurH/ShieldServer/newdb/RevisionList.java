package me.FurH.ShieldServer.newdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class RevisionList {

    private final ArrayList<VersionEntry> entries;
    private boolean loaded = false;
    
    public RevisionList() {
        this.entries = new ArrayList<>();
    }
    
    public Iterable<VersionEntry> entries() {
        return this.entries;
    }

    public String cutPath(String path) {

        String verpath = cutVersion(path);
        if (verpath == null) {
            verpath = cutLibrary(path);
        }
        
        return verpath;
    }

    String cutVersion(String path) {

        int j1 = path.lastIndexOf("/versions/");

        if (j1 == -1) {
            return null;
        }

        return path.substring(j1);
    }

    String cutLibrary(String path) {

        int j1 = path.lastIndexOf("/libraries/");

        if (j1 == -1) {
            return null;
        }

        return path.substring(j1);
    }

    void load(File revdir) {
        for (File verdir : revdir.listFiles()) {
            VersionEntry entry = new VersionEntry(this);
            if (entry.load(verdir)) {
                entries.add(entry);
            }
        }
    }

    public void loadAll() {
        
        if (loaded) {
            return;
        }
        
        loaded = true;

        for (VersionEntry entry : entries) {
            try {
                entry.loadAll();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}