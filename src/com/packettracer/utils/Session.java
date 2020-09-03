package com.packettracer.utils;

import com.cisco.pt.impl.OptionsManager;
import com.cisco.pt.ipc.IPCFactory;
import com.cisco.pt.ipc.ui.IPC;
import com.cisco.pt.ptmp.ConnectionNegotiationProperties;
import com.cisco.pt.ptmp.PacketTracerSession;
import com.cisco.pt.ptmp.PacketTracerSessionFactory;
import com.cisco.pt.ptmp.impl.PacketTracerSessionFactoryImpl;
import com.packettracer.grader.exceptions.ConnectionError;

import java.io.IOException;

public class Session {
    private static final String AUTH_SECRET = "cisco";
    private static final String AUTH_APP = "com.packettracer.grader";

    private final IPC ipc;
    private PacketTracerSession packetTracerSession;

    public Session(String host, Integer port, Integer attempts, Integer delay) throws InterruptedException, ConnectionError {
        // Prepare for connection

        // Start by getting an instance of the PT Session factory
        PacketTracerSessionFactory packetTracerSessionFactory = PacketTracerSessionFactoryImpl.getInstance();

        // Get the options used to connect to PT
        ConnectionNegotiationProperties cnp = OptionsManager.getInstance().getConnectOpts();

        // Modify the default options to specify your application parameters
        cnp.setAuthenticationSecret(AUTH_SECRET);
        cnp.setAuthenticationApplication(AUTH_APP);
        cnp.setAuthentication(ConnectionNegotiationProperties.MD5_AUTH);

        for (int i = attempts; i > 0; i--) {
            try {
                // Create a PT session
                packetTracerSession = packetTracerSessionFactory.openSession(host, port, cnp);
            } catch (Error | IOException e) {
                System.out.println("Can not connect to Packet Tracer");
                if (i > 1) {
                    System.out.println("Trying to reconnect... Left connection times: " + (i - 1));
                    Thread.sleep(delay);
                } else {
                    throw new ConnectionError(String.format("Unable to connect to %s:%s", host, port), e);
                }
                continue;
            }
            break;
        }

        // Get the top level IPC object to communicate with PT
        IPCFactory ipcFactory = new IPCFactory(packetTracerSession);
        ipc = ipcFactory.getIPC();
    }

    public void close() throws IOException {
        packetTracerSession.close();
    }

    public IPC getIpc() {
        return ipc;
    }
}
