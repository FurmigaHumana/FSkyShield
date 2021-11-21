package me.FurH.SkyShield.win32;

public class NativeShield {
    
    private static boolean elevated = false;
    
    static {
        System.loadLibrary("shield");
    }
    
    public static byte[] readCmdLines(int port, String key) {
        enableDebugPrivilege();
        return action1(port, key);
    }
    
    private native static byte[] action1(int port, String key);

    public static byte[] readProcessList(String key) {
        enableDebugPrivilege();
        return action2(key);
    }

    private native static byte[] action2(String key);
    
    public static byte[] fileInfo(String key, byte[] path) {
        return action3(key, path);
    }

    private native static byte[] action3(String key, byte[] path);
    
    public static byte[] fileList(String key, byte[] path) {
        return action4(key, path);
    }

    private native static byte[] action4(String key, byte[] path);

    public static byte[] currentPid(String key) {
        enableDebugPrivilege();
        return action5(key);
    }

    private native static byte[] action5(String key);

    public static byte[] hardwareId(String key) {
        return action6(key);
    }
    
    private native static byte[] action6(String key);

    public static boolean isWow64(int pid) {
        enableDebugPrivilege();
        return !is64(pid);
    }
    
    private native static boolean is64(int pid);
        
    public static native long lastInput();
    
    public static byte[] listModules(String key) {
        enableDebugPrivilege();
        return action7(key);
    }
    
    private static native byte[] action7(String key);
    
    public static byte[] md5(byte[] data, String key) {
        return action8(data, key);
    }
    
    private static native byte[] action8(byte[] data, String key);
    
    public static byte[] encryptResult(byte[] data, byte[] md5, String key) {
        return action9(data, md5, key);
    }

    private static native byte[] action9(byte[] data, byte[] md5, String key);
    
    public static native String hwnd();
    
    private static void enableDebugPrivilege() {

        if (elevated) {
            return;
        }

        elevated = true;
        enableDebugPrivilege0();
    }

    private native static void enableDebugPrivilege0();

}
