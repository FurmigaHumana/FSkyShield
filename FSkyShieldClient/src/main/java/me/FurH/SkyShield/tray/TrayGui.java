package me.FurH.SkyShield.tray;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import me.FurH.JavaPacker.loader.ErrorGui;
import me.FurH.SkyShield.ShieldClient;
import me.FurH.SkyShield.gui.Popup;
import me.FurH.SkyShield.resources.Resources;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public final class TrayGui {

    public final TrayIcon trayIcon;
    private final ShieldClient shield;
    
    public TrayGui(final ShieldClient shield) {

        this.shield = shield;
                
        trayIcon = new TrayIcon(Resources.getIcon());
        trayIcon.setImageAutoSize(true);

        trayIcon.setToolTip("SkyShield");

        PopupMenu popup     = new PopupMenu();

        MenuItem restore    = new MenuItem("Restaurar");
        MenuItem exit       = new MenuItem("Fechar");

        popup.add(restore);
        popup.addSeparator();
        popup.add(exit);

        trayIcon.setPopupMenu(popup);

        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toFront();
            }
        });

        restore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toFront();
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shield.shutdown();
            }
        });
    }
    
    private void toFront() {
        shield.main_gui.setVisible(true);
        shield.main_gui.toFront();
    }

    public void display() {
        
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {

                try {
                    SystemTray.getSystemTray().add(trayIcon);
                } catch (AWTException ex) {
                    ErrorGui.error("Error on tray icon add", true, false, ex);
                }

                trayIcon.displayMessage("Sky Shield", "Estamos executando em plano de fundo para garantir sua segurança!", MessageType.INFO);
            }
        });
    }

    public void hide() {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                SystemTray.getSystemTray().remove(trayIcon);
//            }
//        });
    }
    
    public void setToolTip(String label) {
        trayIcon.setToolTip("SkyShield - " + label);
    }
    
    public void notify(String string) {
        
        if (!shield.main_gui.isNotificationSet()) {
            return;
        }
        
        Popup.success("SkyShield", string);
    }

    public void error(String string) {
        Popup.error("SkyShield", string);
    }

    public void neutral(String string) {
        
        if (!shield.main_gui.isNotificationSet()) {
            return;
        }
        
        Popup.neutral("SkyShield", string);
    }
}