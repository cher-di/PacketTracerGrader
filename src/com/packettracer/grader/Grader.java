package com.packettracer.grader;

import com.packettracer.Constants;
import com.packettracer.args.ArgsParser;
import com.packettracer.args.exceptions.ArgumentAlreadyExists;
import com.packettracer.args.exceptions.ParseError;
import com.packettracer.args.parsers.*;
import com.packettracer.grader.exceptions.*;
import org.apache.commons.cli.Option;

import java.util.HashMap;

import static com.packettracer.utils.Utils.getFirstLetter;


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
    private static final String ARG_NAME_KEY = "ket";
    private static final String ARG_NAME_HOST = "host";
    private static final String ARG_NAME_PORT = "port";
    private static final String ARG_NAME_ATTEMPTS = "attempts";
    private static final String ARG_NAME_OUTPUT = "output";
    private static final String ARG_NAME_DELAY = "delay";

    private static final Integer CHECK_ALIVE_TIME_DELTA = 500;

    public static final Integer GENERAL_ERROR = 1;
    public static final Integer ARGUMENTS_PARSING_FAILED = 2;
    public static final Integer INPUT_FILE_READING_FAILED = 3;
    public static final Integer OUTPUT_FILE_WRITING_FAILED = 4;
    public static final Integer UNABLE_TO_CONNECT = 5;
    public static final Integer WRONG_PASSWORD = 6;
    public static final Integer WRONG_CREDENTIALS = 7;

    public static void main(String[] args) throws Exception {
        ArgsParser parser = makeArgsParser();
        HashMap<String, Object> parsedArgs = null;

        try {
            parsedArgs = parser.parse(args);
        } catch (ParseError e) {
            System.err.println(e.getMessage());
            parser.printHelp();
            System.exit(ARGUMENTS_PARSING_FAILED);
        }

        String input = (String) parsedArgs.get(ARG_NAME_INPUT);
        String output = (String) parsedArgs.get(ARG_NAME_OUTPUT);
        String key = (String) parsedArgs.get(ARG_NAME_KEY);
        String host = (String) parsedArgs.get(ARG_NAME_HOST);
        Integer port = (Integer) parsedArgs.get(ARG_NAME_PORT);
        Integer attempts = (Integer) parsedArgs.get(ARG_NAME_ATTEMPTS);
        Integer delay = (Integer) parsedArgs.get(ARG_NAME_DELAY);

        try {
            // Grade
            ActivityData activityData = grade(input, key, host, port, attempts, delay);

            // Save data to JSON
            activityData.toJsonFile(output);
        } catch (GeneralError e) {
            System.err.println(e.getMessage());
            System.exit(e.getReturnCode());
        } catch (Throwable e) {
            System.err.println(String.format("Unknown error: %s", e.getMessage()));
            System.exit(GENERAL_ERROR);
        }
    }

    private static ActivityData grade(String input, String password, String host, Integer port, Integer attempts, Integer delay) throws Throwable {
        GraderRunnable runnable = new GraderRunnable(input, password, host, port, attempts, delay);
        GraderUncaughtExceptionHandler exceptionHandler = new GraderUncaughtExceptionHandler();
        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler(exceptionHandler);
        thread.start();

        for (int i = 0; i < delay * attempts / CHECK_ALIVE_TIME_DELTA + 2; i++) {
            Thread.sleep(CHECK_ALIVE_TIME_DELTA);
            if (!thread.isAlive())
                break;
        }
        if (thread.isAlive()) {
            thread.interrupt();
            throw new InputFileReadingError(String.format("PacketTracer not support this type of files: %s", input));
        }

        if (exceptionHandler.getUncaughtException() != null)
            throw exceptionHandler.getUncaughtException();

        return runnable.getActivityData();
    }

    private static ArgsParser makeArgsParser() throws ArgumentAlreadyExists {
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
                        .desc("Path to file to store results")
                        .argName(ARG_NAME_OUTPUT)
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ARG_NAME_PORT,
                Option.builder(getFirstLetter(ARG_NAME_PORT))
                        .longOpt(ARG_NAME_PORT)
                        .hasArg(true)
                        .desc(String.format("Port to connect to Packet Tracer (default: %d)", Constants.DEFAULT_PORT))
                        .argName(ARG_NAME_PORT)
                        .required(false)
                        .type(Number.class)
                        .build(), new PortParser());

        parser.addParameter(ARG_NAME_HOST,
                Option.builder(getFirstLetter(ARG_NAME_HOST))
                        .longOpt(ARG_NAME_HOST)
                        .hasArg(true)
                        .desc(String.format("Host to connect to Packet Tracer (default: %s)", Constants.DEFAULT_HOST))
                        .argName(ARG_NAME_HOST)
                        .required(false)
                        .type(String.class)
                        .build(), new HostParser());

        parser.addParameter(ARG_NAME_ATTEMPTS,
                Option.builder(getFirstLetter(ARG_NAME_ATTEMPTS))
                        .longOpt(ARG_NAME_ATTEMPTS)
                        .hasArg(true)
                        .desc(String.format("Number of connection attempts (default: %d)", Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER))
                        .argName(ARG_NAME_ATTEMPTS)
                        .required(false)
                        .type(Number.class)
                        .build(), new AttemptsParser());

        parser.addParameter(ARG_NAME_DELAY,
                Option.builder(getFirstLetter(ARG_NAME_DELAY))
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