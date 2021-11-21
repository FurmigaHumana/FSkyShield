package me.FurH.ShieldServer.newlistener;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import me.FurH.Core.arrays.ArrayUtils;
import me.FurH.Core.util.Utils;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class Encryption {
    
    public static String decrypt(String key, byte[] data) throws Exception {

        byte[] encrypted = Arrays.copyOf(data, data.length - 16);
        byte[] iv = Arrays.copyOfRange(data, data.length - 16, data.length);

        byte[] decrypted = aes_decrypt(encrypted, key, iv);
        byte[] decompressed = ArrayUtils.decompress(decrypted);
        
        return new String(decompressed, Utils.UTF8);
    }
    
    public static byte[] encrypt(String key, String rawdata) throws Exception {

        byte[] compressed = ArrayUtils.compress(rawdata.getBytes(Utils.UTF8), 1);
        byte[] encrypted = aes_encrypt(compressed, key);
        
        return encrypted;
    }
    
    private static byte[] aes_encrypt(byte[] data, String password) throws Exception {

        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "@@REMOVED");
       
        SecureRandom randomSecureRandom = SecureRandom.getInstance("@@REMOVED");
        byte[] iv = new byte[16];
        randomSecureRandom.nextBytes(iv);

        byte[] cipher = doFinal(data, key, "@@REMOVED", iv, Cipher.ENCRYPT_MODE);
        byte[] result = Arrays.copyOf(cipher, cipher.length + 16);
        
        System.arraycopy(iv, 0, result, cipher.length, iv.length);
        
        return result;
    }
    
    private static byte[] aes_decrypt(byte[] data, String password, byte[] iv) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "@@REMOVED");
        return doFinal(data, key, "@@REMOVED", iv, Cipher.DECRYPT_MODE);
    }
    
    private static byte[] doFinal(byte[] data, Key key, String algorithm, byte[] iv_spec, int cipher_mode) throws Exception {
        
        Cipher cipher = Cipher.getInstance(algorithm);

        if (iv_spec != null) {
            cipher.init(cipher_mode, key, new IvParameterSpec(iv_spec));
        } else {
            cipher.init(cipher_mode, key);
        }

        return cipher.doFinal(data);
    }
}