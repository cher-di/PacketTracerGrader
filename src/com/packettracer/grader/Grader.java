package com.packettracer.grader;

import com.packettracer.args.ArgsParser;
import com.packettracer.args.exceptions.ArgumentAlreadyExists;
import com.packettracer.args.exceptions.ParseError;
import com.packettracer.args.exceptions.ReturnCodeAlreadyExists;
import com.packettracer.args.parsers.*;
import com.packettracer.grader.exceptions.*;
import org.apache.commons.cli.Option;

import java.util.HashMap;

import static com.packettracer.utils.Utils.*;


class GraderUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Throwable throwable;

    GraderUncaughtExceptionHandler() {
        throwable = null;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getUncaughtException() {
        return throwable;
    }
}


public class Grader {
    private static final String APP_NAME = "Packet Tracer Grader";

    private static final String ARG_NAME_INPUT = "input";
    private static final String ARG_NAME_KEY = "key";
    private static final String ARG_NAME_HOST = "host";
    private static final String ARG_NAME_PORT = "port";
    private static final String ARG_NAME_OUTPUT = "output";
    private static final String ARG_NAME_PRETTY = "pretty";
    private static final String ARG_NAME_CONN_ATTEMPTS = "conn_attempts";
    private static final String ARG_NAME_CONN_DELAY = "conn_delay";
    private static final String ARG_NAME_CHECK_ALIVE_DELAY = "alive_delay";
    private static final String ARG_NAME_CHECK_ALIVE_ATTEMPTS = "alive_attempts";

    public static final Integer RETURN_CODE_GENERAL_ERROR = 1;
    public static final Integer RETURN_CODE_WRONG_CREDENTIALS = 2;
    public static final Integer RETURN_CODE_UNABLE_TO_CONNECT = 3;
    public static final Integer RETURN_CODE_ARGUMENTS_PARSING_FAILED = 4;
    public static final Integer RETURN_CODE_INPUT_FILE_READING_FAILED = 5;
    public static final Integer RETURN_CODE_OUTPUT_FILE_WRITING_FAILED = 6;
    public static final Integer RETURN_CODE_WRONG_PASSWORD = 7;

    private static final String DEFAULT_HOST = "localhost";
    private static final Integer DEFAULT_PORT = 39000;

    private static final Integer DEFAULT_CONN_DELAY = 100;
    private static final Integer MIN_CONN_DELAY = 100;
    private static final Integer MAX_CONN_DELAY = 60000;

    private static final Integer DEFAULT_CONN_ATTEMPTS = 5;
    private static final Integer MIN_CONN_ATTEMPTS = 1;
    private static final Integer MAX_CONN_ATTEMPTS = 20;

    private static final Integer DEFAULT_CHECK_ALIVE_DELAY = 2000;
    private static final Integer MIN_CHECK_ALIVE_DELAY = 100;
    private static final Integer MAX_CHECK_ALIVE_DELAY = 60000;

    private static final Integer DEFAULT_CHECK_ALIVE_ATTEMPTS = 10;
    private static final Integer MIN_CHECK_ALIVE_ATTEMPTS = 1;
    private static final Integer MAX_CHECK_ALIVE_ATTEMPTS = 40;

