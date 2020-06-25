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
import com.packettracer.grader.args.ArgsParser;
import com.packettracer.grader.args.exceptions.ArgumentAlreadyExists;
import com.packettracer.grader.args.exceptions.ParseError;
import com.packettracer.grader.args.parsers.*;
import com.packettracer.grader.exceptions.*;
import org.apache.commons.cli.Option;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;


public class Grader {
    private enum ArgName {
        SOURCE("source"),
        KEY("key"),
        PORT("port"),
        HOST("host"),
        ATTEMPTS("attempts"),
        TARGET("target"),
        DELAY("delay");

        private final String argName;

        ArgName(String argName) {
            this.argName = argName;
        }

        String getArgName() {
            return argName;
        }

        @Override
        public String toString() {
            return getArgName();
        }
    }

    private static final String APP_NAME = "Packet Tracer Grader";

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
            System.err.println(e.getMessage());
            parser.printHelp();
            System.exit(Constants.ExitStatus.ARGUMENTS_PARSING_FAILED.getReturnCode());
        }

        String source = (String) parsedArgs.get(ArgName.SOURCE.getArgName());
        String target = (String) parsedArgs.get(ArgName.TARGET.getArgName());
        String password = (String) parsedArgs.get(ArgName.KEY.getArgName());
        String host = (String) parsedArgs.get(ArgName.HOST.getArgName());
        Integer port = (Integer) parsedArgs.get(ArgName.PORT.getArgName());
        Integer attempts = (Integer) parsedArgs.get(ArgName.ATTEMPTS.getArgName());
        Integer delay = (Integer) parsedArgs.get(ArgName.DELAY.getArgName());

        try {
            // Grade
            MyRunnable runnable = new MyRunnable(source, password, host, port, attempts, delay);
            Thread thread = new Thread(runnable);
            thread.start();
            Thread.sleep(3000);
            if (thread.isAlive()) {
                thread.interrupt();
                throw new SourceFileReadingError("PacketTracer not support this type of files");
            }
            else {
                ActivityData activityData = runnable.getActivityData();
                Integer returnCode = runnable.getReturnCode();

                if (returnCode != 0) {
                    throw new BaseGraderError("Grader failed with exit code " + returnCode.toString());
                }
                else {
                    // Save data to JSON
                    activityData.toJsonFile(target);
                }
            }
        } catch (BaseGraderError e) {
            System.err.println(e.getMessage());
            System.exit(e.getExitStatus().getReturnCode());
        } catch (Exception e) {
            System.err.println(String.format("Unknown error: %s", e.getMessage()));
            System.exit(Constants.ExitStatus.GENERAL_ERROR.getReturnCode());
        }
    }

    public static ActivityData grade(String source, String password, String host, Integer port, Integer attempts, Integer delay) throws Exception {
        source = formatActivityFilePath(source);

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
            if (!confirmed) {
                throw new WrongPasswordError("Wrong password");
            } else {
                ActivityData activityData = new ActivityData(activityFile);
                packetTracerSession.close();
                return activityData;
            }

        } catch (Exception e) {
            packetTracerSession.close();
            throw e;
        }
    }

    private static String hashPassword(String password, List<Integer> challengeKey) throws NoSuchAlgorithmException {
        var salt = challengeKey.toArray();
        byte[] array = new byte[salt.length];
        for (int i = 0; i < salt.length; i++) {
            array[i] = (byte) (int) salt[i];
        }

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(array);
        messageDigest.update(password.getBytes());
        byte[] digest = messageDigest.digest();
        StringBuilder result = new StringBuilder();
        for (byte b : digest) {
            result.append(Integer.toHexString((0x000000ff & b) | 0xffffff00).substring(6));
        }
        return result.toString().toUpperCase();
    }

    private static String formatActivityFilePath(String filepath) throws SourceFileReadingError {
        try {
            File file = new File(filepath);
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new SourceFileReadingError(e.getMessage(), e);
        }
    }

    private static ArgsParser createArgsParser() throws ArgumentAlreadyExists {
        ArgsParser parser = new ArgsParser(APP_NAME);

        parser.addParameter(ArgName.SOURCE.getArgName(),
                Option.builder("s")
                        .longOpt(ArgName.SOURCE.getArgName())
                        .hasArg(true)
                        .desc("Path to activity file")
                        .argName(ArgName.SOURCE.getArgName())
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ArgName.KEY.getArgName(),
                Option.builder("k")
                        .longOpt(ArgName.KEY.getArgName())
                        .hasArg(true)
                        .desc("Key for activity file")
                        .argName(ArgName.KEY.getArgName())
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ArgName.TARGET.getArgName(),
                Option.builder("t")
                        .longOpt(ArgName.TARGET.getArgName())
                        .hasArg(true)
                        .desc("Path to file to store results")
                        .argName(ArgName.TARGET.getArgName())
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ArgName.PORT.getArgName(),
                Option.builder("p")
                        .longOpt(ArgName.PORT.getArgName())
                        .hasArg(true)
                        .desc(String.format("Port to connect to Packet Tracer (default: %d)", Constants.DEFAULT_PORT))
                        .argName(ArgName.PORT.getArgName())
                        .required(false)
                        .type(Number.class)
                        .build(), new PortParser());

        parser.addParameter(ArgName.HOST.getArgName(),
                Option.builder("h")
                        .longOpt(ArgName.HOST.getArgName())
                        .hasArg(true)
                        .desc(String.format("Host to connect to Packet Tracer (default: %s)", Constants.DEFAULT_HOST))
                        .argName(ArgName.HOST.getArgName())
                        .required(false)
                        .type(String.class)
                        .build(), new HostParser());

        parser.addParameter(ArgName.ATTEMPTS.getArgName(),
                Option.builder("a")
                        .longOpt(ArgName.ATTEMPTS.getArgName())
                        .hasArg(true)
                        .desc(String.format("Number of connection attempts (default: %d)", Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER))
                        .argName(ArgName.ATTEMPTS.getArgName())
                        .required(false)
                        .type(Number.class)
                        .build(), new AttemptsParser());

        parser.addParameter(ArgName.DELAY.getArgName(),
                Option.builder("d")
                        .longOpt(ArgName.DELAY.getArgName())
                        .hasArg(true)
                        .desc(String.format("Delay between connection attempts in milliseconds, %d <= delay <= %d (default: %d)",
                                Constants.MIN_CONNECTION_ATTEMPTS_DELAY, Constants.MAX_CONNECTION_ATTEMPTS_DELAY, Constants.DEFAULT_CONNECTION_ATTEMPTS_DELAY))
                        .argName(ArgName.DELAY.getArgName())
                        .required(false)
                        .type(Number.class)
                        .build(), new DelayParser());

        return parser;
    }
}