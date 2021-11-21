package me.FurH.ShieldGen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import me.FurH.Core.close.Closer;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.file.SimpleVisitor;
import me.FurH.Core.util.Utils;
import me.FurH.SkyShield.packets.scan.ZipHash2;
import static me.FurH.SkyShield.scanner.FileHash.CLASS_HEADER;
import static me.FurH.SkyShield.scanner.FileHash.ZIP_HEADER;
import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Main implements SimpleVisitor {
    
    private static LinkedHashMap<String, String> sourceindex;
    private static LinkedHashMap<String, String> hashdb;
    private static LinkedHashMap<String, String> libdb;
    
    private static File basedir;
    private static File sourcedir;
    
    // current visit dir
    private LinkedHashMap<String, String> db;
    private File hashdir;
    private File dir;
    

    
    public static void main(String[] args) throws Exception {
        
        basedir = new File("@@REMOVED");        
        
        File hashfile = new File(basedir, "hashes.txt");
        File libfile = new File(basedir, "hashes.txt");

        sourcedir = new File(basedir, "sources");
        sourcedir.mkdirs();
        
        hashdb = loadDb(hashfile);
        libdb = loadDb(libfile);
        
        File source = new File(basedir, "sources.txt");
        sourceindex = loadDb(source);

        try {

            File jardir = new File(basedir, "jars");
            
            File hashdir = new File(basedir, "hashes");
            hashdir.mkdirs();

            File libhashdir = new File(hashdir, "libraries");
            libhashdir.mkdirs();
            
            File libdirs = new File(jardir, "libraries");
            visitDir(libdirs, libhashdir, libdb);

            File ordir = new File(jardir, "original");
            visitDir(ordir, hashdir, hashdb);
           
            File optdir = new File(jardir, "optifine");
            visitDir(optdir, hashdir, hashdb);
            
            File zig5 = new File(jardir, "mod5zig");
            visitDir(zig5, hashdir, hashdb);
                        
        } finally {
            
            saveDb(hashfile, hashdb);
            saveDb(source, sourceindex);
            saveDb(libfile, libdb);
            
        }
    }
    
    public static void visitDir(File dir, File hashdir, LinkedHashMap<String, String> db) throws IOException {
       
        Main main = new Main();
        
        main.dir = dir;
        main.db = db;
        main.hashdir = hashdir;

        FileUtils.visitAllFilesAt(main, dir);
    }
    
    @Override
    public void visit(File file) {
        
        if (!file.getName().endsWith(".jar")) {
            return;
        }
        
        try {
            
            String hash = Encrypto.hash("MD5", file);
            
            if (db.containsKey(hash)) {
                return;
            }
            
            System.out.println("Hashing: " + file.getName());
            
            splitHash(file, hash);
            
            db.put(hash, dir.getName() + "/" + file.getName());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void splitHash(File file, String hash) throws Exception {
        
        ArrayList<String> hashes = new ArrayList<>();

        MessageDigest md2 = MessageDigest.getInstance("MD5");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        BufferedInputStream bis = null;
        FileInputStream fis     = null;
        ZipInputStream zis      = null;

        try {

            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            byte[] buffer   = new byte[ 1024 * 8 ];
            int header      = getInputHead(bis);

            if (header == ZIP_HEADER) {

                zis = new ZipInputStream(bis, Charset.forName("CP866"));

                ZipEntry entry = zis.getNextEntry();

                while (entry != null) {
                    
                    baos.reset();

                    boolean inside = false;
                    Integer ehead = null;
                    
                    int size = 0;
                    int read;

                    while ((read = zis.read(buffer)) > 0) {

                        if (ehead == null) {
                            ehead = getInt(buffer, 0);
                        }

                        if (ehead == ZIP_HEADER || ehead == CLASS_HEADER) {
                            
                            inside = true;
                            size += read;
                            
                            baos.write(buffer, 0, read);
                            md2.update(buffer, 0, read);
                        }
                    }

                    if (inside) {

                        ZipHash2 zhash = new ZipHash2(true);

                        zhash.md5 = md2.digest();
                        zhash.name = entry.getName();
                        zhash.size = size;
                                
                        int index = zhash.name.lastIndexOf('/');
                        
                        if (index > 0) {
                            zhash.name = zhash.name.substring(index + 1);
                        }
                        
                        index = zhash.name.lastIndexOf('.');
                        if (index > 0) {
                            zhash.name = zhash.name.substring(0, index);
                        }

                        String hex = Encrypto.hex(zhash.md5);
                        hashes.add(hex + "," + zhash.size + "," + zhash.name);

                        sourceindex.put(hex, zhash.name);

                        File source = new File(sourcedir, hex + ".java");

                        if (!source.exists()) {
                            String decompiled = decompile(baos.toByteArray());
                            FileUtils.setLineOfFile(source, decompiled);
                        }
                    }

                    entry = zis.getNextEntry();
                }
            }
            
            if (!hashes.isEmpty()) {
                FileUtils.setLinesOfFile(new File(hashdir, dir.getName() + "-" + FileUtils.getFileName(file) + "-" + hash + "-.txt"), hashes);
            }
            
        } finally {

            Closer.closeQuietly(fis);
            Closer.closeQuietly(bis);
            Closer.closeQuietly(zis);

        }
    }
    
    private String decompile(byte[] data) throws IOException {

        File input = new File(basedir, "temp.class");
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
    }
    
    private static void saveDb(File file, LinkedHashMap<String, String> map) throws IOException {
            
        ArrayList<String> list = new ArrayList<>();

        for (Entry<String, String> entry : map.entrySet()) {
            list.add(entry.getKey() + ":" + entry.getValue());
        }

        FileUtils.setLinesOfFile(file, list);
    }
    
    private static LinkedHashMap<String, String> loadDb(File file) throws IOException {
        
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        
        if (!file.exists()) {
            return map;
        }
        
        ArrayList<String> lines = FileUtils.getLinesFromFile(file);
        
        for (String line : lines) {
            map.put(line.substring(0, 32), line.substring(33));
        }

        return map;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
    
    private static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF)      ) +
               ((b[off + 2] & 0xFF) <<  8) +
               ((b[off + 1] & 0xFF) << 16) +
               ((b[off    ]       ) << 24);
    }

    private static int getInputHead(BufferedInputStream in) {

        byte[] b = new byte[ 4 ];
        in.mark(b.length);

        try {

            for (int i = 0; i < b.length; i++) {
                b[ i ] = (byte) in.read();
            }

        } catch (IOException e) {

            return 0;

        } finally {
            try {
                in.reset();
            } catch (IOException ex) { }
        }

        return getInt(b, 0);
    }
    
    public static String getRawLines(File file) throws IOException {
        return getRawLines(new FileInputStream(file));
    }
    
    public static String getRawLines(InputStream is) throws IOException {
        
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