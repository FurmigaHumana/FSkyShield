/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.ShieldServer.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import me.FurH.Core.cache.soft.SoftMap;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQLThread;
import me.FurH.ShieldServer.Stats;

/**
 *
 * @author lgpse
 */
public class ClientIds {

    private static final SoftMap<UUID, Integer> cache;

    static {
        cache = new SoftMap<>();
    }

    public static Integer getUniqueID(UUID uniqueId, boolean create, SQLThread t) throws SQLException {
        
        Integer id = cache.get(uniqueId);
        if (id != null) {
            return id;
        }
        
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = t.prepare("@@REMOVED");
            ps.setString(1, uniqueId.toString());
            ps.execute();

            rs = ps.getResultSet();

            if (!rs.next()) {
                
                if (!create) {
                    id = null;
                } else {
                    id = createUniqueID(uniqueId, t);
                }
                
            } else {
                
                id = rs.getInt(1);
                
            }
            
            cache.put(uniqueId, id);
            return id;

        } finally {

            Closer.closeQuietly(ps, rs);

        }
    }
    
    private static Integer createUniqueID(UUID uniqueId, SQLThread t) throws SQLException {
        
        Stats.setups++;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = t.prepareAutoId("@@REMOVED");
            t.commitNext();
            
            ps.setString(1, uniqueId.toString());
            ps.execute();

            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                return null;
            }

            return rs.getInt(1);

        } finally {
            
            Closer.closeQuietly(ps, rs);
            
        }
    }
}