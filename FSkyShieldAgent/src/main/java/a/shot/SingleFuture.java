/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a.shot;

import static a.Agent.print;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

/**
 *
 * @author lgpse
 */
public class SingleFuture implements Runnable {

    private final Object lock = new Object();
    BufferedImage captured;
    private final IShot ishot;
    
    public SingleFuture(IShot ishot) {
        this.ishot = ishot;
    }

    @Override
    public void run() {
        consume(ishot);
    }

    public void consume(IShot handler) {

        synchronized (lock) {

            if (captured != null) {
                return;
            }

            try {

                BufferedImage image;

                if (handler != null) {

                    image = handler.screenshot();

                } else {

                    System.setProperty("java.awt.headless", "false");
                    
                    Rectangle size = new Rectangle(ishot.getX(), ishot.getY() + 10, ishot.getWidth(), ishot.getHeight());
                    size.grow(30, 30);

                    image = new Robot().createScreenCapture(size);
                }

                captured = image;

            } catch (Throwable ex) {

                print(ex);

            }
        }
    }
}
