package com.packettracer.grader;

import com.cisco.pt.impl.OptionsManager;
import com.cisco.pt.ipc.IPCFactory;
import com.cisco.pt.ipc.enums.FileOpenReturnValue;
import com.cisco.pt.ipc.system.ActivityFile;
import com.cisco.pt.ipc.ui.IPC;
import com.cisco.pt.ptmp.ConnectionNegotiationProperties;
import com.cisco.pt.ptmp.PacketTracerSession;
import com.cisco.pt.ptmp.PacketTracerSessionFactory;
import com.cisco.pt.ptmp.impl.PacketTracerSessionFactoryImpl;
import com.packettracer.grader.exceptions.*;

import java.util.List;

public class MyRunnable implements Runnable {
    private static final String AUTH_SECRET = "cisco";
    private static final String AUTH_APP = "com.packettracer.grader";

    private ActivityData activityData;
    private Integer returnCode;

    private String source;
    private final String password;
    private final String host;
    private final Integer port;
    private final Integer attempts;
    private final Integer delay;

    MyRunnable(String source, String password, String host, Integer port, Integer attempts, Integer delay) {
        this.source = source;
        this.password = password;
        this.host = host;
        this.port = port;
        this.attempts = attempts;
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            source = Grader.formatActivityFilePath(source);

            // Prepare for connection

            // Start by getting an instance of the PT Session factory
            PacketTracerSessionFactory packetTracerSessionFactory = PacketTracerSessionFactoryImpl.getInstance();

            // Get the options used to connect to PT
            ConnectionNegotiationProperties cnp = OptionsManager.getInstance().getConnectOpts();

            // Modify the default options to specify your application parameters
            cnp.setAuthenticationSecret(AUTH_SECRET);
            cnp.setAuthenticationApplication(AUTH_APP);
            cnp.setAuthentication(ConnectionNegotiationProperties.MD5_AUTH);

            PacketTracerSession packetTracerSession = null;
            try {
                for (int i = attempts; i > 0; i--) {
                    try {
                        // Create a PT session
                        packetTracerSession = packetTracerSessionFactory.openSession(host, port, cnp);
                    } catch (Error e) {
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
                final IPC ipc = ipcFactory.getIPC();

                // Get percentage of completed

                // Open activity file
                FileOpenReturnValue status = null;
                try {
                    status = ipc.appWindow().fileOpen(source);
                } catch (Error e) {
                    throw new WrongCredentialsError("Wrong credentials", e);
                }
                if (status.compareTo(FileOpenReturnValue.FILE_RETURN_OK) != 0) {
                    throw new SourceFileReadingError(String.format("Can not open .pka file: %s", source));
                }
                ActivityFile activityFile = (ActivityFile) ipc.appWindow().getActiveFile();
                // TODO если не может открыть файл, то выводит окно, где нужно нажать ОК

                // Generate password MD5 hash
                List<Integer> challengeKey = activityFile.getChallengeKeyAsInts();
                String hashedPassword = Grader.hashPassword(password, challengeKey);

                // Get percentage
                boolean confirmed = activityFile.confirmPassword(hashedPassword);
                if (!confirmed) {
                    throw new WrongPasswordError("Wrong password");
                } else {
                    activityData = new ActivityData(activityFile);
                    packetTracerSession.close();
                }

            } catch (Exception e) {
                packetTracerSession.close();
                throw e;
            }
            returnCode = 0;
        } catch (BaseGraderError e) {
            returnCode = e.getExitStatus().getReturnCode();
        } catch (Exception e) {
            returnCode = Constants.ExitStatus.GENERAL_ERROR.getReturnCode();
        }
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public ActivityData getActivityData() {
        return activityData;
    }
}
