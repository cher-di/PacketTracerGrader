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
import com.packettracer.grader.args.ArgsParser;
import com.packettracer.grader.args.exceptions.ArgumentAlreadyExists;
import com.packettracer.grader.args.exceptions.ParseError;
import com.packettracer.grader.args.parsers.*;
import com.packettracer.grader.exceptions.*;
import org.apache.commons.cli.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Grader {
    private static final String ARG_NAME_SOURCE = "source";
    private static final String ARG_NAME_KEY = "key";
    private static final String ARG_NAME_PORT = "port";
    private static final String ARG_NAME_HOST = "host";
    private static final String ARG_NAME_ATTEMPTS = "attempts";
    private static final String ARG_NAME_TARGET = "target";
    private static final String ARG_NAME_DELAY = "delay";

    private static final Integer EXIT_STATUS_GENERAL_ERROR = 1;
    private static final Integer EXIT_STATUS_ARGUMENTS_PARSING_FAILED = 2;
    private static final Integer EXIT_STATUS_SOURCE_FILE_READING_FAILED = 3;
    private static final Integer EXIT_STATUS_TARGET_FILE_WRITING_FAILED = 4;
    private static final Integer EXIT_STATUS_UNABLE_TO_CONNECT = 5;
    private static final Integer EXIT_STATUS_WRONG_PASSWORD = 6;
    private static final Integer EXIT_STATUS_WRONG_CREDENTIALS = 7;

    private static final Map<Class<? extends BaseGraderError>, Integer> EXCEPTION_TO_EXIT_STATUS_MAPPING = Map.of(
            BaseGraderError.class, EXIT_STATUS_GENERAL_ERROR,
            ParseError.class, EXIT_STATUS_ARGUMENTS_PARSING_FAILED,
            SourceFileReadingError.class, EXIT_STATUS_SOURCE_FILE_READING_FAILED,
            TargetFileWritingError.class, EXIT_STATUS_TARGET_FILE_WRITING_FAILED,
            ConnectionError.class, EXIT_STATUS_UNABLE_TO_CONNECT,
            WrongPasswordError.class, EXIT_STATUS_WRONG_PASSWORD,
            WrongCredentialsError.class, EXIT_STATUS_WRONG_CREDENTIALS
    );

    private static final String AUTH_SECRET = "cisco";
    private static final String AUTH_APP = "com.packettracer.grader";

    public Grader(PacketTracerSession session) {
    }

    public static void main(String[] args) throws Exception {

        ArgsParser parser = createArgsParser();
        HashMap<String, Object> parsedArgs = null;

        try {
            parsedArgs = parser.parse(args);
        } catch (ParseError e) {
            System.out.println(e.getMessage());
            parser.printHelp();
            System.exit(EXIT_STATUS_ARGUMENTS_PARSING_FAILED);
        }

        String source = (String) parsedArgs.get(ARG_NAME_SOURCE);
        String target = (String) parsedArgs.get(ARG_NAME_TARGET);
        String password = (String) parsedArgs.get(ARG_NAME_KEY);
        String host = (String) parsedArgs.get(ARG_NAME_HOST);
        Integer port = (Integer) parsedArgs.get(ARG_NAME_PORT);
        Integer attempts = (Integer) parsedArgs.get(ARG_NAME_ATTEMPTS);
        Integer delay = (Integer) parsedArgs.get(ARG_NAME_DELAY);

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
            System.exit(EXCEPTION_TO_EXIT_STATUS_MAPPING.get(e.getClass()));
        } catch (Exception e) {
            System.out.println(String.format("Unknown error: %s", e.getMessage()));
            System.exit(EXIT_STATUS_GENERAL_ERROR);
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

    private static ArgsParser createArgsParser() throws ArgumentAlreadyExists {
        ArgsParser parser = new ArgsParser();

        parser.addParameter(ARG_NAME_SOURCE,
                Option.builder("s")
                        .longOpt(ARG_NAME_SOURCE)
                        .hasArg(true)
                        .desc("Path to activity file")
                        .argName(ARG_NAME_SOURCE)
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ARG_NAME_KEY,
                Option.builder("k")
                        .longOpt(ARG_NAME_KEY)
                        .hasArg(true)
                        .desc("Key for activity file")
                        .argName(ARG_NAME_KEY)
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ARG_NAME_TARGET,
                Option.builder("t")
                        .longOpt(ARG_NAME_TARGET)
                        .hasArg(true)
                        .desc("Path to file to store results")
                        .argName(ARG_NAME_TARGET)
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ARG_NAME_PORT,
                Option.builder("p")
                        .longOpt(ARG_NAME_PORT)
                        .hasArg(true)
                        .desc(String.format("Port to connect to Packet Tracer (default: %d)", Constants.DEFAULT_PORT))
                        .argName(ARG_NAME_PORT)
                        .required(false)
                        .type(Number.class)
                        .build(), new PortParser());

        parser.addParameter(ARG_NAME_HOST,
                Option.builder("h")
                        .longOpt(ARG_NAME_HOST)
                        .hasArg(true)
                        .desc(String.format("Host to connect to Packet Tracer (default: %s)", Constants.DEFAULT_HOST))
                        .argName(ARG_NAME_HOST)
                        .required(false)
                        .type(String.class)
                        .build(), new HostParser());

        parser.addParameter(ARG_NAME_ATTEMPTS,
                Option.builder("a")
                        .longOpt(ARG_NAME_ATTEMPTS)
                        .hasArg(true)
                        .desc(String.format("Number of connection attempts (default: %d)", Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER))
                        .argName(ARG_NAME_ATTEMPTS)
                        .required(false)
                        .type(Number.class)
                        .build(), new AttemptsParser());

        parser.addParameter(ARG_NAME_DELAY,
                Option.builder("d")
                        .longOpt(ARG_NAME_DELAY)
                        .hasArg(true)
                        .desc(String.format("Delay between connection attempts in milliseconds, %d <= delay <= %d (default: %d)",
                                Constants.MIN_CONNECTION_ATTEMPTS_DELAY, Constants.MAX_CONNECTION_ATTEMPTS_DELAY, Constants.DEFAULT_CONNECTION_ATTEMPTS_DELAY))
                        .argName(ARG_NAME_DELAY)
                        .required(false)
                        .type(Number.class)
                        .build(), new DelayParser());

        return parser;
    }
}