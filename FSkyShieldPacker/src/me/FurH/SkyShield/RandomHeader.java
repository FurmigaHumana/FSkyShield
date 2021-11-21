/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.list.InfiniteIterator;

/**
 *
 * @author Luis
 */
public class RandomHeader {

    private static InfiniteIterator<String> it;

    public static byte[] newHeader(int size) throws IOException {

        File dir = new File("@@REMOVED");

        if (it == null) {

            List<String> classes = new ArrayList<String>();

            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    classes.add(file.getName());
                }
            }

            Collections.shuffle(classes);

            it = new InfiniteIterator(classes);
        }

        return FileUtils.getBytesFromFile(new File(dir, it.next()), true);
    }
}
