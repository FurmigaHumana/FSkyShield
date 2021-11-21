package me.FurH.SkyShield.win32;

public class NativeException extends Throwable {
    
    private int win32ErrorCode = -1;

    public NativeException() {
    }

    public NativeException(String message) {
        super(message);
    }

    public NativeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NativeException(Throwable cause) {
        super(cause);
    }

    public NativeException(String message, int lastError, String file, int line) {
        this(message+" error="+lastError+" at "+file+":"+line);
        win32ErrorCode = lastError;
    }

    public int getWin32ErrorCode() {
        return win32ErrorCode;
    }
}
