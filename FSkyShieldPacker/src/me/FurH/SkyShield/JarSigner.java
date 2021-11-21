package me.FurH.SkyShield;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import me.FurH.Core.encript.Encrypto;
import static me.FurH.SkyShield.Packer.nextPass;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class JarSigner {
    
    public static File signJar(File sign_in, File sign_out) throws Exception {
       
        System.out.println("Signing rsa file...");

        Files.copy(sign_in.toPath(), sign_out.toPath(), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Generating passwords...");

        String keypass      = nextPass();
        String storepass    = keypass;//nextPass();

        System.out.println("Keypass: " + keypass);
        System.out.println("Storapass: " + storepass);

        File keystore = new File("temp", "keystore_" + Encrypto.hash("MD5", keypass + storepass) + ".keystore");
        keystore.getParentFile().mkdirs();
        
        String alias = sign_in.getName().replaceAll("[^A-Za-z0-9]", "");
        System.out.println("Generating keystore file, alias: " + alias);

        int result = CmUtils.exec(new String[] {
        "@@REMOVED"});

        if (result != 0) {
            throw new IOException("Keytool error code " + result);
        }

        System.out.println("Signing jar...");

        result = CmUtils.exec(new String[] { "@@REMOVED" } );

        if (result != 0) {
            throw new IOException("Signer error code " + result);
        }
        
        return sign_out;
    }
}