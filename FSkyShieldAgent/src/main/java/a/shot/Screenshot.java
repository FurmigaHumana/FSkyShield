/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a.shot;

import static a.Agent.print;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.FutureTask;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 *
 * @author lgpse
 */
public class Screenshot {
    
    private static ArrayList<Queue> possibles;

    private static void shotScreens(long bits1, long bits2, IShotResult result) {

        for (Window w : Window.getWindows()) {

            Graphics g = null;

            try {

                Dimension size = w.getSize();

                BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
                g = img.getGraphics();

                boolean visible = w.isVisible();
                
                if (!visible) {
                    w.setVisible(true);
                }

                w.paintAll(g);

                if (w.isVisible() != visible) {
                    w.setVisible(visible);
                }

                saveImage(img, bits1, bits2, result);

            } catch (Throwable ex) {

                result.error("Erro na captura de janelas: " + ex.getMessage(), bits1, bits2);

            } finally {

                if (g != null) {
                    try {
                        g.dispose();
                    } catch (Throwable ex) { }
                }
            }
        }
    }
    
    private static IShot newIShot() {
        try {
            Class.forName("org.lwjgl.opengl.Display");
            return new ShotGl2();
        } catch (Throwable ex) {
            return new ShotGl3();
        }
    }

    public static void screenshot(Instrumentation instrumentation, long bits1, long bits2, IShotResult result) {
        
        try {

            try {
                shotScreens(bits1, bits2, result);
            } catch (Throwable ex) {
                result.error("Erro na lista de janelas: " + ex.getMessage(), bits1, bits2);
            }

            IShot handler = newIShot();
            SingleFuture task = new SingleFuture(handler);

            if (possibles == null) {

                possibles = new ArrayList<Queue>();
                
                for (Class cls : instrumentation.getAllLoadedClasses()) {

                    try {

                        Field[] fields1 = cls.getDeclaredFields();
                        Field[] fields2 = cls.getFields();

                        if (getFieldOfType(fields1, fields2, Thread.class) == null) {
                            continue;
                        }

                        Field queueField = getFieldOfType(fields1, fields2, Queue.class);
                        if (queueField == null) {
                            continue;
                        }

                        Field localField = getFieldOfType(fields1, fields2, cls);
                        if (localField == null) {
                            continue;
                        }

                        Object self = localField.get(null);
                        Queue queue = (Queue) queueField.get(self);
                        possibles.add(queue);
 
                    } catch (Throwable ex) { }
                }
            }

            if (possibles.isEmpty()) {
                result.error("A fila principal não foi encontrada", bits1, bits2);
                return;
            }

            int hits = 0;

            for (Queue queue : possibles) {
                try {
                    queue.add(new FutureTask(task, null));
                    hits++;
                } catch (Throwable ex) { }
            }

            if (hits == 0) {
                result.error("A fila principal esta inacessível", bits1, bits2);
                return;
            }

            for (int j1 = 0; task.captured == null && j1 < 20; j1++) {
                Thread.sleep(50);
            }

            task.consume(null);
            
            if (task.captured != null) {
                saveImage(task.captured, bits1, bits2, result);
            } else {
                result.error("Não foi possível capturar a imagem", bits1, bits2);
            }

        } catch (Throwable ex) {

            result.error("Erro fatal: " + ex.getMessage(), bits1, bits2);
            print(ex);
            
        }
    }
    
    private static void saveImage(BufferedImage image, long bits1, long bits2, IShotResult result) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        MemoryCacheImageOutputStream ios = null;
        ImageWriter writer = null;

        try {

            writer = ImageIO.getImageWritersByFormatName("jpg").next();

            JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(0.30f);

            ios = new MemoryCacheImageOutputStream(baos);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), jpegParams);

            result.success(baos.toByteArray(), bits1, bits2);

        } catch (Throwable ex) {

            result.error("Erro fatal 2: " + ex.getMessage(), bits1, bits2);
            print(ex);
            
        } finally {

            if (writer != null) {
                try {
                    writer.dispose();
                } catch (Throwable ex1) { }
            }

            if (ios != null) {
                try {
                    ios.close();
                } catch (Throwable ex1) { }
            }
        }
    }

    private static Field getFieldOfType(Field[] fields1, Field[] fields2, Class<?> type) {

        Field field = getFieldOfType(fields1, type);
        if (field == null) {
            field = getFieldOfType(fields2, type);
        }

        return field;
    }
    
    private static Field getFieldOfType(Field[] fields, Class<?> type) {

        for (Field field : fields) {
            field.setAccessible(true);
            if (type.isAssignableFrom(field.getType())) {
                return field;
            }
        }

        return null;
    }
}