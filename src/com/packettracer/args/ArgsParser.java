package com.packettracer.args;

import com.cisco.pt.util.Pair;
import com.packettracer.args.exceptions.ArgumentAlreadyExists;
import com.packettracer.args.exceptions.ParseError;
import com.packettracer.args.exceptions.ReturnCodeAlreadyExists;
import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.Map;


public class ArgsParser {
    private final Options options;
    private final CommandLineParser parser;
    private final HelpFormatter formatter;
    private final HashMap<String, Pair<String, Parser>> arguments;
    private final HashMap<Integer, String> returnCodes;
    private final String appName;

    public ArgsParser(String appName) {
        arguments = new HashMap<>();
        returnCodes = new HashMap<>();
        options = new Options();
        parser = new DefaultParser();
        formatter = new HelpFormatter();
        this.appName = appName;
    }

    public HashMap<String, Object> parse(String[] args) throws ParseError {
        try {
            var cmd = parser.parse(options, args);
            HashMap<String, Object> result = new HashMap<>();
            for (String argName : arguments.keySet()) {
                Pair<String, Parser> pair = arguments.get(argName);
                String arg = pair.getFirst();
                Parser parser = pair.getSecond();

                Object optionValue = options.getOption(arg).hasArg() ? cmd.getOptionValue(arg) : cmd.hasOption(arg);
                result.put(argName, parser.parse(optionValue));
            }
            return result;
        } catch (ParseException e) {
            throw new ParseError(e.getMessage(), e);
        }
    }

    public void printHelp() {
        formatter.printHelp(appName, options);
        printReturnCodes();
    }

    private void printReturnCodes() {
        System.out.println("\nReturn codes:");
        for (Map.Entry<Integer, String> entry: returnCodes.entrySet())
            System.out.println(String.format("%d: %s", entry.getKey(), entry.getValue()));
    }

    public void addParameter(String argName, Option option, Parser parser) throws ArgumentAlreadyExists {
        if (arguments.containsKey(argName))
            throw new ArgumentAlreadyExists(argName);
        else {
            arguments.put(argName, new Pair<>(option.getArgName(), parser));
            options.addOption(option);
        }
    }

    public void addReturnCode(Integer returnCode, String description) throws ReturnCodeAlreadyExists {
        if (returnCodes.containsKey(returnCode))
            throw new ReturnCodeAlreadyExists(returnCode);
        else
            returnCodes.put(returnCode, description);
    }
}
