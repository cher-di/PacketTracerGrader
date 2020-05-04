package com.packettracer.grader.args;

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
                .desc("Port to connect to Packet Tracer")
                .argName(ARG_NAME_PORT)
                .required(false)
                .type(Number.class)
                .build());

        options.addOption(Option.builder("h")
                .longOpt(ARG_NAME_HOST)
                .hasArg(true)
                .desc("Host to connect to Packet Tracer")
                .argName(ARG_NAME_HOST)
                .required(false)
                .type(String.class)
                .build());

        options.addOption(Option.builder("a")
                .longOpt(ARG_NAME_ATTEMPTS)
                .hasArg(true)
                .desc("Number of connection attempts")
                .argName(ARG_NAME_ATTEMPTS)
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

            return new Args(filepath, key, target, port, host, connectionAttemptsNumber);
        } catch (ParseException e) {
            throw new ParseError(e.getMessage(), e);
        }
    }

    public void printHelp() {
        formatter.printHelp("PacketTracerGrader", options);
    }
}
