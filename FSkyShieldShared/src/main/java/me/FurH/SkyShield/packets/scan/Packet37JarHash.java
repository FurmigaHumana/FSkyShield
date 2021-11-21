/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.packets.scan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import me.FurH.Core.close.Closer;
import me.FurH.NIO.executor.ExecutorThread;
import me.FurH.NIO.executor.ThreadCache;
import me.FurH.NIO.packet.Packet;
import me.FurH.NIO.stream.DataInputStream;
import me.FurH.NIO.stream.DataOutputStream;
import me.FurH.SkyShield.PacketListener;

/**
 *
 * @author lgpse
 */
public class Packet37JarHash extends Packet<PacketListener> {
    
    public int pid;
    public ArrayList<ZipHash2> hashes;
   
    private byte[] compressed0;

    @Override
    public short getId() {
        return 37;
    }

    @Override
    public void handle(PacketListener listener) {
        listener.scanHashes(this);
    }

    @Override
    public void writeExternal(DataOutputStream out0) throws IOException {

        ExecutorThread t = (ExecutorThread) Thread.currentThread();
        ThreadCache cache = t.getThreadCache();

        ByteArrayOutputStream baos = cache.getByteArray();
        baos.reset();

        Deflater deflater = cache.getDeflater();
        deflater.reset();

        deflater.setLevel(9);

        java.io.DataOutputStream out = null;
        DeflaterOutputStream oos = null;

        try {

            oos = new DeflaterOutputStream(baos, deflater, false);
            out = new java.io.DataOutputStream(oos);

            out.writeInt(pid);

            out.writeInt(hashes.size());
            
            for (ZipHash2 result : hashes) {
                result.write(out);
            }

            out.close();

            compressed0 = baos.toByteArray();

            out0.writeInt(compressed0.length);
            out0.write(compressed0);

        } finally {

            Closer.closeQuietly(out);
            Closer.closeQuietly(oos);

        }
    }

    @Override
    public void readExternal(DataInputStream in0) throws IOException {
        
        compressed0 = new byte[ in0.readInt() ];
        in0.readFully(compressed0);

        ExecutorThread t = (ExecutorThread) Thread.currentThread();
        ThreadCache cache = t.getThreadCache();

        ByteArrayInputStream bais = new ByteArrayInputStream(compressed0);

        Inflater inflater = cache.getInflater();
        inflater.reset();

        java.io.DataInputStream in = null;
        InflaterInputStream iis = null;

        try {

            iis = new InflaterInputStream(bais, inflater);
            in  = new java.io.DataInputStream(iis);

            pid         = in.readInt();

            hashes      = new ArrayList<>();
            int size    = in.readInt();

            for (int j1 = 0; j1 < size; j1++) {

                ZipHash2 result = new ZipHash2(false);
                result.read(in);

                hashes.add(result);
            }

            in.close();

        } finally {

            Closer.closeQuietly(in);
            Closer.closeQuietly(iis);

        }
    }
}