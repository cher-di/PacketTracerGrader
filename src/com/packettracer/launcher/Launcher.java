package com.packettracer.launcher;

import com.cisco.pt.launcher.PacketTracerLaunchException;
import com.packettracer.args.ArgsParser;
import com.packettracer.args.exceptions.ArgumentAlreadyExists;
import com.packettracer.args.exceptions.ParseError;
import com.packettracer.args.exceptions.ReturnCodeAlreadyExists;
import com.packettracer.args.parsers.BooleanDefaultFalseParser;
import com.packettracer.args.parsers.PortParser;
import com.packettracer.grader.exceptions.ConnectionError;
import com.packettracer.utils.Session;
import org.apache.commons.cli.Option;

import java.util.HashMap;

import static com.packettracer.utils.Utils.getFirstLetter;
import static com.packettracer.utils.Utils.launchPacketTracer;

public class Launcher {
    private static final String APP_NAME = "Packet Tracer Launcher";

    private static final String ARG_NAME_PORT = "port";
    private static final String ARG_NAME_NOGUI = "nogui";

    private static final Integer RETURN_CODE_ARGS_PARSING_ERROR = 1;
    private static final Integer RETURN_CODE_LAUNCH_ERROR = 2;
    private static final Integer RETURN_CODE_LISTENING_SAME_PORT_PROCESS_ALREADY_EXISTS = 3;

    private static final Integer ATTEMPTS = 3;
    private static final Integer DELAY = 500;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws Exception {
        ArgsParser parser = makeArgsParser();
        HashMap<String, Object> parsedArgs = null;

        try {
            parsedArgs = parser.parse(args);
        } catch (ParseError e) {
            System.err.println(e.getMessage());
            parser.printHelp();
            System.exit(RETURN_CODE_ARGS_PARSING_ERROR);
        }

        Integer port = (Integer) parsedArgs.get(ARG_NAME_PORT);
        Boolean noGui = (Boolean) parsedArgs.get(ARG_NAME_NOGUI);

        try {
            // Check, if there if one existent Packet Tracer process listening this port
            new Session(HOST, port, ATTEMPTS, DELAY);
        } catch (ConnectionError connectionError) {
            // No Packet Tracer processes listening same port
            try {
                launchPacketTracer(port, noGui);
                System.exit(0);
            } catch (PacketTracerLaunchException launchException) {
                System.err.println(launchException.getMessage());
                System.exit(RETURN_CODE_LAUNCH_ERROR);
            }
        }
        // There is another Packet Tracer process listening same port
        System.err.println(String.format("There is another Packet Tracer process listening same port: %d", port));
        System.exit(RETURN_CODE_LISTENING_SAME_PORT_PROCESS_ALREADY_EXISTS);
    }

    private static ArgsParser makeArgsParser() throws ArgumentAlreadyExists, ReturnCodeAlreadyExists {
        ArgsParser parser = new ArgsParser(APP_NAME);

        parser.addParameter(ARG_NAME_PORT,
                Option.builder(getFirstLetter(ARG_NAME_PORT))
                        .longOpt(ARG_NAME_PORT)
                        .hasArg(true)
                        .desc("Port to connect to Packet Tracer via IPC")
                        .argName(ARG_NAME_PORT)
                        .required(false)
                        .type(String.class)
                        .build(), new PortParser());

        parser.addParameter(ARG_NAME_NOGUI,
                Option.builder(getFirstLetter(ARG_NAME_NOGUI))
                        .longOpt(ARG_NAME_NOGUI)
                        .hasArg(false)
                        .desc("If specified, Packet Tracer is launched without GUI, otherwise with GUI")
                        .argName(ARG_NAME_NOGUI)
                        .required(false)
                        .build(), new BooleanDefaultFalseParser());

        parser.addReturnCode(RETURN_CODE_ARGS_PARSING_ERROR, "Arguments parsing error");
        parser.addReturnCode(RETURN_CODE_LAUNCH_ERROR, "An error occurred while launching Packet Tracer");
        parser.addReturnCode(RETURN_CODE_LISTENING_SAME_PORT_PROCESS_ALREADY_EXISTS, "Packet Tracer process, listening the same port, already exists");

        return parser;
    }
}
