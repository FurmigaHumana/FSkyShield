package me.FurH.ShieldServer.tasklist;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ProcessList {

    private final LinkedHashMap<ProcessEntry, HashSet<Integer>> processes;

    public ProcessList() {
        processes = new LinkedHashMap<>();
    }

    public void parse(String data) {

        reset();
        String[] entries = data.split("\n");
        
        for (String entry : entries) {
            
            ProcessEntry pentry = new ProcessEntry(entry);
            HashSet<Integer> list = processes.get(pentry);

            if (list == null) {
                list = new HashSet<>();
                processes.put(pentry, list);
            }

            list.add(pentry.pid);
        }
    }
    
    private void reset() {
        for (Entry<ProcessEntry, HashSet<Integer>> entry : processes.entrySet()) {
            entry.setValue(new HashSet<>());
        }
    }

    public String serialize() {

        JSONArray array = new JSONArray();
        
        for (Entry<ProcessEntry, HashSet<Integer>> entry : processes.entrySet()) {

            ProcessEntry process = entry.getKey();
            HashSet<Integer> pids = entry.getValue();

            JSONObject obj = process.toJson();
            obj.put("pids", pids);
            
            array.put(obj);
        }

        return array.toString();
    }

    public void fromJson(String json) {

        JSONTokener token = new JSONTokener(json);
        JSONArray array = new JSONArray(token);

        for (int j1 = 0; j1 < array.length(); j1++) {
            
            JSONObject next = array.getJSONObject(j1);
            ProcessEntry entry = new ProcessEntry(next);
            
            next.get("pids");
            
            processes.put(entry, new HashSet<>());
        }
    }
}