package a.shot;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ShotGl2 implements IShot {

    @Override
    public int getX() {
        return Display.getX();
    }

    @Override
    public int getY() {
        return Display.getY();
    }

    @Override
    public int getWidth() {
        return Display.getWidth();
    }

    @Override
    public int getHeight() {
        return Display.getHeight();
    }
    
    @Override
    public BufferedImage screenshot() {
        
        int paramInt1 = Display.getWidth();
        int paramInt2 = Display.getHeight();

        int i = paramInt1 * paramInt2;

        IntBuffer c = BufferUtils.createIntBuffer(i);
        int[] d = new int[i];

        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);

        c.clear();

        GL11.glReadPixels(0, 0, paramInt1, paramInt2, 32993, 33639, c);
        c.get(d);

        int[] arrayOfInt = new int[paramInt1];
        int j1 = paramInt2 / 2;

        for (int j = 0; j < j1; j++) {
            System.arraycopy(d, j * paramInt1, arrayOfInt, 0, paramInt1);
            System.arraycopy(d, (paramInt2 - 1 - j) * paramInt1, d, j * paramInt1, paramInt1);
            System.arraycopy(arrayOfInt, 0, d, (paramInt2 - 1 - j) * paramInt1, paramInt1);
        }

        BufferedImage image = new BufferedImage(paramInt1, paramInt2, 1);
        image.setRGB(0, 0, paramInt1, paramInt2, d, 0, paramInt1);
        
        return image;
    }
}