    public static void main(String[] args) throws Exception {
        ArgsParser parser = makeArgsParser();
        HashMap<String, Object> parsedArgs = null;

        try {
            parsedArgs = parser.parse(args);
        } catch (ParseError e) {
            System.err.println(e.getMessage());
            parser.printHelp();
            System.exit(RETURN_CODE_ARGUMENTS_PARSING_FAILED);
        }

        String input = (String) parsedArgs.get(ARG_NAME_INPUT);
        String output = (String) parsedArgs.get(ARG_NAME_OUTPUT);
        String key = (String) parsedArgs.get(ARG_NAME_KEY);
        String host = (String) parsedArgs.get(ARG_NAME_HOST);
        Integer port = (Integer) parsedArgs.get(ARG_NAME_PORT);
        Integer connAttempts = (Integer) parsedArgs.get(ARG_NAME_CONN_ATTEMPTS);
        Integer connDelay = (Integer) parsedArgs.get(ARG_NAME_CONN_DELAY);
        Boolean pretty = (Boolean) parsedArgs.get(ARG_NAME_PRETTY);
        Integer checkAliveAttempts = (Integer) parsedArgs.get(ARG_NAME_CHECK_ALIVE_ATTEMPTS);
        Integer checkAliveDelay = (Integer) parsedArgs.get(ARG_NAME_CHECK_ALIVE_DELAY);

        try {
            // Grade
            ActivityData activityData = grade(input, key, host, port, connAttempts, connDelay, checkAliveAttempts, checkAliveDelay);

            String json = activityData.toJson(pretty);
            if (output != null)
                writeDataToFile(json, output);
            else
                System.out.println(json);
        } catch (GeneralError e) {
            System.err.println(e.getMessage());
            System.exit(e.getReturnCode());
        } catch (Throwable e) {
            System.err.println(String.format("Unknown error: %s", e.getMessage()));
            System.exit(RETURN_CODE_GENERAL_ERROR);
        }
    }

    private static ActivityData grade(String input, String password, String host, Integer port,
                                      Integer connAttempts, Integer connDelay,
                                      Integer checkAliveAttempts, Integer checkAliveDelay) throws Throwable {
        GraderRunnable runnable = new GraderRunnable(input, password, host, port, connAttempts, connDelay);
        GraderUncaughtExceptionHandler exceptionHandler = new GraderUncaughtExceptionHandler();
        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler(exceptionHandler);
        thread.start();

        for (int i = 0; i < checkAliveAttempts; i++) {
            if (!thread.isAlive())
                break;
            Thread.sleep(checkAliveDelay);
            System.out.println(String.format("Check alive %d", i + 1));
        }
        if (thread.isAlive()) {
            thread.interrupt();
            throw new InputFileReadingError(String.format("PacketTracer not support this type of files: %s", input));
        }

        if (exceptionHandler.getUncaughtException() != null)
            throw exceptionHandler.getUncaughtException();

        return runnable.getActivityData();
    }

