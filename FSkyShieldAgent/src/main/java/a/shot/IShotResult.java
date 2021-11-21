package a.shot;

/**
 *
 * @author lgpse
 */
public interface IShotResult {

    public void error(String string, long bits1, long bits2);

    public void success(byte[] toByteArray, long bits1, long bits2);
    
}
