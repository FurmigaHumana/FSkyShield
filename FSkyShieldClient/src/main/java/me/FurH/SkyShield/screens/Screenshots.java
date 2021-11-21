/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.FurH.SkyShield.screens;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import me.FurH.Core.close.Closer;
import me.FurH.Core.http.HttpException;
import me.FurH.Core.util.Utils;
import me.FurH.SkyShield.ShieldClient;
import me.FurH.SkyShield.shot.Packet72ScreenData;

/**
 *
 * @author lgpse
 */
public class Screenshots {

    private final ShieldClient shield;

    public Screenshots(ShieldClient shield) {
        this.shield = shield;
    }

    public void shotError(int pid, String error, UUID requester) {
        shotMessage(pid, error, requester);
    }

    private void shotMessage(int pid, String message, UUID requester) {
        
        Packet72ScreenData packet = new Packet72ScreenData();

        packet.requester = requester;
        packet.pid = pid;
        packet.error = true;
        packet.message = message;
        
        shield.connection.write(packet);
    }

    public void uploadShot(int pid, byte[] data, UUID requester) {

        try {

            if (data.length > 70000) {
                BufferedImage scaled = scale(pid, data, ImageIO.read(new ByteArrayInputStream(data)), 960, requester);
                if (scaled != null) {
                    data = saveImage(scaled);
                }
            }

            shotMessage(pid, "Enviando captura... ( " + Utils.getFormatedBytes(data.length) + " )", requester);
            String ret = uploadPost(data);

            String key1 = "\"link\":\"";
            String key2 = "\"},";

            int index1 = ret.indexOf(key1);
            ret = ret.substring(index1 + key1.length());

            int index2 = ret.indexOf(key2);
            ret = ret.substring(0, index2).replace("\\", "");

            Packet72ScreenData packet = new Packet72ScreenData();

            packet.requester = requester;
            packet.pid = pid;
            packet.error = false;
            packet.url = ret;

            shield.connection.write(packet);
        
        } catch (Throwable ex) {

            ex.printStackTrace();
            this.shotError(pid, "Erro: " + ex.getMessage(), requester);

        }
    }

    private String uploadPost(byte[] bytes) throws IOException, HttpException {

        HttpURLConnection conn = null;
        OutputStreamWriter osw = null;
        OutputStream os = null;
        PrintWriter writer = null;
        InputStream is = null;

        try {

            String boundary = Long.toHexString(System.nanoTime());
            String CRLF = "\r\n";

            URL url = new URL("@@REMOVED");

            conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestMethod("POST");

            conn.setRequestProperty("@@REMOVED", "@@REMOVED");
            conn.setRequestProperty("Content-Type", "@@REMOVED" + boundary);
            conn.setRequestProperty("Charset", "UTF-8");

            os = conn.getOutputStream();

            os.write(new byte[] {
            });

            os.flush();

            osw = new OutputStreamWriter(os, Utils.UTF8);
            writer = new PrintWriter(osw, true);

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"image\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=UTF-8").append(CRLF);
            writer.append(CRLF).append("files").append(CRLF).flush();

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"skyshield.jpg\"").append(CRLF);
            writer.append("Content-Type: image/jpeg").append(CRLF);
            writer.append(CRLF).flush();

            os.write(bytes);

            os.flush();
            writer.append(CRLF).flush();

            writer.append("--" + boundary + "--").append(CRLF).flush();

            is = conn.getInputStream();
            int read;

            byte[] buffer = new byte[ 4096 ];

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }

            return new String(baos.toByteArray(), Utils.UTF8);

        } finally {

            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Throwable ex) { }
            }

            Closer.closeQuietly(writer);
            Closer.closeQuietly(osw);
            Closer.closeQuietly(os);
            Closer.closeQuietly(is);
        }
    }

    private byte[] saveImage(BufferedImage image) throws Throwable {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream mc = null;
        ImageWriter writer = null;

        try {

            writer = ImageIO.getImageWritersByFormatName("jpg").next();

            JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(0.45f);
            
            mc = new MemoryCacheImageOutputStream(baos);
            writer.setOutput(mc);
            writer.write(null, new IIOImage(image, null, null), jpegParams);

        } finally {

            if (writer != null) {
                try {
                    writer.dispose();
                } catch (Throwable ex1) { }
            }

            Closer.closeQuietly(mc);
        }

        return baos.toByteArray();
    }

    private BufferedImage scale(int pid, byte[] data, Image image, int max, UUID requester) {

        int w = image.getWidth(null);
        int h = image.getHeight(null);
        double newW;
        double newH;

        if (w == h) {
            newW = max;
            newH = max;
        } else if (w > h) {
            newW = max;
            newH = ((double) h / (double) w) * max;
        } else {
            newH = max;
            newW = ((double) w / (double) h) * max;
        }

        if (newH >= h || newW >= w) {
            return null;
        }

        shotMessage(pid, "Comprimindo captura... ( " + Utils.getFormatedBytes(data.length) + " )", requester);

        return toBufferedImage(image.getScaledInstance((int) newW, (int) newH, Image.SCALE_SMOOTH));
    }

    private BufferedImage toBufferedImage(Image image) {
        
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }
}