    private static ArgsParser makeArgsParser() throws ArgumentAlreadyExists, ReturnCodeAlreadyExists {
        ArgsParser parser = new ArgsParser(APP_NAME);

        parser.addParameter(ARG_NAME_INPUT,
                Option.builder(getFirstLetter(ARG_NAME_INPUT))
                        .longOpt(ARG_NAME_INPUT)
                        .hasArg(true)
                        .desc("Path to activity file")
                        .argName(ARG_NAME_INPUT)
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ARG_NAME_KEY,
                Option.builder(getFirstLetter(ARG_NAME_KEY))
                        .longOpt(ARG_NAME_KEY)
                        .hasArg(true)
                        .desc("Key for activity file")
                        .argName(ARG_NAME_KEY)
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ARG_NAME_OUTPUT,
                Option.builder(getFirstLetter(ARG_NAME_OUTPUT))
                        .longOpt(ARG_NAME_OUTPUT)
                        .hasArg(true)
                        .desc("Path to file to store results. If not specified, print results to stdout")
                        .argName(ARG_NAME_OUTPUT)
                        .required(false)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ARG_NAME_PORT,
                Option.builder(getFirstLetter(ARG_NAME_PORT))
                        .longOpt(ARG_NAME_PORT)
                        .hasArg(true)
                        .desc(String.format("Port to connect to Packet Tracer (default: %d)", DEFAULT_PORT))
                        .argName(ARG_NAME_PORT)
                        .required(false)
                        .type(Number.class)
                        .build(), new PortParser(DEFAULT_PORT));

        parser.addParameter(ARG_NAME_HOST,
                Option.builder(getFirstLetter(ARG_NAME_HOST))
                        .longOpt(ARG_NAME_HOST)
                        .hasArg(true)
                        .desc(String.format("Host to connect to Packet Tracer (default: %s)", DEFAULT_HOST))
                        .argName(ARG_NAME_HOST)
                        .required(false)
                        .type(String.class)
                        .build(), new HostParser(DEFAULT_HOST));

        parser.addParameter(ARG_NAME_CONN_ATTEMPTS,
                Option.builder("ca")
                        .longOpt(ARG_NAME_CONN_ATTEMPTS)
                        .hasArg(true)
                        .desc(String.format("Number of connection attempts in range from %d to %d (default: %d)",
                                MIN_CONN_ATTEMPTS, MAX_CONN_ATTEMPTS, DEFAULT_CONN_ATTEMPTS))
                        .argName(ARG_NAME_CONN_ATTEMPTS)
                        .required(false)
                        .type(Number.class)
                        .build(), new AttemptsParser(MIN_CONN_ATTEMPTS, MAX_CONN_ATTEMPTS, DEFAULT_CONN_ATTEMPTS));

        parser.addParameter(ARG_NAME_CONN_DELAY,
                Option.builder("cd")
                        .longOpt(ARG_NAME_CONN_DELAY)
                        .hasArg(true)
                        .desc(String.format("Delay between connection attempts in milliseconds, %d <= delay <= %d (default: %d)",
                                MIN_CONN_DELAY, MAX_CONN_DELAY, DEFAULT_CONN_DELAY))
                        .argName(ARG_NAME_CONN_DELAY)
                        .required(false)
                        .type(Number.class)
                        .build(), new DelayParser(MIN_CONN_DELAY, MAX_CONN_DELAY, DEFAULT_CONN_DELAY));

        parser.addParameter(ARG_NAME_PRETTY,
                Option.builder(getFirstLetters(ARG_NAME_PRETTY, 2))
                        .longOpt(ARG_NAME_PRETTY)
                        .desc("If specified, grader return json in pretty printing format, otherwise json is returned as one string")
                        .argName(ARG_NAME_PRETTY)
                        .required(false)
                        .build(), new BooleanDefaultFalseParser());

        parser.addParameter(ARG_NAME_CHECK_ALIVE_DELAY,
                Option.builder("ad")
                        .longOpt(ARG_NAME_CHECK_ALIVE_DELAY)
                        .hasArg(true)
                        .desc(String.format("Delay between check alive attempts in milliseconds, %d <= delay <= %d (default: %d)",
                                MIN_CONN_DELAY, MAX_CONN_DELAY, DEFAULT_CONN_DELAY))
                        .argName(ARG_NAME_CHECK_ALIVE_DELAY)
                        .required(false)
                        .type(Number.class)
                        .build(), new DelayParser(MIN_CHECK_ALIVE_DELAY, MAX_CHECK_ALIVE_DELAY, DEFAULT_CHECK_ALIVE_DELAY));


        parser.addParameter(ARG_NAME_CHECK_ALIVE_ATTEMPTS,
                Option.builder("aa")
                        .longOpt(ARG_NAME_CHECK_ALIVE_ATTEMPTS)
                        .hasArg(true)
                        .desc(String.format("Number of check alive attempts in range from %d to %d (default: %d)",
                                MIN_CONN_ATTEMPTS, MAX_CONN_ATTEMPTS, DEFAULT_CONN_ATTEMPTS))
                        .argName(ARG_NAME_CHECK_ALIVE_ATTEMPTS)
                        .required(false)
                        .type(Number.class)
                        .build(), new AttemptsParser(MIN_CHECK_ALIVE_ATTEMPTS, MAX_CHECK_ALIVE_ATTEMPTS, DEFAULT_CHECK_ALIVE_ATTEMPTS));


        parser.addReturnCode(RETURN_CODE_GENERAL_ERROR, "General grader error");
        parser.addReturnCode(RETURN_CODE_ARGUMENTS_PARSING_FAILED, "Arguments parsing failed");
        parser.addReturnCode(RETURN_CODE_INPUT_FILE_READING_FAILED, "An error occurred while reading data from input file");
        parser.addReturnCode(RETURN_CODE_OUTPUT_FILE_WRITING_FAILED, "An error occurred while writing data to output file");
        parser.addReturnCode(RETURN_CODE_UNABLE_TO_CONNECT, "Unable to connect to Packet Tracer");
        parser.addReturnCode(RETURN_CODE_WRONG_PASSWORD, "Wrong password");
        parser.addReturnCode(RETURN_CODE_WRONG_CREDENTIALS, "Wrong credentials");

        return parser;
    }
}