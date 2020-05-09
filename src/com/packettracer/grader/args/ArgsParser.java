package com.packettracer.grader.args;

import com.packettracer.grader.Constants;
import com.packettracer.grader.exceptions.ParseError;
import org.apache.commons.cli.*;


public class ArgsParser {
    private final Options options;
    private final CommandLineParser parser;
    private final HelpFormatter formatter;

    private static final String ARG_NAME_SOURCE = "source";
    private static final String ARG_NAME_KEY = "key";
    private static final String ARG_NAME_PORT = "port";
    private static final String ARG_NAME_HOST = "host";
    private static final String ARG_NAME_ATTEMPTS = "attempts";
    private static final String ARG_NAME_TARGET = "target";
    private static final String ARG_NAME_DELAY = "delay";

    public ArgsParser() {
        options = new Options();

        options.addOption(Option.builder("s")
                .longOpt(ARG_NAME_SOURCE)
                .hasArg(true)
                .desc("Path to activity file")
                .argName(ARG_NAME_SOURCE)
                .required(true)
                .type(String.class)
                .build());

        options.addOption(Option.builder("k")
                .longOpt(ARG_NAME_KEY)
                .hasArg(true)
                .desc("Key for activity file")
                .argName(ARG_NAME_KEY)
                .required(true)
                .type(String.class)
                .build());

        options.addOption(Option.builder("t")
                .longOpt(ARG_NAME_TARGET)
                .hasArg(true)
                .desc("Path to file to store results")
                .argName(ARG_NAME_TARGET)
                .required(true)
                .type(String.class)
                .build());

        options.addOption(Option.builder("p")
                .longOpt(ARG_NAME_PORT)
                .hasArg(true)
                .desc(String.format("Port to connect to Packet Tracer (default: %d)", Constants.DEFAULT_PORT))
                .argName(ARG_NAME_PORT)
                .required(false)
                .type(Number.class)
                .build());

        options.addOption(Option.builder("h")
                .longOpt(ARG_NAME_HOST)
                .hasArg(true)
                .desc(String.format("Host to connect to Packet Tracer (default: %s)", Constants.DEFAULT_HOST))
                .argName(ARG_NAME_HOST)
                .required(false)
                .type(String.class)
                .build());

        options.addOption(Option.builder("a")
                .longOpt(ARG_NAME_ATTEMPTS)
                .hasArg(true)
                .desc(String.format("Number of connection attempts (default: %d)", Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER))
                .argName(ARG_NAME_ATTEMPTS)
                .required(false)
                .type(Number.class)
                .build());

        options.addOption(Option.builder("d")
                .longOpt(ARG_NAME_DELAY)
                .hasArg(true)
                .desc(String.format("Delay between connection attempts in milliseconds, %d <= delay <= %d (default: %d)",
                        Constants.MIN_CONNECTION_ATTEMPTS_DELAY, Constants.MAX_CONNECTION_ATTEMPTS_DELAY, Constants.DEFAULT_CONNECTION_ATTEMPTS_DELAY))
                .argName(ARG_NAME_DELAY)
                .required(false)
                .type(Number.class)
                .build());

        parser = new DefaultParser();
        formatter = new HelpFormatter();
    }

    public Args parse(String[] args) throws ParseError {
        try {
            var cmd = parser.parse(options, args);
            String filepath = cmd.getOptionValue(ARG_NAME_SOURCE);
            String key = cmd.getOptionValue(ARG_NAME_KEY);
            String port = cmd.getOptionValue(ARG_NAME_PORT);
            String host = cmd.getOptionValue(ARG_NAME_HOST);
            String connectionAttemptsNumber = cmd.getOptionValue(ARG_NAME_ATTEMPTS);
            String target = cmd.getOptionValue(ARG_NAME_TARGET);
            String delay = cmd.getOptionValue(ARG_NAME_DELAY);

            return new Args(filepath, key, target, port, host, connectionAttemptsNumber, delay);
        } catch (ParseException e) {
            throw new ParseError(e.getMessage(), e);
        }
    }

    public void printHelp() {
        formatter.printHelp("PacketTracerGrader", options);
    }
}
