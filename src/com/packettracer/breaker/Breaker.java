package com.packettracer.breaker;

import com.packettracer.Constants;
import com.packettracer.args.ArgsParser;
import com.packettracer.args.exceptions.ArgumentAlreadyExists;
import com.packettracer.args.exceptions.ParseError;
import com.packettracer.args.parsers.HostParser;
import com.packettracer.args.parsers.PortParser;
import com.packettracer.grader.exceptions.GeneralError;
import com.packettracer.utils.Session;
import org.apache.commons.cli.Option;

import java.util.HashMap;

import static com.packettracer.utils.Utils.*;

public class Breaker {
    private static final String APP_NAME = "Packet Tracer Launcher";

    private static final String ARG_NAME_PORT = "port";
    private static final String ARG_NAME_HOST = "host";

    private static final Integer RETURN_CODE_ARGS_PARSING_ERROR = 1;
    private static final Integer UNABLE_TO_CONNECT = 2;
    private static final Integer GENERAL_ERROR = 3;

    private static final Integer ATTEMPTS = 10;
    private static final Integer DELAY = 500;

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
        String host = (String) parsedArgs.get(ARG_NAME_HOST);

        try {
            Session session = new Session(host, port, ATTEMPTS, DELAY);

            session.getIpc().appWindow().exitNoConfirm(true);
            session.close();
        } catch (GeneralError e) {
            System.err.println(e.getMessage());
            System.exit(e.getReturnCode());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(GENERAL_ERROR);
        }
    }

    private static ArgsParser makeArgsParser() throws ArgumentAlreadyExists {
        ArgsParser parser = new ArgsParser(APP_NAME);

        parser.addParameter(ARG_NAME_PORT,
                Option.builder(getFirstLetter(ARG_NAME_PORT))
                        .longOpt(ARG_NAME_PORT)
                        .hasArg(true)
                        .desc("Port to connect to Packet tracer via IPC")
                        .argName(ARG_NAME_PORT)
                        .required(false)
                        .type(String.class)
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

        return parser;
    }
}
