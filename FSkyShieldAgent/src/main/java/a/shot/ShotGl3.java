package a.shot;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import me.FurH.SkyShield.win32.NativeShield;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ShotGl3 implements IShot {

    private final int width;
    private final int height;
    private final int x;
    private final int z;

    public ShotGl3() {

        String data = NativeShield.hwnd();
        String[] split = data.split(":");

        this.width = Integer.parseInt(split[0]);
        this.height = Integer.parseInt(split[1]);
        this.x = Integer.parseInt(split[2]);
        this.z = Integer.parseInt(split[3]);
    }
    
    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return z;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
    
    @Override
    public BufferedImage screenshot() {
                
        int paramInt1 = getWidth();
        int paramInt2 = getHeight();

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