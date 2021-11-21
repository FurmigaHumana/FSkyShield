/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a;

import me.FurH.JavaPacker.loader.AClassLoader;

/**
 *
 * @author lgpse
 */
public class z {

    /*public static void agentmain(String args, Instrumentation instrumentation) throws Throwable {
        
        // mp,hw,cfi,ciz,
        
        String input = "mp,hw,cft,cjk,jy,pc";
        
        ProtocolOption opt = new ProtocolOption(input);
        boolean supported = opt.isSupported(null);
        
        System.out.println("supported: " + supported);
    }

    public static void main(String[] args) throws Exception {
        
        for (VirtualMachineDescriptor machine : VirtualMachine.list()) {
            if (machine.displayName().contains("net.minecraft.client.main.Main")) {
                System.out.println("Machine: " + machine.displayName());
                VirtualMachine vm = VirtualMachine.attach(machine.id());
                vm.loadAgent("D:\\Java\\SkyShield\\FSkyShieldAgent\\target\\FSkyShieldAgent-1.0-SNAPSHOT.jar");
                return;
            }
        }
        
        if (true) {
            return;
        }
    }*/
    
//    public static void agentmain(String args, Instrumentation instrumentation) throws Throwable {
//        Agent.agentmain(args, instrumentation);
//    }
    
    public static void main(String[] args) throws Exception {
        try {
            Agent.agentmain(args[0], AClassLoader.getInstrumentation());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}