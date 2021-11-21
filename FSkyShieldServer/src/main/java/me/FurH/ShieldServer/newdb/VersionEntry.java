package me.FurH.ShieldServer.newdb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.file.SimpleVisitor;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class VersionEntry {

    private final HashMap<String, FileEntry> entries_libraries;
    private final HashMap<String, FileEntry> entries_versions;
    
    private String version;

    private File libraries;
    private File versions;
    
    private final RevisionList list;
    
    public VersionEntry(RevisionList list) {
        entries_libraries = new HashMap<>();
        entries_versions = new HashMap<>();
        this.list = list;
    }
    
    public boolean hasHash(String path, String hash, boolean ziphash) {
        FileEntry entry = getEntry(path);
        if (entry == null) {
            return false;
        }
        
        return entry.compatible(hash, ziphash);
    }
    
    public boolean belongs(String path) {
        return getEntry(path) != null;
    }
    
    private FileEntry getEntry(String path) {
        
        FileEntry entry = entries_libraries.get(path);
        if (entry == null) {
            entry = entries_versions.get(path);
        }
        
        return entry;
    }
    
    @Override
    public String toString() {
        return version;
    }

    boolean load(File verdir) {

        this.version = verdir.getName();

        this.libraries  = new File(verdir, "libraries");
        this.versions   = new File(verdir, "versions");

        return libraries.exists() && versions.exists();
    }

    void loadAll() throws IOException {

        FileUtils.visitAllFilesAt(new SimpleVisitor() {

            @Override
            public void visit(File file) {
                entries_versions.put(list.cutVersion(file.getAbsolutePath()), new FileEntry(file));
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        }, versions);
        
        FileUtils.visitAllFilesAt(new SimpleVisitor() {

            @Override
            public void visit(File file) {
                entries_libraries.put(list.cutLibrary(file.getAbsolutePath()), new FileEntry(file));
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        }, libraries);
    }
}