package com.company;

import com.cisco.pt.impl.OptionsManager;
import com.cisco.pt.ipc.IPCFactory;
import com.cisco.pt.ipc.enums.FileOpenReturnValue;
import com.cisco.pt.ipc.system.ActivityFile;
import com.cisco.pt.ipc.ui.IPC;
import com.cisco.pt.ptmp.ConnectionNegotiationProperties;
import com.cisco.pt.ptmp.PacketTracerSession;
import com.cisco.pt.ptmp.PacketTracerSessionFactory;
import com.cisco.pt.ptmp.impl.PacketTracerSessionFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.cli.*;

public class Main {


    public Main(PacketTracerSession session) {
    }

    public static void main(String[] args)
            throws Exception {
        ArgsParser parser = new ArgsParser();
        var parsedArgs = parser.parse(args);

        String filepath = parsedArgs.get(ArgsNames.FILEPATH);
        String password = parsedArgs.get(ArgsNames.PASSWORD);
        int port = Integer.parseInt(parsedArgs.get(ArgsNames.PORT));
        String host = parsedArgs.get(ArgsNames.HOST);
        int connectionAttemptsNumber = Integer.parseInt(parsedArgs.get(ArgsNames.CONNECTION_ATTEMPTS_NUMBER));

        // Get activity file path
        var file = new File(filepath);
        try {
            filepath = file.getCanonicalPath();
        } catch (IOException e) {
            System.out.println("No such file: " + filepath);
            System.exit(1);
        }

        // Prepare for connection

        // Start by getting an instance of the PT Session factory
        PacketTracerSessionFactory packetTracerSessionFactory = PacketTracerSessionFactoryImpl.getInstance();

        // Get the options used to connect to PT
        ConnectionNegotiationProperties cnp = OptionsManager.getInstance().getConnectOpts();

        // Modify the default options to specify your application parameters
        cnp.setAuthenticationSecret(Constants.AUTH_SECRET);
        cnp.setAuthenticationApplication(Constants.AUTH_APP);
        cnp.setAuthentication(ConnectionNegotiationProperties.MD5_AUTH);

        PacketTracerSession packetTracerSession = null;
        for (int i = connectionAttemptsNumber; i > 0; i--) {
            try {
                // Create a PT session
                packetTracerSession = packetTracerSessionFactory.openSession(host, port, cnp);
            } catch (Error e) {
                System.out.println("Can not connect to Packet Tracer");
                if (i > 1) {
                    System.out.println("Trying to reconnect... Left connection times: " + (i - 1));
                    Thread.sleep(500);
                } else {
                    System.out.println("No more connection times. Exit program.");
                    System.exit(1);
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
        FileOpenReturnValue status = ipc.appWindow().fileOpen(filepath);
        if (status.compareTo(FileOpenReturnValue.FILE_RETURN_OK) != 0) {
            System.out.println("Can not open .pka file: " + filepath);
            System.exit(1);
        }
        var activityFile = (ActivityFile) ipc.appWindow().getActiveFile();
        // TODO если не может открыть файл, то выводит окно, где нужно нажать ОК

        // Generate password MD5 hash
        var challengeKey = activityFile.getChallengeKeyAsInts();
        var hashedPassword = hashPassword(password, challengeKey);

        // Get percentage
        var confirmed = activityFile.confirmPassword(hashedPassword);
        if (confirmed) {
            System.out.println("Percentage: " + activityFile.getPercentageComplete());
            System.out.println("Name: " + activityFile.getUserProfile().getName());
            System.out.println("Email: " + activityFile.getUserProfile().getEmail());
            System.out.println("PercentageScore: " + activityFile.getPercentageCompleteScore());
        } else {
            System.out.println("Wrong password");
            System.exit(1);
        }

        // Close session with Packet Tracer
        packetTracerSession.close();
    }

    private static String hashPassword(String password, List<Integer> challengeKey) throws NoSuchAlgorithmException {
        var salt = challengeKey.toArray();
        byte[] array = new byte[salt.length];
        for (int i = 0; i < salt.length; i++) {
            array[i] = (byte) (int) salt[i];
        }

        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(array);
        m.update(password.getBytes());
        byte s[] = m.digest();
        String result = "";
        for (int i = 0; i < s.length; i++) {
            result += Integer.toHexString((0x000000ff & s[i]) | 0xffffff00).substring(6);
        }
        return result.toUpperCase();
    }
}