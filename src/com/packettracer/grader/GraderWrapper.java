package com.packettracer.grader;

import com.cisco.pt.launcher.PacketTracerLauncher;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.packettracer.grader.args.ArgsParser;
import com.packettracer.grader.args.exceptions.ArgumentAlreadyExists;
import com.packettracer.grader.args.exceptions.ParseError;
import com.packettracer.grader.args.parsers.DefaultParser;
import com.packettracer.grader.args.parsers.HostParser;
import com.packettracer.grader.args.parsers.PortParser;
import com.packettracer.grader.exceptions.BaseGraderError;
import org.apache.commons.cli.Option;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GraderWrapper {
    private enum ArgName {
        INPUT_FILE("input"),
        OUTPUT_FILE("output"),
        HOST("host"),
        PORT("port");

        private final String argName;

        ArgName(String argName) {
            this.argName = argName;
        }

        public String getArgName() {
            return argName;
        }

        @Override
        public String toString() {
            return getArgName();
        }
    }

    private static final Integer DELAY = 500;
    private static final Integer ATTEMPTS = 10;
    private static final String HOST = "localhost";
    private static final Integer SLEEP_AFTER_LAUNCH = 1000 * 10;

    private static class ActivityFileData {
        private final String filepath;
        private final String password;

        ActivityFileData(String filepath, String password) {
            this.filepath = filepath;
            this.password = password;
        }

        ActivityFileData(ActivityFileData other) {
            this.filepath = other.filepath;
            this.password = other.password;
        }
    }

    private static final String appName = "Packet Tracer Grader Wrapper";

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

        String input = (String) parsedArgs.get(ArgName.INPUT_FILE.getArgName());
        String output = (String) parsedArgs.get(ArgName.OUTPUT_FILE.getArgName());
        String host = (String) parsedArgs.get(ArgName.HOST.getArgName());
        Integer port = (Integer) parsedArgs.get(ArgName.PORT.getArgName());

        // Launch PacketTracer
        Process packetTracerProcess = launchNewPacketTracer(port);
        // Sleep after launch to give Packet Tracer time to initialize
        Thread.sleep(SLEEP_AFTER_LAUNCH);

        try {
            // Read data from json file
            List<ActivityFileData> data = readJson(input);

            // Grade
            var extendedActivityDataList = new ArrayList<ExtendedActivityData>();
            for (ActivityFileData activityFileData : data) {
                extendedActivityDataList.add(grade(activityFileData, host, port));
            }

            // Save results to JSON file
            saveJson(extendedActivityDataList, output);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        finally {
            packetTracerProcess.destroy();
        }
    }

    private static ExtendedActivityData grade(ActivityFileData activityFileData, String host, Integer port) throws Exception {
        try {
            ActivityData activityData = Grader.grade(activityFileData.filepath, activityFileData.password, host, port, ATTEMPTS, DELAY);
            return new ExtendedActivityData(activityData, activityFileData.filepath, 0);
        }
        catch (BaseGraderError e) {
            return new ExtendedActivityData(null, activityFileData.filepath, e.getExitStatus().getReturnCode());
        }
        catch (Exception e) {
            return new ExtendedActivityData(null, activityFileData.filepath, Constants.ExitStatus.GENERAL_ERROR.getReturnCode());
        }
    }

    private static List<ActivityFileData> readJson(String filepath) throws FileNotFoundException {
        Type ACTIVITY_FILE_DATA_TYPE = new TypeToken<List<ActivityFileData>>() {}.getType();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filepath));
        return gson.fromJson(reader, ACTIVITY_FILE_DATA_TYPE);
    }

    private static void saveJson(List<ExtendedActivityData> extendedActivityDataList, String filepath) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        String json = gson.toJson(extendedActivityDataList);
        FileWriter writer = new FileWriter(filepath);
        writer.write(json);
        writer.flush();
    }

    private static Process launchNewPacketTracer(Integer port) throws Exception {
        PacketTracerLauncher launcher = PacketTracerLauncher.getInstance();
        return launcher.launch(port, false);
    }

    private static ArgsParser createArgsParser() throws ArgumentAlreadyExists {
        ArgsParser parser = new ArgsParser(appName);

        parser.addParameter(ArgName.INPUT_FILE.getArgName(),
                Option.builder("i")
                        .longOpt(ArgName.INPUT_FILE.getArgName())
                        .hasArg(true)
                        .desc("Path to file with activity files info")
                        .argName(ArgName.INPUT_FILE.getArgName())
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ArgName.OUTPUT_FILE.getArgName(),
                Option.builder("o")
                        .longOpt(ArgName.OUTPUT_FILE.getArgName())
                        .hasArg(true)
                        .desc("Path to file to store results in json")
                        .argName(ArgName.OUTPUT_FILE.getArgName())
                        .required(true)
                        .type(String.class)
                        .build(), new DefaultParser());

        parser.addParameter(ArgName.HOST.getArgName(),
                Option.builder("h")
                        .longOpt(ArgName.HOST.getArgName())
                        .hasArg(true)
                        .desc("PacketTracer server IP address")
                        .argName(ArgName.HOST.getArgName())
                        .required(false)
                        .type(String.class)
                        .build(), new HostParser());

        parser.addParameter(ArgName.PORT.getArgName(),
                Option.builder("p")
                        .longOpt(ArgName.PORT.getArgName())
                        .hasArg(true)
                        .desc("Path to file to store results in json")
                        .argName(ArgName.PORT.getArgName())
                        .required(false)
                        .type(Number.class)
                        .build(), new PortParser());

        return parser;
    }
}
