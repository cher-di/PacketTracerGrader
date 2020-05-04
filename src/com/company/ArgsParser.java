package com.company;

import org.apache.commons.cli.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

enum ArgsNames {
    FILEPATH("filepath"),
    PASSWORD("password"),
    PORT("port"),
    HOST("host"),
    CONNECTION_ATTEMPTS_NUMBER("attempts");

    private final String argName;

    ArgsNames(String argName) {
        this.argName = argName;
    }

    String getArgName() {
        return argName;
    }
}


public class ArgsParser {
    private final Options options;
    private final CommandLineParser parser;
    private final HelpFormatter formatter;

    private static final Map<ArgsNames, String> DEFAULTS = Map.of(
            ArgsNames.PORT, Integer.toString(Constants.DEFAULT_PORT),
            ArgsNames.HOST, Constants.DEFAULT_HOST,
            ArgsNames.CONNECTION_ATTEMPTS_NUMBER, Integer.toString(Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER)
    );

    ArgsParser() {
        options = new Options();

        options.addOption(Option.builder("f")
                .longOpt("filepath")
                .hasArg(true)
                .desc("Path to activity file")
                .argName(ArgsNames.FILEPATH.getArgName())
                .required(true)
                .type(String.class)
                .build());

        options.addOption(Option.builder("k")
                .longOpt("key")
                .hasArg(true)
                .desc("Password key for activity file")
                .argName(ArgsNames.PASSWORD.getArgName())
                .required(true)
                .type(String.class)
                .build());

        options.addOption(Option.builder("p")
                .longOpt("port")
                .hasArg(true)
                .desc("Port to connect to Packet Tracer")
                .argName(ArgsNames.PORT.getArgName())
                .required(false)
                .type(Number.class)
                .build());

        options.addOption(Option.builder("h")
                .longOpt("host")
                .hasArg(true)
                .desc("Host to connect to Packet Tracer")
                .argName(ArgsNames.HOST.getArgName())
                .required(false)
                .type(String.class)
                .build());

        options.addOption(Option.builder("a")
                .longOpt("attempts")
                .hasArg(true)
                .desc("Number of connection attempts")
                .argName(ArgsNames.CONNECTION_ATTEMPTS_NUMBER.getArgName())
                .required(false)
                .type(Number.class)
                .build());

        parser = new DefaultParser();
        formatter = new HelpFormatter();
    }

    HashMap<ArgsNames, String> parse(String[] args) throws ParseException {
        try {
            CommandLine cmd = parser.parse(options, args);
            return cmdToHashMap(cmd);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("PacketTracerGrader", options);
            throw new ParseException(e.getMessage());
        }
    }

    private HashMap<ArgsNames, String> cmdToHashMap(CommandLine cmd) {
        HashMap<ArgsNames, String> args = new HashMap<ArgsNames, String>();
        for (ArgsNames name : ArgsNames.values()) {
            String argName = name.getArgName();
            String argValue = cmd.getOptionValue(argName);
            if (argValue != null) {
                args.put(name, argValue);
            }
            else {
                args.put(name, DEFAULTS.get(name));
            }
        }
        return args;
    }
}
