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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.packettracer.grader.args.Args;
import com.packettracer.grader.args.ArgsParser;
import com.packettracer.grader.exceptions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

public class Grader {


    public Grader(PacketTracerSession session) {
    }

    public static void main(String[] args) throws Exception {

        ArgsParser parser = new ArgsParser();
        Args parsedArgs = null;

        try {
            parsedArgs = parser.parse(args);
        } catch (ParseError e) {
            System.out.println(e.getMessage());
            parser.printHelp();
            System.exit(Constants.EXIT_STATUS_ARGUMENTS_PARSING_FAILED);
        }

        String source = parsedArgs.getSource();
        String target = parsedArgs.getTarget();
        String password = parsedArgs.getKey();
        String host = parsedArgs.getHost();
        int port = parsedArgs.getPort();
        int attempts = parsedArgs.getAttempts();
        int delay = parsedArgs.getDelay();

        // Get canonical source file path
        try {
            File file = new File(source);
            source = file.getCanonicalPath();
        } catch (IOException e) {
            throw new SourceFileReadingError(e.getMessage(), e);
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
            }
            catch (Error e) {
                throw new WrongCredentialsError("Wrong credentials", e);
            }
            if (status.compareTo(FileOpenReturnValue.FILE_RETURN_OK) != 0) {
                throw new SourceFileReadingError(String.format("Can not open .pka file: %s", source));
            }
            ActivityFile activityFile = (ActivityFile) ipc.appWindow().getActiveFile();
            // TODO если не может открыть файл, то выводит окно, где нужно нажать ОК

            // Generate password MD5 hash
            List<Integer> challengeKey = activityFile.getChallengeKeyAsInts();
            String hashedPassword = hashPassword(password, challengeKey);

            // Get percentage
            boolean confirmed = activityFile.confirmPassword(hashedPassword);
            if (confirmed) {
                HashMap<String, Object> activityInfo = new HashMap<String, Object>();
                activityInfo.put("name", activityFile.getUserProfile().getName());
                activityInfo.put("email", activityFile.getUserProfile().getEmail());
                activityInfo.put("percentageComplete", activityFile.getPercentageComplete());
                activityInfo.put("percentageCompleteScore", activityFile.getPercentageCompleteScore());
                activityInfo.put("addInfo", activityFile.getUserProfile().getAddInfo());
                activityInfo.put("timeElapsed", activityFile.getTimeElapsed());

                try {
                    saveJSON(activityInfo, target);
                } catch (IOException e) {
                    throw new TargetFileWritingError(e.getMessage(), e);
                }

            } else {
                throw new WrongPasswordError("Wrong password");
            }

            packetTracerSession.close();

        } catch (BaseGraderError e) {
            System.out.println(e.getMessage());
            packetTracerSessionFactory.close();
            System.exit(Constants.EXCEPTION_TO_EXIT_STATUS_MAPPING.get(e.getClass()));
        } catch (Exception e) {
            System.out.println(String.format("Unknown error: %s", e.getMessage()));
            System.exit(Constants.EXIT_STATUS_GENERAL_ERROR);
        }
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

    private static void saveJSON(HashMap<String, Object> activityInfo, String filepath) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(activityInfo);
        FileWriter writer = new FileWriter(filepath);
        writer.write(json);
        writer.flush();
    }
}