package me.FurH.ShieldServer.nicks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import me.FurH.Core.cache.soft.SoftMap;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.encript.password.PasswordHash;
import me.FurH.Core.util.Utils;
import me.FurH.ShieldServer.MainServer;
import me.FurH.ShieldServer.server.ShieldClient;
import me.FurH.SkyShield.packets.nicks.Packet79AddNick;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class NickManager {
    
    private final SoftMap<String, LoginTry> tries;
    private final PasswordHash hasher;

    public NickManager(MainServer main) {
        this.hasher = new PasswordHash("@@REMOVED", "@@REMOVED", -1);
        this.tries = new SoftMap<>();
    }
    
    private LoginTry fetchTries(String name) {
       
        LoginTry trydat = tries.get(name);

        if (trydat != null && trydat.isExpired()) {
            tries.remove(name);
            return null;
        }
        
        return trydat;
    }

    public void addNick(ShieldClient client, Packet79AddNick packet, String key) {

        try {
            
            String name = packet.name;
            client.info("Try login for: " + name);
            
            LoginTry trydat = fetchTries(name);
            
            if (trydat != null && !trydat.isAllowedToTry()) {
                reply(client, packet, "Limite de tentativas excedido, tente novamente mais tarde!");
                return;
            }
            
            String password = new String(Encrypto.aes_decrypt(packet.pw, key + name), Utils.UTF8);
            
            SQL.mslow(new SQLTask() {
                
                @Override
                public void execute(SQLDb sqldb, SQLThread t) throws Throwable {
                    
                    ArrayList<Integer> ids = fetchIds(name, t);
                    
                    if (ids == null) {
                        reply(client, packet, "Nenhuma conta encontrada com este nick!");
                        return;
                    }
                    
                    ArrayList<Integer> success = null;
                    
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    
                    try {
                        
                        ps = t.prepare("@@REMOVED");
                        
                        for (Integer id : ids) {
                            
                            Closer.closeQuietly(rs);
                            
                            ps.setInt(1, id);
                            ps.execute();
                            
                            rs = ps.getResultSet();
                            
                            if (!rs.next()) {
                                continue;
                            }
                            
                            String user_pass = rs.getString(1);
                            String user_salt = rs.getString(2);
                            
                            if (hasher.match(password, user_pass, user_salt)) {
                                
                                if (success == null) {
                                    success = new ArrayList<>();
                                }
                                
                                success.add(id);
                                
                            } else {
                                
                                LoginTry ntrydat = trydat;
                                
                                if (ntrydat == null) {
                                    ntrydat = new LoginTry();
                                    tries.put(name, ntrydat);
                                }
                                
                                ntrydat.incTries();
                            }
                        }
                        
                        if (success == null) {
                            reply(client, packet, "Este nick não esta cadastrado ou a senha esta errada!");
                            return;
                        }
                        
                        packet.ids = success.toArray(new Integer[0]);
                        reply(client, packet, "OK");
                        
                    } finally {
                        
                        Closer.closeQuietly(ps, rs);
                        
                    }
                }
            });
            
        } catch (Exception ex) {

            ex.printStackTrace();
            reply(client, packet, "Erro");
            
        }
    }

    private void reply(ShieldClient client, Packet79AddNick packet, String msg) {
        packet.reply = msg;
        client.write(packet);
    }

    private ArrayList<Integer> fetchIds(String name, SQLThread t) throws SQLException {
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {

            ps = t.prepare("@@REMOVED");

            ps.setString(1, name);
            ps.execute();

            rs = ps.getResultSet();

            ArrayList<Integer> ret = null;

            while (rs.next()) {
                
                if (ret == null) {
                    ret = new ArrayList<>();
                }
                
                ret.add(rs.getInt(1));
            }
            
            return ret;
            
        } finally {
            
            Closer.closeQuietly(ps, rs);
            
        }
    }
}