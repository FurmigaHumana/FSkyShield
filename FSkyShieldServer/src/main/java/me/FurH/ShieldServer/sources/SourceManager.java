package me.FurH.ShieldServer.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import me.FurH.Core.close.Closer;
import me.FurH.Core.database.SQL;
import me.FurH.Core.database.SQLDb;
import me.FurH.Core.database.SQLTask;
import me.FurH.Core.database.SQLThread;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.number.NumberUtils;
import me.FurH.Core.util.Utils;
import me.FurH.ShieldServer.MainServer;
import me.FurH.ShieldServer.bigdata.BigData;
import static me.FurH.ShieldServer.database.DatabaseManager.heavy;
import me.FurH.ShieldServer.diff.Diff;
import me.FurH.ShieldServer.diff.StringDiff;
import me.FurH.ShieldServer.server.ShieldClient;
import me.FurH.SkyShield.encoder.Compressor;
import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class SourceManager {

    private final HashMap<String, ArrayList<String>> index;

    public SourceManager(MainServer server) {
        this.index = new HashMap<>();
    }

    public void loadAll() throws IOException {
        
        File sources = new File("@@REMOVED.txt");
        ArrayList<String> lines = FileUtils.getLinesFromFile(sources);

        for (String line : lines) {
            
            if (line.isEmpty() || line.charAt(0) == '#') {
                continue;
            }

            String hex = line.substring(0, 32);
            String name = line.substring(33);
            
            ArrayList<String> list = index.get(name);

            if (list == null) {
                list = new ArrayList<>(0);
                index.put(name, list);
            }

            list.add(hex);
        }

    }
    
    public void checkCompare(ShieldClient client, int playid, int bigid, String name, SQLThread t) throws Exception {
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            
            ps = t.prepare("@@REMOVED");
            
            ps.setInt(1, bigid);
            
            ps.execute();
            
            rs = ps.getResultSet();
            
            if (rs.next()) {
                return;
            }
            
            byte[] data = BigData.fetch(bigid, t);

            heavy(new Runnable() {
                @Override
                public void run() {
                    try {
                        heavyCompare(client, playid, bigid, data, name);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } finally {
            
            Closer.closeQuietly(ps, rs);
            
        }
    }

    private void heavyCompare(ShieldClient client, int playid, int bigid, byte[] data, String name) throws Exception {

        SourceResult result = new SourceResult();
        result.source = Compressor.toString(data);

        heavyCompare(client, result, name);

        SQL.mslow(new SQLTask() {
            @Override
            public void execute(SQLDb db, SQLThread t) throws Throwable {
                result.storeCompare(playid, bigid, t);
            }
        });
    }
    
    private void heavyCompare(ShieldClient client, SourceResult result, String name) throws IOException {
        
        ArrayList<String> files = index.get(name);
        
        if (files != null) {

            HashSet<String> words = new HashSet<>(Arrays.asList(result.source.split(" ")));

            double lastdiff = 0;
            File bestfile = null;

            for (String file : files) {

                File acfile = new File("sources", file + ".java");
                String file2 = getRawLines(acfile);

                int totalw = 0;
                int hits = 0;
                
                for (String word : file2.split(" ")) {
                    totalw++;
                    
                    if (words.contains(word)) {
                        hits++;
                    }
                }
                
                double ret = NumberUtils.getWorkDoneDouble(hits, totalw);

                if (bestfile == null || ret > lastdiff) {
                    lastdiff = ret;
                    bestfile = acfile;
                }
            }

            if (bestfile != null) {

                client.info("best match for " + name + " is " + bestfile.getName() + " with " + lastdiff);
               
                StringDiff diff = new StringDiff();

                LinkedList<Diff> diffs = diff.diff_main(result.source, getRawLines(bestfile));
                diff.diff_cleanupSemantic(diffs);
                
                result.compared = diff_prettyHtml(result, diffs);
            }
        }
    }
    
    private String diff_prettyHtml(SourceResult result, List<Diff> diffs) {
        
        StringBuilder html = new StringBuilder();
        
        for (Diff aDiff : diffs) {
           
            String text = aDiff.text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
            
            switch (aDiff.operation) {
                
                case INSERT:
                    
                    boolean interesting = hasNumbers(text);
                    
                    html.append("<ins style=\"background:")
                            .append(interesting ? "#ff8100" : "#e6ffe6")
                            .append("\">").append(text)
                            .append("</ins>");
                    
                    if (interesting) {
                        result.interesting = true;
                    }
                    
                    break;
                case DELETE:
                    html.append("<del style=\"background:#ffe6e6;\">").append(text)
                            .append("</del>");
                    break;
                case EQUAL:
                    html.append("<span class=\"ortxt\">").append(text).append("</span>");
                    break;
            }
        }
        return html.toString();
    }
    
    private boolean hasNumbers(String text) {
        
        StringTokenizer st = new StringTokenizer(text, " ");
        
        while (st.hasMoreTokens()) {
            if (hasNumbers0(st.nextToken())) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean hasNumbers0(String text) {
        
        StringTokenizer st = new StringTokenizer(text, ")");
        
        while (st.hasMoreTokens()) {
            if (hasNumbers1(st.nextToken())) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean hasNumbers1(String text) {

        StringTokenizer st = new StringTokenizer(text, "(");
        
        while (st.hasMoreTokens()) {
            if (isParsable(st.nextToken())) {
                return true;
            }
        }
        
        return false;
    }

    private static boolean isParsable(String str) {

        if (str.isEmpty()) {
            return false;
        }

        int start = 0;
        int end = str.length();
        
        while (true) {
            
            if (start >= end) {
                return false;
            }

            char head = str.charAt(start);
            
            if (head == '+' || head == '-' || head == '[' || head == '(') {
                start++;
            } else {
                break;
            }
        }
        
        while (true) {
            
            if (end <= 0) {
                return false;
            }

            char footer = Character.toLowerCase(str.charAt(end - 1));

            if (footer == '[' || footer == '(' || footer == ';' || footer == 'f' || footer == 'd' || footer == 'l') {
                end--;
            } else {
                break;
            }
        }

        if (start < 0 || end > str.length()) {
            return false;
        }
        
        boolean any = false;

        for (int j1 = start; j1 < end; j1++) {
            
            char c = str.charAt(j1);

            if (c == '.' || c == '(' || c == ')' || c == '[' || c == ']' || c == ';') {
                continue;
            }

            if (!Character.isDigit(c)) {
                return false;
            } else {
                any = true;
            }
        }

        return any;
    }

    public SourceResult heavyDecompile(ShieldClient client, String name, String hash, byte[] data) throws IOException {

        SourceResult result = new SourceResult();
        result.source = heavyDecompile(hash, data);
        
        heavyCompare(client, result, name);
        
        return result;
    }
    
    private String heavyDecompile(String hash, byte[] data) {
        
        if (data == null || data.length <= 0) {
            return hash;
        }
        
        File temp = new File("temp");
       
        if (!temp.exists()) {
            temp.mkdir();
        }
        
        File input = new File(temp, hash + "-" + Utils.currentTimeMillis() + ".class");

        try {
            
            FileUtils.setBytesOfFile(input, data);
            
            StringWriter writter = new StringWriter();

            OutputSinkFactory mySink = new OutputSinkFactory() {

                @Override
                public List<OutputSinkFactory.SinkClass> getSupportedSinks(OutputSinkFactory.SinkType sinkType, Collection<OutputSinkFactory.SinkClass> collection) {
                    return Collections.singletonList(OutputSinkFactory.SinkClass.STRING);
                }

                @Override
                public <T> OutputSinkFactory.Sink<T> getSink(OutputSinkFactory.SinkType sinkType, OutputSinkFactory.SinkClass sinkClass) {
                    return new OutputSinkFactory.Sink<T>() {
                        @Override
                        public void write(T t) {
                            if (sinkType == OutputSinkFactory.SinkType.JAVA || sinkType == OutputSinkFactory.SinkType.EXCEPTION) {
                                writter.append((CharSequence) t);
                            }
                        }
                    };
                }
            };
            
            HashMap<String, String> options = new HashMap<>();
            
            options.put("hidelongstrings", "true");
            options.put("silent", "true");
            options.put("showversion", "false");
            options.put("commentmonitors", "false");
            options.put("lenient", "true");
            options.put("comments", "false");
            options.put("recover", "false");
    
            CfrDriver driver = new CfrDriver.Builder().withOutputSink(mySink).withOptions(options).build();

            driver.analyse(Collections.singletonList(input.getAbsolutePath()));

            return writter.toString();
            
        } catch (Throwable ex) {

            ex.printStackTrace();
            return ex.getMessage();
            
        } finally {
            
            temp.delete();
            
        }
    }
    
    private String getRawLines(File file) throws IOException {
        return getRawLines(new FileInputStream(file));
    }
    
    private String getRawLines(InputStream is) throws IOException {
        
        StringBuilder sb = new StringBuilder();

        InputStreamReader reader = null;
        BufferedReader input = null;

        try {

            reader  = new InputStreamReader(is, Utils.UTF8);
            input   = new BufferedReader(reader);

            String line;
            while ((line = input.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } finally {
            
            Closer.closeQuietly(input);
            Closer.closeQuietly(reader);
            Closer.closeQuietly(is);
            
        }
        
        return sb.toString();
    }
}