package me.FurH.ShieldServer.filelist;

import java.util.LinkedHashSet;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class FileObject {

    private final LinkedHashSet<FileObject> files;
    
    final String name;
    private final int type;

    FileObject(String name, int type) {
        this.files = new LinkedHashSet<>();
        this.name = name;
        this.type = type;
    }
    
    FileObject(JSONObject obj) {
        
        this(obj.keys().next(), 1);

        JSONArray arr = obj.getJSONArray(name);
        
        for (int j1 = 0; j1 < arr.length(); j1++) {
            Object next = arr.get(j1);
            if (next instanceof String) {
                files.add(new FileObject((String) next, 0));
            } else {
                files.add(new FileObject((JSONObject) next));
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.name);
        hash = 11 * hash + this.type;
        return hash;
    }
    
    public Object toJson() {
        
        if (!isDir()) {
            
            return name;
            
        } else {

            JSONObject json = new JSONObject();
            JSONArray objs = new JSONArray();

            for (FileObject file : files) {
                objs.put(file.toJson());
            }

            json.put(name, objs);
            
            return json;
        }
    }
    
    boolean isDir() {
        return type == 1;
    }

    void add(FileObject file) {
        files.add(file);
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final FileObject other = (FileObject) obj;
        if (this.type != other.type) {
            return false;
        }
        
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        
        return true;
    }
}