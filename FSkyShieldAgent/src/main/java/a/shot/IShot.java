package a.shot;

import java.awt.image.BufferedImage;

/**
 *
 * @author lgpse
 */
public interface IShot {
    
    public BufferedImage screenshot();

    public int getX();
    
    public int getY();
    
    public int getWidth();
    
    public int getHeight();
    
}
