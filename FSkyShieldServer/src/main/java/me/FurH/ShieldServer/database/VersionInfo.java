/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.database;

import java.io.IOException;
import java.util.HashSet;
import me.FurH.Core.yaml.ConfigLoader;

/**
 *
 * @author lgpse
 */
public class VersionInfo {
    
    public final String name;
    public final String hash;
    public final HashSet<String> entries;

    public VersionInfo(ConfigLoader config) throws IOException {
        name = config.getStr("Name");
        hash = config.getStr("Hash");
        entries = config.getSet("entries");
    }

    public boolean belongs(String hex) {
        return entries.contains(hex);
    }
}
