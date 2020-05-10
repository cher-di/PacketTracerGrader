package com.packettracer.grader.args;

import com.cisco.pt.util.Pair;
import com.packettracer.grader.Constants;
import com.packettracer.grader.exceptions.ArgumentAlreadyExists;
import com.packettracer.grader.exceptions.ParseError;
import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.function.Function;

import static com.packettracer.grader.Constants.ARG_NAME_DELAY;
import static com.packettracer.grader.Constants.ARG_NAME_TARGET;



public class ArgsParser {
    private final Options options;
    private final CommandLineParser parser;
    private final HelpFormatter formatter;
    private final HashMap<String, Parser> arguments;

    public ArgsParser() {
        arguments = new HashMap<>();
        options = new Options();
        parser = new DefaultParser();
        formatter = new HelpFormatter();
    }

    public HashMap<String, Object> parse(String[] args) throws ParseError {
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

    public void addParameter(String argName, Option option, Parser parser) throws ArgumentAlreadyExists {
        if (arguments.containsKey(argName)) {
            throw new ArgumentAlreadyExists(argName);
        }
        else {
            arguments.put(argName, parser);
        }
    }
}
