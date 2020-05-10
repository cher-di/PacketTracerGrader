package com.packettracer.grader.args;

import com.cisco.pt.util.Pair;
import com.packettracer.grader.args.exceptions.ArgumentAlreadyExists;
import com.packettracer.grader.args.exceptions.ParseError;
import org.apache.commons.cli.*;

import java.util.HashMap;


public class ArgsParser {
    private final Options options;
    private final CommandLineParser parser;
    private final HelpFormatter formatter;
    private final HashMap<String, Pair<String, Parser>> arguments;

    public ArgsParser() {
        arguments = new HashMap<>();
        options = new Options();
        parser = new DefaultParser();
        formatter = new HelpFormatter();
    }

    public HashMap<String, Object> parse(String[] args) throws ParseError {
        try {
            var cmd = parser.parse(options, args);
            HashMap<String, Object> result = new HashMap<>();
            for (String argName : arguments.keySet()) {
                Pair<String, Parser> pair = arguments.get(argName);
                String arg = pair.getFirst();
                Parser parser = pair.getSecond();
                System.out.println(String.format("%s: %s", argName, cmd.getOptionValue(arg)));
                System.out.println(parser.getClass());
                var parsed_arg = parser.parse(cmd.getOptionValue(arg));
                result.put(argName, parsed_arg);
            }
            return result;
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
            arguments.put(argName, new Pair<>(option.getArgName(), parser));
            options.addOption(option);
        }
    }
}
