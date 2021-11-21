package me.FurH.SkyShield;

import flzma.LzmaUtils;
import java.io.File;
import java.util.ArrayList;
import me.FurH.Core.encript.Encrypto;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.zip.ZipUtils;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class SetupBuilder {
    
    public static void packFiles() throws Exception {

        File dir = new File("@@REMOVED");
        File hashdb = new File(dir, "hashes.txt");
        
        ArrayList<String> hashes;
        
        if (hashdb.exists()) {
            hashes = FileUtils.getLinesFromFile(hashdb);
        } else {
            hashes = new ArrayList<>();
            for (int j1 = 0; j1 < 6; j1++) {
                hashes.add("");
            }
        }
        
        packFolder(dir, "common", hashes, 0);
        packFolder(dir, "x32", hashes, 1);
        packFolder(dir, "x64", hashes, 2);

        FileUtils.setLinesOfFile(hashdb, hashes);
    }

    private static void packFolder(File dir, String folder, ArrayList<String> hashes, int index) throws Exception {

        File zipout = new File(dir, folder + ".zip");
        File common = new File(dir, folder);

        ZipUtils.zipDir(common, zipout, 0, null, 1549325499489L);

        String ziphash = Encrypto.hash("MD5", zipout);
        File ret = new File(dir, folder + ".tz");

        if (!hashes.get(index).equals(ziphash)) {
            new LzmaUtils().compress(zipout, ret);
        }

        hashes.set(index, ziphash);
        hashes.set(index + 3, Encrypto.hash("MD5", ret) + "," + ret.length());
    }
}