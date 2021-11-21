package me.FurH.SkyShield.Constants;

/**
 *
 * @author lgpse
 */
public interface HashProvider {

    public byte[] hash(byte[] data) throws Exception;
    
}
