package a;

import java.io.DataOutputStream;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface WriteCommand {

    public void write(DataOutputStream dos) throws Throwable;

}