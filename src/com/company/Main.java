package com.company;

import com.cisco.pt.impl.OptionsManager;
import com.cisco.pt.ipc.IPCFactory;
import com.cisco.pt.ipc.ui.IPC;
import com.cisco.pt.ptmp.ConnectionNegotiationProperties;
import com.cisco.pt.ptmp.PacketTracerSession;
import com.cisco.pt.ptmp.PacketTracerSessionFactory;
import com.cisco.pt.ptmp.impl.PacketTracerSessionFactoryImpl;

import java.io.File;



class Main
{
    private static final int PORT = 39000;
    private static final String HOST = "localhost";

    public static void main(String args[])
            throws Exception
    {
        File file;
        if (args.length != 1) {
            System.err.println("You need to specify a pka file to grade");
            System.exit(1);
        }
        file = new File(args[0]);
        System.out.println(file.getCanonicalPath());

        // Start by getting an instance of the PT Session factory
        PacketTracerSessionFactory packetTracerSessionFactory = PacketTracerSessionFactoryImpl.getInstance();

        // Get the options used to connect to PT
        ConnectionNegotiationProperties cnp = OptionsManager.getInstance().getConnectOpts();

        // Modify the default options to specify your application parameters
        cnp.setAuthenticationSecret("cisco");
        cnp.setAuthenticationApplication("net.netacad.cisco.javafwtest");

        // Create a PT session
        PacketTracerSession packetTracerSession = packetTracerSessionFactory.openSession(HOST, PORT, cnp);

        // Get the top level IPC object to communicate with PT
        IPCFactory ipcFactory = new IPCFactory(packetTracerSession);
        final IPC ipc = ipcFactory.getIPC();

//        PacketTracerLauncher launcher = PacketTracerLauncher.getInstance();
//        Process packet_tracer_process = launcher.launch(PORT, true);


//        PacketTracerSessionFactory session_factory = PacketTracerSessionFactoryImpl.getInstance();
//        PacketTracerSession session = session_factory.openSession(HOST, PORT);
////        IPCFactory ipc_factory = new IPCFactory(session);
////        IPC ipc = ipc_factory.getIPC();
//
//        System.out.println(session.getSessionStatus().toString());
//        if (packet_tracer_process != null) {
//            packet_tracer_process.destroy();
//        }


        int a = ipc.network().getLinkCount();
        System.out.println(a);

//        ipc.appWindow().fileOpen(file.getCanonicalPath());

//        AppWindow window = ipc.appWindow();
//        ActivityFileEventRegistry registry = session.getEventManager().getActivityFileEvents();
//        FileOpenReturnValue status = window.fileOpen(file.getCanonicalPath());
//        System.out.println(status.getIntValue());
//        ActivityFileImpl activity = ;
//        pt.
//        packettracer = new PacketTracer("localhost", 39000, true);
//        packettracer.launch();
//        packettracer.connect();
//        packettracer.fileOpen(file.getCanonicalPath());
//
//        ActivityFile activityfile = packettracer.getActivityFile();
//        Double s = activityfile.getPercentageComplete();
//
//        System.out.println((new StringBuilder()).append(s).append("\t").append(args[0]).toString());
//
//        packettracer.shutDown();
    }
}
