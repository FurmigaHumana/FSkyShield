package me.FurH.ShieldServer.newlistener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class CmdLine {
    
    private static final Pattern pattern;

    static {
        pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
    }
    
    int pid;

    String javaw;
    String librarypath;
    String launcherbrand;
    String launcherversion;
    String clientjar;

    String[] classpath;
    String mainclass;

    String username;
    String version;
    String gamedir;
    String assetsdir;
    String assetindex;
    String uuid;
    String token;
    String properties;
    String usertype;

    ArrayList<String> extraargs = new ArrayList<>();

    public static String path(String path) {
        return path.replace("\\", "/");
    }
    
    public static String[] path(String[] path) {
       
        for (int j1 = 0; j1 < path.length; j1++) {
            path[j1] = path[j1].replace("\\", "/");
        }
        
        return path;
    }
    
    void parse(String result) {
        
        int j1 = result.indexOf(':');
        pid = Integer.parseInt(result.substring(0, j1));

        result = result.substring(j1 + 1);

        ArrayList<String> args = parseargs(result);
        Iterator<String> it = args.iterator();

        boolean nextmain = false;
        
        while (it.hasNext()) {
            
            String next = it.next();

            if (nextmain) {
                if (!next.startsWith("-")) {
                    mainclass = next;
                    nextmain = false;
                    continue;
                }
            }

            if (javaw == null) {
                javaw = path(next);
            } else if (librarypath == null && next.startsWith("-Djava.library.path=")) {
                librarypath = path(next.substring(20));
            } else if (launcherbrand == null && next.startsWith("-Dminecraft.launcher.brand=")) {
                launcherbrand = next.substring(27);
            } else if (launcherversion == null && next.startsWith("-Dminecraft.launcher.version=")) {
                launcherversion = next.substring(29);
            }  else if (clientjar == null && next.startsWith("-Dminecraft.client.jar=")) {
                clientjar = path(next.substring(23));
            } else if (classpath == null && next.equals("-cp")) {
                classpath = path(it.next().split(";"));
                nextmain = true;
            } else if (username == null && next.equals("--username")) {
                username = it.next();
            } else if (version == null && next.equals("--version")) {
                version = it.next();
            } else if (gamedir == null && next.equals("--gameDir")) {
                gamedir = path(it.next());
            } else if (assetsdir == null && next.equals("--assetsDir")) {
                assetsdir = path(it.next());
            } else if (assetindex == null && next.equals("--assetIndex")) {
                assetindex = it.next();
            } else if (uuid == null && next.equals("--uuid")) {
                uuid = it.next();
            } else if (token == null && next.equals("--accessToken")) {
                token = it.next();
            } else if (properties == null && next.equals("--userProperties")) {
                properties = it.next();
            } else if (usertype == null && next.equals("--userType")) {
                usertype = it.next();
            } else {
                extraargs.add(next);
            }
        }
    }
    
    private ArrayList<String> parseargs(String cmdline) {
        
        if (cmdline == null) {
            return null;
        }

        ArrayList<String> args = new ArrayList<>();

        if (cmdline.isEmpty()) {
            return args;
        }

        Matcher m = pattern.matcher(cmdline);

        while (m.find()) {
            String token = m.group(1).replace("\"", "").trim();
            if (!token.isEmpty()) {
                args.add(token);
            }
        }

        return args;
    }

    JSONObject toJson() {
        
        JSONObject data = new JSONObject();

        data.put("pid", pid);
        data.put("javaw", javaw);
        
        data.put("username", username);
        data.put("uuid", uuid);

        data.put("launcherbrand", launcherbrand);
        data.put("launcherversion", launcherversion);
        data.put("mainclass", mainclass);
        data.put("version", version);
        data.put("assetindex", assetindex);

        data.put("token", "<token>");

        data.put("properties", properties);
        data.put("usertype", usertype);
        
        data.put("librarypath", librarypath);
        data.put("assetsdir", assetsdir);
        data.put("gamedir", gamedir);
        data.put("clientjar", clientjar);
        
        data.put("classpath", sort(Arrays.asList(classpath)));
        data.put("extraargs", sort(extraargs));

        return data;
    }
    
    private ArrayList<String> sort(List<String> sorted) {
       
        ArrayList<String> result = new ArrayList<>();
        result.addAll(sorted);
        Collections.sort(result);
        
        return result;
    }
}